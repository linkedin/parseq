package com.linkedin.parseq.lambda;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.noneOf;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import com.linkedin.parseq.TaskDescriptor;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;


/**
 * An ASM based implementation of {@link TaskDescriptor} to provide description for generated Lambda class.
 * Description of Lambda expression includes source code location of lambda, function call or method reference
 * within lambda.
 */
public class ASMBasedTaskDescriptor implements TaskDescriptor {

  private static final ConcurrentMap<String, String> NAMES = new ConcurrentHashMap<>();
  private static final AtomicReference<CountDownLatch> LATCH_REF = new AtomicReference<>();
  private static final AtomicInteger COUNT = new AtomicInteger();
  // Dynamically allow downsizing of threads, never increase more than CPU due to analysis being CPU intensive
  private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0,
      Runtime.getRuntime().availableProcessors(),
      5,
      TimeUnit.SECONDS,
      new LinkedBlockingQueue<>());

  public static class AnalyzerAdvice {

    /*
     * We invoke the analyze(byte[] byteCode, ClassLoader loader) method through reflection
     * so that it can be executed in the context of the Analyzer class's ClassLoader.
     * Without it the Platform ClassLoader would have to have all dependencies such as
     * asm injected to it.
     */
    public static Method _method;

    @Advice.OnMethodExit
    static void onExit(@Advice.Argument(0) Class<?> hostClass, @Advice.Argument(1) byte[] bytecode,
        @Advice.Return Class<?> definedClass) {
      try {
        _method.invoke(null, bytecode, hostClass.getClassLoader());
      } catch (Throwable t) {
        t.printStackTrace();
        /*
         * We want to continue. We can't afford to throw an exception in such a critical point.
         * The application should still execute correctly even though lambda names are not improved.
         */
      }
    }
  }

  static {

    try {
      Instrumentation inst = ByteBuddyAgent.install();

      /*
       * If we can get the instance of jdk.internal.misc.Unsafe then we will
       * attempt to instrument Unsafe.defineAnonymousClass(...) to capture classes
       * generated for lambdas.
       * This approach does not work for Oracle Java 8 because
       * sun.misc.Unsafe.defineAnonymousClass(...) is a native method and we can
       * at most replace it but there is no reasonably easy way to replace it and
       * still invoke the original method.
       */
      boolean isJdkUnsafe = false;
      Class<?> unsafe = null;
      try {
        unsafe = Class.forName("jdk.internal.misc.Unsafe");
        isJdkUnsafe = true;
      } catch (ClassNotFoundException e) {
      }

      if (isJdkUnsafe) {
        // Code path that supports OpenJDK Java 11 and up

        /*
         * Inject AnalyzerAdvice to boot ClassLoader.
         * It has to be reachable from jdk.internal.misc.Unsafe.
         */
        ClassInjector.UsingUnsafe.ofBootLoader()
            .inject(Collections.singletonMap(new TypeDescription.ForLoadedType(AnalyzerAdvice.class),
                ClassFileLocator.ForClassLoader.read(AnalyzerAdvice.class)));

        /*
         * Inject the analyze(byte[] byteCode, ClassLoader loader) method from this ClassLoader
         * to the AnalyzerAdvice class from boot ClassLoader.
         */
        Class<?> injectedInt = ClassLoader.getSystemClassLoader().getParent().loadClass(AnalyzerAdvice.class.getName());
        injectedInt.getField("_method")
            .set(null, Analyzer.class.getDeclaredMethod("analyze", byte[].class, ClassLoader.class));

        JavaModule module = JavaModule.ofType(injectedInt);

        new AgentBuilder.Default().disableClassFormatChanges()
            .ignore(noneOf(unsafe))
            .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
            .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
            .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
            .with(AgentBuilder.InjectionStrategy.UsingUnsafe.INSTANCE)
            .assureReadEdgeTo(inst, module)
            .type(is(unsafe))
            .transform(new AgentBuilder.Transformer() {
              @Override
              public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader,
                  JavaModule module) {
                return builder.visit(Advice.to(AnalyzerAdvice.class).on(ElementMatchers.named("defineAnonymousClass")));
              }
            })
            .installOnByteBuddyAgent();
      } else {
        // Code path that supports Oracle Java 8 and 9
        inst.addTransformer(new Analyzer());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getDescription(String className) {
    Optional<String> lambdaClassDescription = getLambdaClassDescription(className);
    return lambdaClassDescription.orElse(className);
  }

  Optional<String> getLambdaClassDescription(String className) {
    int slashIndex = className.lastIndexOf('/');
    // If we can't find the slash, we can't find the name of the lambda.
    if (slashIndex <= 0) {
      return Optional.empty();
    }
    String name = className.substring(0, slashIndex);
    String description = NAMES.get(name);

    // If we have already analyzed the class, we don't need to await
    // analysis on other lambdas.
    if (description != null) {
      return Optional.of(description).filter(s -> !s.isEmpty());
    }

    CountDownLatch latch = LATCH_REF.get();
    if (latch != null) {
      try {
        // We wait up to one minute - an arbitrary, sufficiently large amount of time.
        // The wait period must be bounded to avoid locking out JVM.
        latch.await(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        System.err.println("ERROR: ParSeq Latch timed out suggesting serious issue in ASMBasedTaskDescriptor. "
            + "Current number of class being analyzed: " + COUNT.get());
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }

    // Try again
    return Optional.ofNullable(NAMES.get(name)).filter(s -> !s.isEmpty());
  }

  private static void add(String lambdaClassName, String description) {
    NAMES.put(lambdaClassName, description);
  }

  public static class Analyzer implements ClassFileTransformer {

    /**
     * Defining this class as not anonymous to avoid analyzing the runnable that is created to
     * perform analysis. Without this, it is easy for an infinite loop to occur when using a lambda,
     * which would result in a {@link StackOverflowError}, because when performing an analysis of the lambda
     * would then require a new analysis of a new lambda.
     *
     * TODO: Avoid analyzing anonymous classes unrelated to parseq
     */
    static class AnalyzerRunnable implements Runnable {
      private final byte[] byteCode;
      private final ClassLoader loader;
      private final Exception e;

      private AnalyzerRunnable(byte[] byteCode, ClassLoader loader, Exception e) {
        this.byteCode = byteCode;
        this.loader = loader;
        this.e = e;
      }

      public static AnalyzerRunnable of(byte[] byteCode, ClassLoader loader, Exception e) {
        return new AnalyzerRunnable(byteCode, loader, e);
      }

      @Override
      public void run() {
        try {
          doAnalyze(byteCode, loader, e);
        } catch (Throwable t) {
          /*
           * We need to catch  everything because other
           * threads may be blocked on CountDownLatch.
           */
          System.out.println("WARNING: Parseq cannot doAnalyze");
          t.printStackTrace();
        }
        if (COUNT.decrementAndGet() == 0) {
          CountDownLatch latch = LATCH_REF.getAndSet(null);
          latch.countDown();
        }

      }

      public static void doAnalyze(byte[] byteCode, ClassLoader loader, Exception exception) {
        ClassReader reader = new ClassReader(byteCode);
        LambdaClassLocator cv = new LambdaClassLocator(Opcodes.ASM7, loader, exception);
        reader.accept(cv, 0);
        if (cv.isLambdaClass()) {
          LambdaClassDescription lambdaClassDescription = cv.getLambdaClassDescription();
          add(lambdaClassDescription.getClassName(), lambdaClassDescription.getDescription());
        }
      }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
      if (className == null && loader != null) {
        analyze(classfileBuffer, loader);
      }
      return classfileBuffer;
    }

    public static void analyze(byte[] byteCode, ClassLoader loader) {
      if (COUNT.getAndIncrement() == 0) {
        CountDownLatch latch = new CountDownLatch(1);
        while (!LATCH_REF.compareAndSet(null, latch)) {
          /*
           * Busy spin. If we got here it means that other thread just
           * decremented _count to 0 and is about to null out _latchRef.
           * We need to wait for it to happen in order to avoid our
           * newly created CountDownLatch to be overwritten.
           */
        }
      }
      final Exception e = new Exception();
      EXECUTOR_SERVICE.submit(AnalyzerRunnable.of(byteCode, loader, e));
    }
  }
}

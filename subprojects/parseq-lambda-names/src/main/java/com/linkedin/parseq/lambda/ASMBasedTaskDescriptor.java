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
import java.util.concurrent.ForkJoinPool;
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

  private static final ConcurrentMap<String, String> _names = new ConcurrentHashMap<>();
  private static final AtomicReference<CountDownLatch> _latchRef = new AtomicReference<CountDownLatch>();
  private static final AtomicInteger _count = new AtomicInteger();

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
	          .inject(Collections.singletonMap(
	                  new TypeDescription.ForLoadedType(AnalyzerAdvice.class),
	                  ClassFileLocator.ForClassLoader.read(AnalyzerAdvice.class)
	              )
	          );

	      /*
	       * Inject the analyze(byte[] byteCode, ClassLoader loader) method from this ClassLoader
	       * to the AnalyzerAdvice class from boot ClassLoader.
	       */
	      Class<?> injectedInt = ClassLoader.getSystemClassLoader().getParent().loadClass(AnalyzerAdvice.class.getName());
	      injectedInt.getField("_method").set(null, Analyzer.class.getDeclaredMethod("analyze", byte[].class, ClassLoader.class));

	      JavaModule module = JavaModule.ofType(injectedInt);

	      new AgentBuilder.Default()
	        .disableClassFormatChanges()
	        .ignore(noneOf(unsafe))
	        .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
	        .with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
	        .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
	        .with(AgentBuilder.InjectionStrategy.UsingUnsafe.INSTANCE)
	        .assureReadEdgeTo(inst, module)
	        .type(is(unsafe))
	        .transform(new AgentBuilder.Transformer() {
	          @Override
	          public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
	              ClassLoader classLoader, JavaModule module) {
	            return builder.visit(Advice.to(AnalyzerAdvice.class).on(ElementMatchers.named("defineAnonymousClass")));
	          }
	        }).installOnByteBuddyAgent();
			} else {
			  // Code path that supports Oracle Java 8 and 9
        inst.addTransformer(new Analyzer());
			}
	  } catch(Exception e) {
		  e.printStackTrace();
	  }
  }

  @Override
  public String getDescription(String className) {
    Optional<String> lambdaClassDescription = getLambdaClassDescription(className);
    if (lambdaClassDescription.isPresent()) {
      return lambdaClassDescription.get();
    } else {
      return className;
    }
  }

  Optional<String> getLambdaClassDescription(String className) {
    CountDownLatch latch = _latchRef.get();
    if (latch != null) {
      try {
        /*
         * We wait up to one minute - an arbitrary, sufficiently large amount of time.
         * The wait period must be bounded to avoid locking out JVM.
         */
        latch.await(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        System.out.println("ERROR: ParSeq Latch timed out suggesting serious issue in ASMBasedTaskDescriptor. "
            + "Current number of class being analyzed: " + String.valueOf(_count.get()));
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
    int slashIndex = className.lastIndexOf('/');
    if (slashIndex > 0) {
      String name = className.substring(0, slashIndex);
      String desc = _names.get(name);
      if (desc == null || desc.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(desc);
      }
    }

    return Optional.empty();
  }

  private static void add(String lambdaClassName, String description) {
    _names.put(lambdaClassName, description);
  }

  public static class Analyzer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain, byte[] classfileBuffer)
        throws IllegalClassFormatException {
      if (className == null && loader != null) {
        analyze(classfileBuffer, loader);
      }
      return classfileBuffer;
    }

    public static void analyze(byte[] byteCode, ClassLoader loader) {
      if (_count.getAndIncrement() == 0) {
        CountDownLatch latch = new CountDownLatch(1);
        while (!_latchRef.compareAndSet(null, latch)) {
          /*
           * Busy spin. If we got here it means that other thread just
           * decremented _count to 0 and is about to null out _latchRef.
           * We need to wait for it to happen in order to avoid our
           * newly created CountDownLatch to be overwritten.
           */
        }
      }
      final Exception e = new Exception();
      ForkJoinPool.commonPool().execute(new Runnable() {
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
          if (_count.decrementAndGet() == 0) {
            CountDownLatch latch = _latchRef.getAndSet(null);
            latch.countDown();
          }
        }
      });
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
}

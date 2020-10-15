package com.linkedin.parseq.lambda;

import static java.util.Map.entry;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.noneOf;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

  private static class QueueEntry {
	  private final byte[] _bytes;
	  private final ClassLoader _classLoader;
	  private final Exception _exception;
	public QueueEntry(byte[] bytes, ClassLoader classLoader, Exception exception) {
		this._bytes = bytes;
		this._classLoader = classLoader;
		this._exception = exception;
	}
  }

  private static ConcurrentMap<String, String> _names = new ConcurrentHashMap<>();
  private static ConcurrentLinkedQueue<QueueEntry> _entries = new ConcurrentLinkedQueue<>();


  public static void analyzeAll() {
	  while (!_entries.isEmpty()) {
		  QueueEntry e = _entries.poll();
		  Agent.Analyzer.doAnalyze(e._bytes, e._classLoader, e._exception);
	  }
  }


  public static class AnalyzerAdvice {

    public static Method _method;

    @Advice.OnMethodExit
    static void onExit(@Advice.Argument(0) Class<?> hostClass, @Advice.Argument(1) byte[] bytecode,
        @Advice.Return Class<?> definedClass) {
      try {
        _method.invoke(null, bytecode, definedClass.getClassLoader());
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  static {

	  try {
			Instrumentation inst = ByteBuddyAgent.install();

			// Inject AnalyzerAdvice and other classes to boot classloader
			Map<TypeDescription, Class<?>> injected = ClassInjector.UsingUnsafe.ofBootLoader()
					.inject(Map.ofEntries(
							entry(
									new TypeDescription.ForLoadedType(AnalyzerAdvice.class),
									ClassFileLocator.ForClassLoader.read(AnalyzerAdvice.class)
								)
							)
					);

			Class<?> injectedInt = ClassLoader.getPlatformClassLoader().loadClass(AnalyzerAdvice.class.getName());
			injectedInt.getField("_method").set(null, Agent.Analyzer.class.getDeclaredMethod("analyze", byte[].class, ClassLoader.class));

			JavaModule module = JavaModule.ofType(injectedInt);

			Class<?> unsafe = Class.forName("jdk.internal.misc.Unsafe");

			// Redefine File.exists
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
					public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
							ClassLoader classLoader, JavaModule module) {
						return builder.visit(Advice.to(AnalyzerAdvice.class).on(ElementMatchers.named("defineAnonymousClass")));
					}
				}).installOnByteBuddyAgent();
	  } catch(Exception e) {
		  e.printStackTrace();
	  }


//		new ByteBuddy()
//			.redefine(jdk.internal.misc.Unsafe.class)
//			.visit(Advice.to(AnalyzerAdvice.class).on(named("defineAnonymousClass")))
//			.make()
//			.load(jdk.internal.misc.Unsafe.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);

//	Blah.init();

//	Instrumentation instrumentation = ByteBuddyAgent.install();
//	final Agent.Analyzer analyzer = new Agent.Analyzer();
//
//	AgentBuilder.Default.LambdaInstrumentationStrategy.ENABLED.apply(new ByteBuddy(), instrumentation, analyzer);

//    if (LambdaFactory.register(classFileTransformer, new LambdaInstanceFactory(byteBuddy))) {
//        Class<?> lambdaMetaFactory;
//        try {
//            lambdaMetaFactory = Class.forName("java.lang.invoke.LambdaMetafactory");
//        } catch (ClassNotFoundException ignored) {
//            return;
//        }
//        byteBuddy.with(Implementation.Context.Disabled.Factory.INSTANCE)
//                .redefine(lambdaMetaFactory)
//                .visit(new AsmVisitorWrapper.ForDeclaredMethods()
//                        .method(named("metafactory"), MetaFactoryRedirection.INSTANCE)
//                        .method(named("altMetafactory"), AlternativeMetaFactoryRedirection.INSTANCE))
//                .make()
//                .load(lambdaMetaFactory.getClassLoader(), ClassReloadingStrategy.of(instrumentation));
//    }


//	new AgentBuilder.Default()
//	  .with(LambdaInstrumentationStrategy.ENABLED)
//	  .type(ElementMatchers.any())
//	  .transform((builder, type, classLoader, module) -> {
////		  analyzer.analyze(builder., loader);
//		  System.out.println(type);
//		  return builder;
//	  })
//	  .installOn(instrumentation);



//	ASMBasedTaskDescriptor.Agent.agentmain(null, instrumentation);


//    if (ASMBasedTaskDescriptor.Agent.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
//      try {
//        // Agent needs to be loaded through system class loader
//        // This piece of code adds Agent to system class path and then loads the Agent
//        ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(ASMBasedTaskDescriptor.Agent.class));
//        AgentLoader.loadAgentClass(ASMBasedTaskDescriptor.Agent.class.getName(), null, null, true, true, false);
//
//        // Reference the names field to names field of instance loaded through system class loader
//        // Its the system class loaded instance names field which is populated with lambda descriptions
//        // because agent is loaded through system class loader
//        Class<?> systemClazz = ClassLoader.getSystemClassLoader().loadClass(ASMBasedTaskDescriptor.class.getName());
//        Object _systemClassDescriptor = systemClazz.newInstance();
//
//        Field field = systemClazz.getDeclaredField("_names");
//        field.setAccessible(true);
//
//        ConcurrentMap<String, String> systemsNamesMap = (ConcurrentMap<String, String>) field.get(_systemClassDescriptor);
//        _names = systemsNamesMap;
//      } catch (Throwable e) {
//        System.err.println("Unable to refer to names map of ASMBasedTaskDescriptor loaded from system classpath");
//      }
//    } else {
//      try {
//        //for cases such as test executions which have only one class loader.
//        AgentLoader.loadAgentClass(ASMBasedTaskDescriptor.Agent.class.getName(), null);
//      } catch (Throwable e) {
//        // ignore this
//        // in case of multiple class loaders, this can throw an error as SystemClassLoaded loaded it already
//        // look above (ClassLoader.getSystemClassLoader().loadClass(ASMBasedTaskDescriptor.class.getName());)
//      }
//    }
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

  /*package private */ Optional<String> getLambdaClassDescription(String className) {
	analyzeAll();
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

  public static class Agent {

    private static final AtomicBoolean _initialized = new AtomicBoolean(false);

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
      if (_initialized.compareAndSet(false, true)) {
        instrumentation.addTransformer(new Analyzer());
      }
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
        _entries.add(new QueueEntry(byteCode, loader, new Exception()));
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
}

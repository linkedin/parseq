package com.linkedin.parseq.lambda;

import com.linkedin.agentloader.AgentLoader;
import com.linkedin.agentloader.ClassPathUtils;
import com.linkedin.parseq.TaskDescriptor;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;


/**
 * An ASM based implementation of {@link TaskDescriptor} to provide description for generated Lambda class.
 * Description of Lambda expression includes source code location of lambda, function call or method reference
 * within lambda.
 */
public class ASMBasedTaskDescriptor implements TaskDescriptor {

  private static ConcurrentMap<String, String> _names = new ConcurrentHashMap<>();

  static {
    if (ASMBasedTaskDescriptor.Agent.class.getClassLoader() != ClassLoader.getSystemClassLoader()) {
      try {
        // Agent needs to be loaded through system class loader
        // This piece of code adds Agent to system class path and then loads the Agent
        ClassPathUtils.appendToSystemPath(ClassPathUtils.getClassPathFor(ASMBasedTaskDescriptor.Agent.class));
        AgentLoader.loadAgentClass(ASMBasedTaskDescriptor.Agent.class.getName(), null, null, true, true, false);

        // Reference the names field to names field of instance loaded through system class loader
        // Its the system class loaded instance names field which is populated with lambda descriptions
        // because agent is loaded through system class loader
        Class<?> systemClazz = ClassLoader.getSystemClassLoader().loadClass(ASMBasedTaskDescriptor.class.getName());
        Object _systemClassDescriptor = systemClazz.newInstance();

        Field field = systemClazz.getDeclaredField("_names");
        field.setAccessible(true);

        ConcurrentMap<String, String> systemsNamesMap = (ConcurrentMap<String, String>) field.get(_systemClassDescriptor);
        _names = systemsNamesMap;
      } catch (Throwable e) {
        System.err.println("Unable to refer to names map of ASMBasedTaskDescriptor loaded from system classpath");
      }
    } else {
      try {
        //for cases such as test executions which have only one class loader.
        AgentLoader.loadAgentClass(ASMBasedTaskDescriptor.Agent.class.getName(), null);
      } catch (Throwable e) {
        // ignore this
        // in case of multiple class loaders, this can throw an error as SystemClassLoaded loaded it already
        // look above (ClassLoader.getSystemClassLoader().loadClass(ASMBasedTaskDescriptor.class.getName());)
      }
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

  /*package private */ Optional<String> getLambdaClassDescription(String className) {
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

    private static class Analyzer implements ClassFileTransformer {

      @Override
      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
          ProtectionDomain protectionDomain, byte[] classfileBuffer)
          throws IllegalClassFormatException {
        if (className == null && loader != null) {
          analyze(classfileBuffer, loader);
        }
        return classfileBuffer;
      }

      private void analyze(byte[] byteCode, ClassLoader loader) {
        ClassReader reader = new ClassReader(byteCode);
        LambdaClassLocator cv = new LambdaClassLocator(Opcodes.ASM5, loader);

        reader.accept(cv, 0);

        if (cv.isLambdaClass()) {
          LambdaClassDescription lambdaClassDescription = cv.getLambdaClassDescription();
          add(lambdaClassDescription.getClassName(), lambdaClassDescription.getDescription());
        }
      }
    }
  }
}

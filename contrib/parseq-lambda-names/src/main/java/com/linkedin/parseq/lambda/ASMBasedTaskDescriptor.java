package com.linkedin.parseq.lambda;

import com.ea.agentloader.AgentLoader;
import com.linkedin.parseq.TaskDescriptor;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
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

  static {
    AgentLoader.loadAgentClass(Agent.class.getName(), null);
  }

  private static final ConcurrentMap<String, LambdaClassDescription> _names = new ConcurrentHashMap<>();

  @Override
  public String getDescription(Class<?> clazz) {
    Optional<LambdaClassDescription> lambdaClassDescription = getLambdaClassDescription(clazz);
    if (lambdaClassDescription.isPresent()) {
      return lambdaClassDescription.get().getDescription();
    } else {
      return clazz.getName();
    }
  }

  /*package private */ Optional<LambdaClassDescription> getLambdaClassDescription(Class<?> clazz) {
    String className = clazz.getName();
    int slashIndex = className.lastIndexOf('/');
    if (slashIndex > 0) {
      String name = className.substring(0, slashIndex);
      return Optional.of(_names.get(name));
    }

    return Optional.empty();
  }

  private static void add(String lambdaClassName, LambdaClassDescription description) {
    _names.put(lambdaClassName, description);
  }

  static class Agent {

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
          analyze(classfileBuffer);
        }
        return classfileBuffer;
      }

      private void analyze(byte[] byteCode) {
        ClassReader reader = new ClassReader(byteCode);
        LambdaClassLocator cv = new LambdaClassLocator(Opcodes.ASM5);

        reader.accept(cv, 0);

        if (cv.isLambdaClass()) {
          LambdaClassDescription description = cv.getLambdaClassDescription();
          add(description.getClassName(), description);
        }
      }
    }
  }
}

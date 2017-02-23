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


public class ASMBasedTaskDescriptor implements TaskDescriptor {

  static {
    AgentLoader.loadAgentClass(Agent.class.getName(), null);
  }

  private static ConcurrentMap<String, String> _names = new ConcurrentHashMap<>();

  public ASMBasedTaskDescriptor() {
  }

  @Override
  public String getDescription(Class<?> clazz) {
    String className = clazz.getName();
    Optional<String> description = Optional.empty();
    int slashIndex = className.lastIndexOf('/');
    if (slashIndex > 0) {
      String name = className.substring(0, slashIndex);
      description = Optional.of(_names.get(name));
    }

    return description.orElse(className);
  }

  static void add(String lambdaClassName, String name) {
    _names.put(lambdaClassName, name);
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

        //TODO: Provide a way to execute TraceClassVisitor, that would ease in debugging
        //reader.accept(new TraceClassVisitor(cv, new PrintWriter(System.out)), 0);
        reader.accept(cv, 0);

        if (cv.isLambdaClass()) {
          LambdaClassDescription description = cv.getLambdaClassDescription();
          add(description.getClassName(), description.getDescription());
        }
      }
    }
  }
}

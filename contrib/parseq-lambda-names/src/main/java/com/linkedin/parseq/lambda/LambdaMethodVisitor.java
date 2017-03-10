package com.linkedin.parseq.lambda;

import java.io.IOException;
import java.util.function.Consumer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


class LambdaMethodVisitor extends MethodVisitor {

  private SourcePointer _lambdaSourcePointer;
  private Consumer<InferredOperation> _inferredOperationConsumer;
  private boolean _visitedFirstInsn;
  private boolean _containsSyntheticLambda;

  private String _methodInsnOwner;
  private String _methodInsnName;
  private String _methodInsnDesc;
  private int _methodInsnOpcode;

  private ClassLoader _loader;

  public LambdaMethodVisitor(int api, MethodVisitor mv, SourcePointer lambdaSourcePointer,
                              Consumer<InferredOperation> inferredOperationConsumer,
                              ClassLoader loader) {
    super(api, mv);
    _lambdaSourcePointer = lambdaSourcePointer;
    _inferredOperationConsumer = inferredOperationConsumer;
    _visitedFirstInsn = false;
    _containsSyntheticLambda = false;
    _loader = loader;
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
    if (!_visitedFirstInsn) {
      switch (opcode) {
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEINTERFACE:
          handleMethodInvoke(owner, name, desc, opcode);
          _visitedFirstInsn = true;
          break;
        default:
          //it should not come here as MethodVisitor API guarantees that it would either of the above 4 op codes.
          //for details look at javadoc of MethodVisitor.visitMethodInsn
          break;
      }
    }

    super.visitMethodInsn(opcode, owner, name, desc, itf);
  }

  @Override
  public void visitEnd() {
    if (_containsSyntheticLambda) {
      String classToVisit = _methodInsnOwner.replace('/', '.');
      try {
        SyntheticLambdaAnalyzer syntheticLambdaAnalyzer = new SyntheticLambdaAnalyzer(api, classToVisit, _methodInsnName);
        ClassReader cr = null;
        try {
          cr = new ClassReader(classToVisit);
        } catch(Throwable e) {
          try {
            cr = new ClassReader(_loader.getResourceAsStream(classToVisit.replace(".", "/") + ".class"));
          } catch (Throwable e1) {
            e1.printStackTrace();
          }
        }
        if (cr == null) {
          System.out.println("STILL A PROBLEM");
        } else {
          cr.accept(syntheticLambdaAnalyzer, 0);
          _inferredOperationConsumer.accept(new InferredOperation(_methodInsnOpcode, syntheticLambdaAnalyzer.getInferredOperation()));
        }
      } catch (Throwable e) {
        System.out.println("Unable to read class: " + classToVisit);
      }
    } else {
      //if it is static invocation, details about function could be found directly from the methodInsnName itself
      if (_methodInsnOpcode == Opcodes.INVOKESTATIC) {
        String functionName = Util.extractSimpleName(_methodInsnOwner, "/") + "::" + _methodInsnName;
        _inferredOperationConsumer.accept(new InferredOperation(Opcodes.INVOKESTATIC, functionName));
      } else {
        String classToVisit = _lambdaSourcePointer._className.replace('/', '.');
        try {
          FindMethodCallAnalyzer methodCallAnalyzer = new FindMethodCallAnalyzer(api, classToVisit, _lambdaSourcePointer, _methodInsnName);
          ClassReader cr = null;
          try {
            cr = new ClassReader(classToVisit);
          } catch(Throwable e) {
            try {
              cr = new ClassReader(_loader.getResourceAsStream(classToVisit.replace(".", "/") + ".class"));
            } catch (Throwable e1) {
              e1.printStackTrace();
            }
          }
          if (cr == null) {
            System.out.println("STILL A PROBLEM");
          } else {
            cr.accept(methodCallAnalyzer, 0);
            _inferredOperationConsumer.accept(new InferredOperation(_methodInsnOpcode, methodCallAnalyzer.getInferredOperation()));
          }
        } catch (Throwable e) {
          System.out.println("Unable to read class: " + classToVisit);
        }
      }
    }

    super.visitEnd();
  }

  private void handleMethodInvoke(String owner, String name, String desc, int opcode) {
    _methodInsnName = name;
    _methodInsnOwner = owner;
    _methodInsnDesc = desc;
    _methodInsnOpcode = opcode;
    if (name.startsWith("lambda$")) {
      _containsSyntheticLambda = true;
    } else {
      _containsSyntheticLambda = false;
    }
  }
}

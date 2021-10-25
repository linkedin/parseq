package com.linkedin.parseq.lambda;

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
  private int _methodInsnOpcode;

  private ClassLoader _loader;

  LambdaMethodVisitor(int api, MethodVisitor mv, SourcePointer lambdaSourcePointer,
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
      SyntheticLambdaAnalyzer syntheticLambdaAnalyzer = new SyntheticLambdaAnalyzer(api, classToVisit, _methodInsnName);
      ClassReader cr = getClassReader(classToVisit);
      if (cr != null) {
        cr.accept(syntheticLambdaAnalyzer, 0);
        _inferredOperationConsumer.accept(new InferredOperation(syntheticLambdaAnalyzer.getInferredOperation()));
        int inferredLineNumber = syntheticLambdaAnalyzer.getLineNumber();
        if (inferredLineNumber != -1) {
          _lambdaSourcePointer.setLineNumber(inferredLineNumber);
          if (!_lambdaSourcePointer.getClassName().equals(classToVisit)) {
            _lambdaSourcePointer.setClassName(classToVisit);
            _lambdaSourcePointer.setCallingMethod(null);
          }
        }
      }
    } else {
      //if it is static invocation, details about function could be found directly from the methodInsnName itself
      if (_methodInsnOpcode == Opcodes.INVOKESTATIC) {
        String functionName = Util.extractSimpleName(_methodInsnOwner, "/") + "::" + _methodInsnName;
        _inferredOperationConsumer.accept(new InferredOperation(functionName));
      } else {
        String classToVisit = _lambdaSourcePointer._className.replace('/', '.');
        FindMethodCallAnalyzer methodCallAnalyzer = new FindMethodCallAnalyzer(api, classToVisit, _lambdaSourcePointer, _methodInsnName);
        ClassReader cr = getClassReader(classToVisit);
        if (cr != null) {
          cr.accept(methodCallAnalyzer, 0);
          _inferredOperationConsumer.accept(new InferredOperation(methodCallAnalyzer.getInferredOperation()));
        }
      }
    }

    super.visitEnd();
  }

  private void handleMethodInvoke(String owner, String name, String desc, int opcode) {
    _methodInsnName = name;
    _methodInsnOwner = owner;
    _methodInsnOpcode = opcode;
    _containsSyntheticLambda = name.startsWith("lambda$");
  }

  private ClassReader getClassReader(String classToVisit) {
    ClassReader cr = null;
    try {
      cr = new ClassReader(classToVisit);
    } catch(Throwable e) {
      try {
        cr = new ClassReader(_loader.getResourceAsStream(classToVisit.replace(".", "/") + ".class"));
      } catch (Throwable e1) {
        System.out.println("WARNING: ParSeq lambda names might not be displayed as expected in the ParSeq trace. Unable to read class: " + classToVisit);
      }
    }

    return cr;
  }
}

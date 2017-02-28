package com.linkedin.parseq.lambda;

import java.io.IOException;
import java.util.function.Consumer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LambdaMethodVisitor extends MethodVisitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LambdaMethodVisitor.class);

  private SourcePointer _lambdaSourcePointer;
  private Consumer<InferredOperation> _inferredOperationConsumer;
  private boolean _visitedFirstInsn;
  private boolean _containsSyntheticLambda;

  private String _methodInsnOwner;
  private String _methodInsnName;
  private String _methodInsnDesc;
  private int _methodInsnOpcode;

  public LambdaMethodVisitor(int api, MethodVisitor mv, SourcePointer lambdaSourcePointer,
                              Consumer<InferredOperation> inferredOperationConsumer) {
    super(api, mv);
    _lambdaSourcePointer = lambdaSourcePointer;
    _inferredOperationConsumer = inferredOperationConsumer;
    _visitedFirstInsn = false;
    _containsSyntheticLambda = false;
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
        ClassReader cr = new ClassReader(classToVisit);
        cr.accept(syntheticLambdaAnalyzer, 0);
        _inferredOperationConsumer.accept(new InferredOperation(_methodInsnOpcode, syntheticLambdaAnalyzer.getInferredOperation()));
      } catch (IOException e) {
        LOGGER.debug("Unable to read class: " + classToVisit, e.getMessage());
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
          ClassReader cr = new ClassReader(classToVisit);
          cr.accept(methodCallAnalyzer, 0);
          _inferredOperationConsumer.accept(new InferredOperation(_methodInsnOpcode, methodCallAnalyzer.getInferredOperation()));
        } catch (IOException e) {
          LOGGER.debug("Unable to read class: " + classToVisit, e.getMessage());
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

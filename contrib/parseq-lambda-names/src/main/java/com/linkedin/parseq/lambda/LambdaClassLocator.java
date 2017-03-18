package com.linkedin.parseq.lambda;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


/**
 * An implementation of ASM ClassVisitor which analyses classes as they are loaded and identifies classes
 * generated for Lamda expressions. In addition, it infers details such as source code location, function call within
 * Lambda expression..
 */
class LambdaClassLocator extends ClassVisitor {

  private String _className;
  private boolean _isLambdaClass;
  private SourcePointer _sourcePointer;
  private InferredOperation _inferredOperation;

  private ClassLoader _loader;

  LambdaClassLocator(int api, ClassLoader loader) {
    super(api);
    _loader = loader;
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    super.visit(version, access, name, signature, superName, interfaces);
    _className = name.replace('/', '.');
    _isLambdaClass = Util.isALambdaClassByName(name);
    if (_isLambdaClass) {
      _sourcePointer = SourcePointer.get().orElse(null);
    }
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
    //ignore visiting method if this is not generated Lambda class
    if (!_isLambdaClass) {
      return mv;
    }

    //these two methods are present in generated byte code for lambda
    //ignoring as they don't provide any insights into details we are looking for
    if (name.equals("<init>") || name.equals("get$Lambda")) {
      return mv;
    }

    //parse generated lambda code to get details about operation
    return new LambdaMethodVisitor(api, mv, _sourcePointer, this::setInferredOperation, _loader);
  }

  private void setInferredOperation(InferredOperation inferredOperation) {
    _inferredOperation = inferredOperation;
  }

  boolean isLambdaClass() {
    return _isLambdaClass;
  }

  LambdaClassDescription getLambdaClassDescription() {
    return new LambdaClassDescription(_className, _sourcePointer, _inferredOperation);
  }
}

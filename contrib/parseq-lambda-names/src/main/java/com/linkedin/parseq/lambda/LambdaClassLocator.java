package com.linkedin.parseq.lambda;

import org.objectweb.asm.ClassVisitor;


/**
 * An implementation of ASM ClassVisitor which analyses classes as they are loaded and identifies classes
 * generated for Lamda expressions. In addition, it infers details such as source code location, function call within
 * Lambda expression..
 */
public class LambdaClassLocator extends ClassVisitor {

  private String _className;
  private SourcePointer _sourcePointer;
  private boolean _isLambdaClass;

  public LambdaClassLocator(int api) {
    super(api);
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

  boolean isLambdaClass() {
    return _isLambdaClass;
  }

  LambdaClassDescription getLambdaClassDescription() {
    return new LambdaClassDescription(_className, _sourcePointer);
  }
}

package com.linkedin.parseq.lambda;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

/**
 * Given the uniquely generated synthetic lambda function, it analyzes class to infer information such as number of
 * parameters for the lambda function
 */
class SyntheticLambdaAnalyzer extends ClassVisitor {

  private final String _classToAnalyze;
  private final String _methodToFind;

  private String _inferredOperation;

  SyntheticLambdaAnalyzer(int api, String classToAnalyze, String methodToFind) {
    super(api);
    _classToAnalyze = classToAnalyze;
    _methodToFind = methodToFind;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    if (name.equals(_methodToFind)) {
      return new SyntheticLambdaMethodVisitor(api, access, name, desc, signature, exceptions);
    }

    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  String getInferredOperation() {
    return _inferredOperation;
  }

  private class SyntheticLambdaMethodVisitor extends MethodNode {

    private String _methodInsnName;
    private String _methodInsnOwner;
    private String _methodInsnDesc;
    private int _methodInsnOpcode;

    SyntheticLambdaMethodVisitor(int api, int access, String name, String desc, String signature, String[] exceptions) {
      super(api, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
      switch (opcode) {
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKESPECIAL:
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEINTERFACE:
          _methodInsnName = name;
          _methodInsnOwner = owner;
          _methodInsnDesc = desc;
          _methodInsnOpcode = opcode;
          break;
        default:
          System.out.println("Unexpected opcode, falling back");
          break;
      }

      super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitEnd() {
      try {
        Analyzer analyzer = new Analyzer(new SourceInterpreter());
        Frame[] frames = analyzer.analyze(_classToAnalyze, this);

        int index = findMethodCall(this.instructions);
        if (index == -1) {
          return;
        }

        Frame f = frames[index];
        String fieldDesc = "";

        for (int j = 0; j < f.getStackSize(); ++j) {
          SourceValue stack = (SourceValue) f.getStack(j);
          Object insn = stack.insns.iterator().next();
          if (insn instanceof FieldInsnNode) {
            FieldInsnNode fieldInstr = (FieldInsnNode) insn;
            fieldDesc = fieldInstr.name;
          } else if (insn instanceof TypeInsnNode) {
            TypeInsnNode typeInsnNode = (TypeInsnNode) insn;
            fieldDesc = "new " + Util.extractSimpleName(typeInsnNode.desc, "/") + "()";
          } else if (insn instanceof MethodInsnNode) {
            MethodInsnNode methodInstr = (MethodInsnNode) insn;
            if (methodInstr.getOpcode() == Opcodes.INVOKESPECIAL && methodInstr.name.equals("<init>")) {
              fieldDesc = "new " + Util.extractSimpleName(methodInstr.owner, "/") + "()";
            } else {
              Type methodType = Type.getMethodType(methodInstr.desc);
              int retSize = methodType.getArgumentsAndReturnSizes() & 0x03;
              if (retSize > 0) {
                fieldDesc = methodInstr.name + Util.getArgumentsInformation(methodInstr.desc);
              }
            }
          }
        }

        _inferredOperation = getInferredOperation(fieldDesc);
      } catch (AnalyzerException e) {
        System.out.println("Unable to analyze class, could not infer operation");
      }
    }

    //find the last operation
    private int findMethodCall(InsnList insns) {
      int ret = -1;
      for (int i = 0; i < insns.size(); i++) {
        AbstractInsnNode n = insns.get(i);
        if (n.getOpcode() == Opcodes.INVOKEVIRTUAL
            || n.getOpcode() == Opcodes.INVOKESTATIC
            || n.getOpcode() == Opcodes.INVOKEINTERFACE) {
          ret = i;
        }
      }

      return ret;
    }

    private String getInferredOperation(String fieldDesc) {
      String functionName;
      if (_methodInsnOpcode == Opcodes.INVOKESTATIC) {
        functionName = Util.extractSimpleName(_methodInsnOwner, "/") + "." + _methodInsnName;
      } else {
        functionName = _methodInsnName;
      }

      StringBuilder sb = new StringBuilder();
      if (!fieldDesc.isEmpty()) {
        sb.append(fieldDesc).append(".");
      }
      sb.append(functionName);
      sb.append(Util.getArgumentsInformation(_methodInsnDesc));
      return sb.toString();
    }
  }
}
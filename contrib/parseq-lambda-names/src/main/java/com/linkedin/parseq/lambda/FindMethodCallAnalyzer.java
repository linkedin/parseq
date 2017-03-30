package com.linkedin.parseq.lambda;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SourceInterpreter;
import org.objectweb.asm.tree.analysis.SourceValue;


/**
 * Given the function name and the line number of its invocation in source code, it analyzes class to infer information
 * such as number of parameters for function, field on which function is executed.
 */
class FindMethodCallAnalyzer extends ClassVisitor {

  private final String _classToAnalyze;
  private final String _methodToFind;
  private final int _lineNumberOfMethodCall;
  private final String _methodInsnName;

  private String _inferredOperation;

  FindMethodCallAnalyzer(int api, String classToAnalyze, SourcePointer sourcePointerOfMethodCall, String methodInsnName) {
    super(api);
    _classToAnalyze = classToAnalyze;
    _methodToFind = sourcePointerOfMethodCall._callingMethod;
    _lineNumberOfMethodCall = sourcePointerOfMethodCall._lineNumber;
    _methodInsnName = methodInsnName;
  }

  String getInferredOperation() {
    return _inferredOperation;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    if (name.equals(_methodToFind)) {
      return new FindMethodCallAnalyzer.FindMethodCallMethodVisitor(api, access, name, desc, signature, exceptions);
    }

    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  private class FindMethodCallMethodVisitor extends MethodNode {

    FindMethodCallMethodVisitor(int api, int access, String name, String desc, String signature, String[] exceptions) {
      super(api, access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
      try {
        Analyzer analyzer = new Analyzer(new SourceInterpreter());
        Frame[] frames = analyzer.analyze(_classToAnalyze, this);

        LabelNode label = findLineLabel(this.instructions, _lineNumberOfMethodCall);
        int index = findMethodCall(this.instructions, label);

        if (index != -1) {
          List<String> localVariables = new ArrayList<>();

          String fieldDesc = "";

          Frame f = frames[index];
          boolean parsedThisOnce = false;
          for (int j = 0; j < f.getStackSize(); ++j) {
            SourceValue stack = (SourceValue) f.getStack(j);

            Object insn = stack.insns.iterator().next();
            if (insn instanceof VarInsnNode) {
              VarInsnNode vinsn = (VarInsnNode) insn;
              if (vinsn.var < this.localVariables.size()) {
                String variable = ((LocalVariableNode) this.localVariables.get(vinsn.var)).name;

                //This part is tricky: discard the first this.
                if (variable.equals("this") && !parsedThisOnce) {
                  parsedThisOnce = true;
                } else {
                  localVariables.add(variable);
                }
              }
            } else if (insn instanceof FieldInsnNode) {
              FieldInsnNode fieldInstr = (FieldInsnNode) insn;
              fieldDesc = fieldInstr.name;
            } else if (insn instanceof TypeInsnNode) {
              fieldDesc = Util.getDescriptionForTypeInsnNode((TypeInsnNode) insn);
            }
          }

          _inferredOperation = getInferredOperation(localVariables, fieldDesc);
        }
      } catch (AnalyzerException e) {
        System.out.println("Unable to analyze class, could not infer operation");
      }
    }

    private LabelNode findLineLabel(InsnList insns, int line) {
      for (Iterator it = insns.iterator(); it.hasNext(); ) {
        Object n = it.next();
        if (n instanceof LineNumberNode && ((LineNumberNode) n).line == line) {
          return ((LineNumberNode) n).start;
        }
      }
      return null;
    }

    private int findMethodCall(InsnList insns, LabelNode label) {
      boolean foundLabel = false;
      for (int i = 0; i < insns.size(); i++) {
        AbstractInsnNode n = insns.get(i);
        if (!foundLabel && n == label) {
          foundLabel = true;
        } else if (foundLabel && n.getOpcode() == Opcodes.INVOKEDYNAMIC) {
          return i;
        }
      }
      return -1;
    }

    //Keeping the code commented if we were to improve this functionality in future
    private String getInferredOperation(List<String> localVariables, String fieldDesc) {
//      String localVarsDesc = getDescriptionForLocalVars(localVariables);
      StringBuilder sb = new StringBuilder();
//      if (!fieldDesc.isEmpty()) {
//        sb.append(fieldDesc).append("::");
//      } else if (!localVarsDesc.isEmpty()) {
//        sb.append(localVarsDesc).append("::");
//      } else if (!methodDesc.isEmpty()) {
//        sb.append(methodDesc).append("::");
//      }

      sb.append("::" + _methodInsnName);
      return sb.toString();
    }
  }

  private static String getDescriptionForLocalVars(List<String> variables) {
    if (variables == null || variables.size() == 0) {
      return "";
    }

    if (variables.size() == 1) {
      return variables.get(0);
    }

    StringJoiner sj = new StringJoiner(",", "(", ")");
    variables.forEach(sj::add);
    return sj.toString();
  }
}
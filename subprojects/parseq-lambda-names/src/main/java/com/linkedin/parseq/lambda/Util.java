package com.linkedin.parseq.lambda;

import java.util.StringJoiner;
import java.util.regex.Pattern;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;


class Util {

  private static final Pattern LAMBDA_NAME_PATTERN = Pattern.compile("^.*\\$\\$Lambda\\$.*$");

  /* package private */
  static boolean isALambdaClassByName(String name) {
    return LAMBDA_NAME_PATTERN.matcher(name.replace('/', '.')).matches();
  }

  /* package private */
  static String extractSimpleName(String fqcn, String separator) {
    if (fqcn.contains(separator)) {
      return fqcn.substring(fqcn.lastIndexOf(separator) + 1, fqcn.length());
    } else {
      return fqcn;
    }
  }

  /* package private */
  static String getArgumentsInformation(String insnDesc) {
    if (insnDesc == null) {
      return "";
    }

    Type methodType = Type.getMethodType(insnDesc);
    int argSize = methodType.getArgumentTypes().length;
    StringJoiner sj = new StringJoiner(",", "(", ")");
    for (int i = 0; i < argSize; i++) {
      sj.add("_");
    }

    return sj.toString();
  }

  /* package private */
  static String getDescriptionForMethodInsnNode(MethodInsnNode methodInstr) {
    if (methodInstr.getOpcode() == Opcodes.INVOKESPECIAL && methodInstr.name.equals("<init>")) {
      return "new " + Util.extractSimpleName(methodInstr.owner, "/") + "()";
    } else {
      Type methodType = Type.getMethodType(methodInstr.desc);
      int retSize = methodType.getArgumentsAndReturnSizes() & 0x03;
      if (retSize > 0) {
        return methodInstr.name + Util.getArgumentsInformation(methodInstr.desc);
      }
    }

    return "";
  }

  /* package private */
  static String getDescriptionForTypeInsnNode(TypeInsnNode typeInsnNode) {
    AbstractInsnNode nextInsn = typeInsnNode.getNext();
    StringJoiner argsDesc = new StringJoiner(",", "(", ")");
    while (nextInsn != null) {
      if (nextInsn instanceof LdcInsnNode) {
        argsDesc.add("_");
      } else if (nextInsn.getOpcode() == Opcodes.DUP) {
        //ignore
      } else {
        break;
      }
      nextInsn = nextInsn.getNext();
    }
    if (typeInsnNode.getOpcode() == Opcodes.NEW) {
      return "new " + Util.extractSimpleName(typeInsnNode.desc, "/") + argsDesc.toString();
    }

    return "";
  }
}

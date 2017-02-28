package com.linkedin.parseq.lambda;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import org.objectweb.asm.Type;


class Util {

  private static final Pattern LAMBDA_NAME_PATTERN = Pattern.compile("^.*\\$\\$Lambda\\$\\d+$");

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
}

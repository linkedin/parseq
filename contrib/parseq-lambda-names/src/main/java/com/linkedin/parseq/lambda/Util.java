package com.linkedin.parseq.lambda;

import java.util.regex.Pattern;


class Util {

  private static final Pattern LAMBDA_NAME_PATTERN = Pattern.compile("^.*\\$\\$Lambda\\$\\d+$");

  /* package private */ static boolean isALambdaClassByName(String name) {
    return LAMBDA_NAME_PATTERN.matcher(name.replace('/', '.')).matches();
  }

  /* package private */ static String extractSimpleName(String fqcn, String separator) {
    if (fqcn.contains(separator)) {
      return fqcn.substring(fqcn.lastIndexOf(separator) + 1, fqcn.length());
    } else {
      return fqcn;
    }
  }
}

package com.linkedin.restli.client.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

class RequestConfigKeyParsingErrorListener extends BaseErrorListener {

  private final List<String> _errors = new ArrayList<>();

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
    String msg, RecognitionException e) {
    _errors.add("line " + line + ":" + charPositionInLine + " " + msg + "\n");
  }

  public boolean hasErrors() {
    return !_errors.isEmpty();
  }

  public List<String> getErrors() {
    return Collections.unmodifiableList(_errors);
  }

  public int errorsSize() {
    return _errors.size();
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner("");
    for (String error: _errors) {
      sj.add(error);
    }
    return sj.toString();
  }

}

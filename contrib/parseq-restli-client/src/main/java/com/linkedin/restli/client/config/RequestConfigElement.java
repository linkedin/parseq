package com.linkedin.restli.client.config;

import java.util.Optional;
import java.util.function.BiFunction;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.linkedin.restli.client.config.RequestConfigKeyParser.InboundContext;
import com.linkedin.restli.client.config.RequestConfigKeyParser.KeyContext;
import com.linkedin.restli.client.config.RequestConfigKeyParser.OperationInContext;
import com.linkedin.restli.client.config.RequestConfigKeyParser.OperationOutContext;
import com.linkedin.restli.client.config.RequestConfigKeyParser.OutboundContext;
import com.linkedin.restli.client.config.RequestConfigKeyParser.ResourceContext;
import com.linkedin.restli.common.ResourceMethod;


class RequestConfigElement implements Comparable<RequestConfigElement> {

  private final String _key;
  private final Object _value;
  private final String _property;
  private final Optional<String> _inboundName;
  private final Optional<String> _outboundName;
  private final Optional<String> _inboundOpName;
  private final Optional<String> _outboundOpName;
  private final Optional<String> _inboundOp;
  private final Optional<ResourceMethod> _outboundOp;

  private RequestConfigElement(String key, Object value, String property, Optional<String> inboundName,
      Optional<String> outboundName, Optional<String> inboundOpName, Optional<String> outboundOpName,
      Optional<String> inboundOp, Optional<ResourceMethod> outboundOp) {
    _key = key;
    _value = value;
    _property = property;
    _inboundName = inboundName;
    _outboundName = outboundName;
    _inboundOpName = inboundOpName;
    _outboundOpName = outboundOpName;
    _inboundOp = inboundOp;
    _outboundOp = outboundOp;
  }

  public String getKey() {
    return _key;
  }

  public Object getValue() {
    return _value;
  }

  public String getProperty() {
    return _property;
  }

  public Optional<String> getInboundName() {
    return _inboundName;
  }

  public Optional<String> getOutboundName() {
    return _outboundName;
  }

  public Optional<String> getInboundOpName() {
    return _inboundOpName;
  }

  public Optional<String> getOutboundOpName() {
    return _outboundOpName;
  }

  public Optional<String> getInboundOp() {
    return _inboundOp;
  }

  public Optional<ResourceMethod> getOutboundOp() {
    return _outboundOp;
  }

  private static Optional<String> handlingWildcard(ResourceContext resourceContext) {
    if (resourceContext == null) {
      return Optional.empty();
    } else {
      return Optional.of(resourceContext.getText());
    }
  }

  private static Optional<String> handlingWildcard(TerminalNode input) {
    if (input == null) {
      return Optional.empty();
    } else {
      return Optional.of(input.getText());
    }
  }

  static RequestConfigElement parse(String property, String key, Object value) throws RequestConfigKeyParsingException {
    RequestConfigKeyParsingErrorListener errorListener = new RequestConfigKeyParsingErrorListener();
    ANTLRInputStream input = new ANTLRInputStream(key);
    RequestConfigKeyLexer lexer = new RequestConfigKeyLexer(input);
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    RequestConfigKeyParser parser = new RequestConfigKeyParser(tokens);
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    KeyContext keyTree = parser.key();

    if (!errorListener.hasErrors()) {
      InboundContext inbound = keyTree.inbound();
      OutboundContext outbound = keyTree.outbound();
      Optional<String> inboundName = handlingWildcard(inbound.resource());
      Optional<String> outboundName = handlingWildcard(outbound.resource());
      Optional<String> inboundOp = getOpIn(inbound.operationIn());
      Optional<ResourceMethod> outboundOp = getOpOut(outbound.operationOut());
      Optional<String> inboundOpName = inboundOp.flatMap(method -> getOpInName(method, inbound.operationIn()));
      Optional<String> outboundOpName = outboundOp.flatMap(method -> getOpOutName(method, outbound.operationOut()));

      return new RequestConfigElement(key, coerceValue(property, value), property, inboundName, outboundName,
          inboundOpName, outboundOpName, inboundOp, outboundOp);

    } else {
      throw new RequestConfigKeyParsingException(
          "Error" + ((errorListener.errorsSize() > 1) ? "s" : "") + " parsing key: " + key + "\n" + errorListener);
    }
  }

  private static Object coerceValue(String property, Object value) throws RequestConfigKeyParsingException {
    try {
      switch(property) {
        case "timeoutMs":
          return ConfigValueCoercers.LONG.apply(value);
        case "batchingEnabled":
          return ConfigValueCoercers.BOOLEAN.apply(value);
        case "maxBatchSize":
          return ConfigValueCoercers.INTEGER.apply(value);
        default:
          throw new RequestConfigKeyParsingException("Internal error: parsed config contains unsupported property: " + property);
      }
    } catch (Exception e) {
      throw new RequestConfigKeyParsingException(e);
    }
  }

  private static Optional<String> getOpOutName(ResourceMethod method, OperationOutContext operation) {
    if (method == ResourceMethod.ACTION || method == ResourceMethod.FINDER) {
      return handlingWildcard(operation.complex().Name());
    } else {
      return Optional.empty();
    }
  }

  private static Optional<ResourceMethod> getOpOut(OperationOutContext operation) {
    if (operation == null) {
      return Optional.empty();
    } else {
      if (operation.simpleOp() != null) {
        return Optional.of(ResourceMethod.fromString(operation.simpleOp().getText()));
      } else {
        return Optional.of(ResourceMethod.fromString(operation.complex().complexOp().getText()));
      }
    }
  }

  private static Optional<String> getOpInName(String method, OperationInContext operation) {
    if (method.equals(ResourceMethod.ACTION.toString().toUpperCase()) || method.equals(ResourceMethod.FINDER.toString().toUpperCase())) {
      return handlingWildcard(operation.complex().Name());
    } else {
      return Optional.empty();
    }
  }

  private static Optional<String> getOpIn(OperationInContext operation) {
    if (operation == null) {
      return Optional.empty();
    } else {
      if (operation.simpleOp() != null) {
        return Optional.of(operation.simpleOp().getText());
      } else if (operation.complex() != null) {
        return Optional.of(operation.complex().complexOp().getText());
      } else {
        return Optional.of(operation.httpExtraOp().getText());
      }
    }
  }

  private static Integer compare(Optional<String> e1, Optional<String> e2) {
    if (e1.isPresent() && !e2.isPresent()) {
      return -1;
    } else if (!e1.isPresent() && e2.isPresent()) {
      return 1;
    } else {
      return 0;
    }
  }

  private static BiFunction<RequestConfigElement, RequestConfigElement, Integer> chain(
      BiFunction<RequestConfigElement, RequestConfigElement, Integer> f1,
      BiFunction<RequestConfigElement, RequestConfigElement, Integer> f2) {
    return (e1, e2) -> {
      int f1Result = f1.apply(e1, e2);
      if (f1Result != 0) {
        return f1Result;
      } else {
        return f2.apply(e1, e2);
      }
    };
  }

  @Override
  public int compareTo(RequestConfigElement o) {
    return
      chain(
          chain(
              chain(
                  chain(
                      chain(
                          (e1, e2) -> compare(e1._outboundName, e2._outboundName),
                          (e1, e2) -> compare(_inboundName, o._inboundName)),
                      (e1, e2) -> compare(_outboundOp.map(ResourceMethod::toString), o._outboundOp.map(ResourceMethod::toString))),
                  (e1, e2) -> compare(_outboundOpName, o._outboundOpName)),
              (e1, e2) -> compare(_inboundOp, o._inboundOp)),
          (e1, e2) -> compare(_inboundOpName, o._inboundOpName))
      .apply(this, o);
  }

}

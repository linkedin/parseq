package com.linkedin.restli.client.config;

import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.linkedin.restli.client.config.ResourceConfigKeyParser.InboundContext;
import com.linkedin.restli.client.config.ResourceConfigKeyParser.KeyContext;
import com.linkedin.restli.client.config.ResourceConfigKeyParser.OutboundContext;
import com.linkedin.restli.common.ResourceMethod;

class ResourceConfigElement {

  private final String _key;
  private final Object _value;
  private final String _property;
  private final Optional<String> _inboundName;
  private final Optional<String> _outboundName;
  private final Optional<String> _inboundOpName;
  private final Optional<String> _outboundOpName;
  private final Optional<ResourceMethod> _inboundOp;
  private final Optional<ResourceMethod> _outboundOp;

  private ResourceConfigElement(String key, Object value, String property, Optional<String> inboundName,
      Optional<String> outboundName, Optional<String> inboundOpName, Optional<String> outboundOpName,
      Optional<ResourceMethod> inboundOp, Optional<ResourceMethod> outboundOp) {
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

  private static Optional<String> getName(String name) {
    if (name.equals("*")) {
      return Optional.empty();
    } else {
      return Optional.of(name);
    }
  }

  static ResourceConfigElement parse(String key, Object value) {
    ANTLRInputStream input = new ANTLRInputStream(key);
    ResourceConfigKeyLexer lexer = new ResourceConfigKeyLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ResourceConfigKeyParser parser = new ResourceConfigKeyParser(tokens);
    KeyContext keyTree = parser.key();
    //System.out.println(keyTree.toStringTree(parser));
    InboundContext inbound = keyTree.path().inbound();
    OutboundContext outbound = keyTree.path().outbound();
//    return new ResourceConfigElement(entry.getKey(), entry.getValue(), key.property().getText(),
//        getName(inbound.Name().getText()),
//        getName(inbound.Name().getText()),
//        inbound.operation()., outboundOpName, inboundOp, outboundOp);
    return null;
  }

}

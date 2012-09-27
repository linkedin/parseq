/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq.trace.codec.json;

import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.codec.TraceCodec;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * JSON implementation of {@link com.linkedin.parseq.trace.codec.TraceCodec}
 *
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class JsonTraceCodec implements TraceCodec
{
  // Top Level Fields
  static final String TRACES = "traces";
  static final String RELATIONSHIPS = "relationships";

  // Trace Fields
  static final String TRACE_ID = "id";
  static final String TRACE_HIDDEN="hidden";
  static final String TRACE_SYSTEM_HIDDEN="systemHidden";
  static final String TRACE_NAME = "name";
  static final String TRACE_VALUE = "value";
  static final String TRACE_RESULT_TYPE = "resultType";
  static final String TRACE_START_NANOS = "startNanos";
  static final String TRACE_END_NANOS = "endNanos";
  static final String TRACE_ATTRIBUTES = "attributes";
  static final String TRACE_ATTRIBUTE_KEY = "key";
  static final String TRACE_ATTRIBUTE_VALUE = "value";

  // Relationship Fields
  static final String RELATIONSHIP_RELATIONSHIP = "relationship";
  static final String RELATIONSHIP_FROM = "from";
  static final String RELATIONSHIP_TO = "to";

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


  @Override
  public Trace decode(InputStream inputStream) throws IOException
  {
    final JsonParser parser = OBJECT_MAPPER.getJsonFactory().createJsonParser(inputStream);
    final JsonNode rootNode = OBJECT_MAPPER.readTree(parser);
    return JsonTraceDeserializer.deserialize(rootNode);
  }

  @Override
  public void encode(Trace trace, OutputStream outputStream) throws IOException
  {
    final JsonGenerator generator = OBJECT_MAPPER.getJsonFactory()
                                                 .createJsonGenerator(outputStream, JsonEncoding.UTF8);
    JsonTraceSerializer.serialize(trace, generator);
    generator.flush();
  }
}
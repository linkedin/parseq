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

package com.linkedin.parseq.trace.codec;

import com.linkedin.parseq.trace.Trace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for encoding/decoding the {@link com.linkedin.parseq.trace.ShallowTrace}
 * @author Chi Chan (ckchan@linkedin.com)
 */
public interface TraceCodec
{
  /**
   * Decode the InputStream to an Trace.
   *
   * @param inputStream the InputStream of the trace
   * @return the decoded trace
   * @throws IOException if an error occur during decoding
   */
  Trace decode(InputStream inputStream) throws IOException;

  /**
   * Similar to {@link #decode(java.io.InputStream)} but takes a String instead
   * of an InputStream.
   *
   * @param traceStr a String representation of a trace
   * @return the decoded trace
   * @throws IOException if an error occur during decoding
   */
  Trace decode(String traceStr) throws IOException;

  /**
   * Encodes a Trace to an OutputStream.
   *
   * @param trace the trace to encode
   * @param outputStream the OutputStream used to encode
   * @throws IOException if an error occurs during encoding
   */
  void encode(Trace trace, OutputStream outputStream) throws IOException;

  /**
   * Similar to {@link #encode(com.linkedin.parseq.trace.ShallowTrace, java.io.OutputStream)}
   * but produces a String that contains the trace instead of writing it to an
   * OutputStream.
   *
   * @param trace the trace to encode
   * @return a String representation for the given trace
   * @throws IOException if an error occurs during encoding
   */
  String encode(Trace trace) throws IOException;
}

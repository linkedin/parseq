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
 * Interface for encoding/decoding the {@link com.linkedin.parseq.trace.Trace}
 * @author Chi Chan (ckchan@linkedin.com)
 */
public interface TraceCodec
{
  /**
   * Decode the InputStream to an TaskTrace
   * @param inputStream the InputStream of the TaskTrace
   * @return the decoded TaskTrace
   * @throws IOException if an error occur during decoding
   */
  Trace decode(InputStream inputStream) throws IOException;

  /**
   * Encoding an TaskTrace to an outputstream
   * @param trace the TaskTrace to encode
   * @param outputStream the OutputStream used to encode
   * @throws IOException if an error occurs during encoding
   */
  void encode(Trace trace, OutputStream outputStream) throws IOException;
}

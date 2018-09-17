package org.HdrHistogram;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import javax.xml.bind.DatatypeConverter;

import com.linkedin.parseq.HistogramSerializer;


public class Base64CompressedHistogramSerializer implements HistogramSerializer {

  private ByteBuffer targetBuffer;

  @Override
  public synchronized String serialize(Histogram histogram) {
    int requiredBytes = histogram.getNeededByteBufferCapacity() + (2 * Long.BYTES);  // Long.BYTES for start and end timestamps
    if ((targetBuffer == null) || targetBuffer.capacity() < requiredBytes) {
      targetBuffer = ByteBuffer.allocate(requiredBytes);
    }
    targetBuffer.clear();

    int compressedLength = histogram.encodeIntoCompressedByteBuffer(targetBuffer, Deflater.BEST_COMPRESSION);
    targetBuffer.putLong(compressedLength, histogram.getStartTimeStamp());
    targetBuffer.putLong(compressedLength + Long.BYTES, histogram.getEndTimeStamp());
    byte[] compressedArray = Arrays.copyOf(targetBuffer.array(), compressedLength + (2 * Long.BYTES));
    return DatatypeConverter.printBase64Binary(compressedArray);
  }

  @Override
  public Histogram deserialize(String serialized) {
    try {
      byte[] rawBytes = DatatypeConverter.parseBase64Binary(serialized);
      final ByteBuffer buffer = ByteBuffer.wrap(rawBytes, 0, rawBytes.length - (2 * Long.BYTES));
      Histogram histogram = (Histogram) EncodableHistogram.decodeFromCompressedByteBuffer(buffer, 0);
      final ByteBuffer timestamps = ByteBuffer.wrap(rawBytes, 0, rawBytes.length);
      histogram.setStartTimeStamp(timestamps.getLong(rawBytes.length - (2 * Long.BYTES)));
      histogram.setEndTimeStamp(timestamps.getLong(rawBytes.length - (2 * Long.BYTES) + Long.BYTES));
      return histogram;
    } catch (DataFormatException e) {
      throw new RuntimeException(e);
    }
  }

}

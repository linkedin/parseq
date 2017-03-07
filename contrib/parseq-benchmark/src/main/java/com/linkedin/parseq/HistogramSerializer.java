package com.linkedin.parseq;

import org.HdrHistogram.Histogram;

public interface HistogramSerializer {

  String serialize(Histogram histogram);

  Histogram deserialize(String serialized);

}

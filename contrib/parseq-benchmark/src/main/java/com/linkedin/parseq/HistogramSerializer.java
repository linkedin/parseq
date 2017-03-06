package com.linkedin.parseq;

import org.HdrHistogram.Histogram;

public interface HistogramSerializer {

  String serlialize(Histogram histogram);

  Histogram deserialize(String serialized);

}

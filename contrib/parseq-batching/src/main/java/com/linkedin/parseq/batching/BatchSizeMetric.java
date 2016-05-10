package com.linkedin.parseq.batching;

import java.util.function.Function;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.Recorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchSizeMetric {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchSizeMetric.class);

  private static final int LOWEST_DISCERNIBLE_VALUE = 1;
  private static final int HIGHEST_TRACKABLE_VALUE = 10_000;
  private static final int NUMBER_OF_FIGNIFICANT_VALUE_DIGITS = 3;

  private Recorder _recorder = null;

  private Histogram _recycle;

  /**
   * Records a batch size.
   * This method is thread safe.
   * @param batchSize batch size
   */
  public void record(int batchSize) {
    recordSafeValue(narrow(batchSize));
  }

  private int narrow(int batchSize) {
    if (batchSize < LOWEST_DISCERNIBLE_VALUE) {
      LOGGER.warn("batch size lower than expected: " + batchSize + ", recording as: " + LOWEST_DISCERNIBLE_VALUE);
      return LOWEST_DISCERNIBLE_VALUE;
    }
    if (batchSize > HIGHEST_TRACKABLE_VALUE) {
      LOGGER.warn("batch size greater than expected: " + batchSize + ", recording as: " + HIGHEST_TRACKABLE_VALUE);
      return HIGHEST_TRACKABLE_VALUE;
    }
    return batchSize;
  }

  private void initializeRecorder() {
    if (_recorder == null) {
      _recorder = new Recorder(LOWEST_DISCERNIBLE_VALUE, HIGHEST_TRACKABLE_VALUE, NUMBER_OF_FIGNIFICANT_VALUE_DIGITS);
    }
  }

  private synchronized void recordSafeValue(int batchSize) {
    initializeRecorder();
    _recorder.recordValue(batchSize);
  }

  /**
   * Allows consuming histogram and returning a result.
   * Histogram passed to the consumer includes stable, consistent view
   * of all values accumulated since last harvest.
   * This method is thread safe.
   * @param consumer
   * @return a result of a passed in function
   */
  public synchronized <T> T harvest(Function<Histogram, T> consumer) {
    initializeRecorder();
    _recycle = _recorder.getIntervalHistogram(_recycle);
    return consumer.apply(_recycle);
  }

}

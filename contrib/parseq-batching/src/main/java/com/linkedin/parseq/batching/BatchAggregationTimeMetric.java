package com.linkedin.parseq.batching;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.Recorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchAggregationTimeMetric {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchAggregationTimeMetric.class);

  private static final long LOWEST_DISCERNIBLE_VALUE = 1;
  private static final long HIGHEST_TRACKABLE_VALUE = TimeUnit.HOURS.toNanos(1);
  private static final int NUMBER_OF_FIGNIFICANT_VALUE_DIGITS = 3;

  private final Recorder _recorder =
      new Recorder(LOWEST_DISCERNIBLE_VALUE, HIGHEST_TRACKABLE_VALUE, NUMBER_OF_FIGNIFICANT_VALUE_DIGITS);

  private Histogram _recycle;

  /**
   * Records a batch aggregation time.
   * This method is thread safe.
   * @param batchAggregationTimeNano batch aggregation time
   */
  public void record(long batchAggregationTimeNano) {
    recordSafeValue(narrow(batchAggregationTimeNano));
  }

  private long narrow(long batchAggregationTimeNano) {
    if (batchAggregationTimeNano < LOWEST_DISCERNIBLE_VALUE) {
      LOGGER.warn("batch aggregation time lower than expected: {}, recording as: {}", batchAggregationTimeNano, LOWEST_DISCERNIBLE_VALUE);
      return LOWEST_DISCERNIBLE_VALUE;
    }
    if (batchAggregationTimeNano > HIGHEST_TRACKABLE_VALUE) {
      LOGGER.warn("batch aggregation time greater than expected: {}, recording as: {}", batchAggregationTimeNano, HIGHEST_TRACKABLE_VALUE);
      return HIGHEST_TRACKABLE_VALUE;
    }
    return batchAggregationTimeNano;
  }

  private void recordSafeValue(long batchAggregationTimeNano) {
    _recorder.recordValue(batchAggregationTimeNano);
  }

  /**
   * Allows consuming histogram and returning a result.
   * Histogram passed to the consumer includes stable, consistent view
   * of all values accumulated since last harvest.
   * This method is thread safe.
   * @param consumer consumer for a harvested histogram
   * @param <T> return type of a passed in function
   * @return a result of a passed in function
   */
  public synchronized <T> T harvest(Function<Histogram, T> consumer) {
    _recycle = _recorder.getIntervalHistogram(_recycle);
    return consumer.apply(_recycle);
  }
}

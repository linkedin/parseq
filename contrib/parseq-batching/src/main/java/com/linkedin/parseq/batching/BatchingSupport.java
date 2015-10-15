package com.linkedin.parseq.batching;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.linkedin.parseq.internal.PlanActivityListener;
import com.linkedin.parseq.internal.PlanContext;

public class BatchingSupport implements PlanActivityListener {

  private final List<BatchingStrategy<?, ?, ?>> _strategies =
      new CopyOnWriteArrayList<>();

  public void registerStrategy(BatchingStrategy<?, ?, ?> strategy) {
    _strategies.add(strategy);
  }

  @Override
  public void onPlanActivated(final PlanContext planContext) {
  }

  @Override
  public void onPlanDeactivated(final PlanContext planContext) {
    _strategies.forEach(strategy -> strategy.handleBatch(planContext));
  }

}

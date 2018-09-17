package com.linkedin.parseq.batching;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.internal.PlanDeactivationListener;

/**
 * This class allows registering instances of {@link BatchingStrategy}.
 * <p>
 * Please note that BatchingSupport must be registered with an {@link EngineBuilder} e.g.
 * <blockquote><pre>
 *  BatchingSupport batchingSupport = new BatchingSupport();
 *  engineBuilder.setPlanDeactivationListener(batchingSupport);
 *  (...)
 *  batchingSupport.registerStrategy(batchingStrategy);
 * </pre></blockquote>
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BatchingSupport implements PlanDeactivationListener {

  private final List<BatchingStrategy<?, ?, ?>> _strategies =
      new CopyOnWriteArrayList<>();

  /**
   * Register an instance of {@link BatchingStrategy}.
   * @param strategy strategy to be registered
   */
  public void registerStrategy(BatchingStrategy<?, ?, ?> strategy) {
    _strategies.add(strategy);
  }

  @Override
  public void onPlanDeactivated(final PlanContext planContext) {
    _strategies.forEach(strategy -> strategy.handleBatch(planContext));
  }

}

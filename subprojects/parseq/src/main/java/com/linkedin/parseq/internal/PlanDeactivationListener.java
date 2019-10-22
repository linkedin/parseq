package com.linkedin.parseq.internal;

import com.linkedin.parseq.EngineBuilder;

/**
 * This listener interface allows receiving notifications about plan deactivation.
 * It can be used for monitoring and to optimize task implementation e.g. to implement batching.
 * @see EngineBuilder#setPlanDeactivationListener(PlanDeactivationListener) for details
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public interface PlanDeactivationListener {

  void onPlanDeactivated(PlanContext planContext);

}

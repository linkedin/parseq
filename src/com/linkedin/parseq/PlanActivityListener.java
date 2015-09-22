package com.linkedin.parseq;

/**
 * This listener interface allows notification about plan activation
 * and deactivation. It can be used for monitoring and to optimize
 * task implementation e.g. to implement batching.
 * See {@link EngineBuilder#setPlanActivityListener(PlanActivityListener)}
 * for details.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public interface PlanActivityListener {

  void onPlanActivated(Long planId);

  void onPlanDeactivated(Long planId);

}

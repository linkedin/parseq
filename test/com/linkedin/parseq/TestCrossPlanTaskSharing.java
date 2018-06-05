package com.linkedin.parseq;

import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.promise.Promises;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * @author Min Chen (mnchen@linkedin.com)
 */
public class TestCrossPlanTaskSharing extends BaseEngineTest {
  private boolean _allowCrossPlanTaskSharing;

  @BeforeClass
  public void start() {
    _allowCrossPlanTaskSharing = ParSeqGlobalConfiguration.allowCrossPlanTaskSharing();
    ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(false);
  }

  @AfterClass
  public void stop() {
    ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(_allowCrossPlanTaskSharing);
  }


  @Test
  public void testTaskSharingByTwoPlans() throws InterruptedException {
    try {
      ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(false);

      Task<String> task = Task.value("shared", "Shared Constant");

      Task<String> plan1 = task.map(s -> s + " on earth!");
      Task<String> plan2 = task.map(s -> s + " on moon!");

      runAndWait("TestTaskReuse.testTaskSharingByTwoPlans-plan1", plan1);
      runAndWaitException("TestTaskReuse.testTaskSharingByTwoPlans-plan2", plan2, CrossPlanTaskSharingException.class);
    } finally {
      ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(true);
    }
  }

  @Test
  public void testFusionTaskSharingByTwoPlansWithRecover() throws InterruptedException {
    try {
      ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(false);

      Task<String> task = Task.value("shared", "Shared Constant");

      Task<String> plan1 = task.map(s -> s + " on earth!");
      Task<String> plan2 = task.recover(e -> "recover");
      Task<String> plan3 = task.map(s -> s + " on moon!").recover(e -> "recover");

      runAndWait("TestTaskReuse.testFusionTaskSharingByTwoPlans-plan1", plan1);
      assertEquals(plan1.get(), "Shared Constant on earth!");

      runAndWait("TestTaskReuse.testFusionTaskSharingByTwoPlans-plan2", plan2);
      assertEquals(plan2.get(), "recover");

      runAndWait("TestTaskReuse.testFusionTaskSharingByTwoPlans-plan3", plan3);
      assertEquals(plan3.get(), "recover");

    } finally {
      ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(true);
    }
  }

  //@Test
  // The fix cannot make this test work because we are using PromiseListener to notify task
  // following the shared task, and Promise for shared task can only be completed once with success,
  // we cannot override it with failure later when we detected cross-plan task sharing.
  public void testBaseTaskSharingByTwoPlansWithRecover() throws InterruptedException {
    try {
      ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(false);

      Task<String> task = Task.async("shared", () -> Promises.value("Shared Constant"));

      Task<String> plan1 = task.map(s -> s + " on earth!");
      //Task<String> plan2 = task.map(s -> s + " on moon!").recover(e -> "recover");
      Task<String> plan2 = task.recover(e -> "recover");

      runAndWait("TestTaskReuse.testFusionTaskSharingByTwoPlans-plan1", plan1);
      assertEquals(plan1.get(), "Shared Constant on earth!");

      runAndWait("TestTaskReuse.testFusionTaskSharingByTwoPlans-plan2", plan2);
      assertEquals(plan2.get(), "recover");
    } finally {
      ParSeqGlobalConfiguration.setAllowCrossPlanTaskSharing(true);
    }
  }
}

package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.Trace;

import java.util.List;

public class DisabledTraceCapturer implements TraceCapturer
{
  public static final TraceCapturer INSTANCE = new DisabledTraceCapturer();

  private DisabledTraceCapturer()
  {

  }

  @Override
  public int registerTask(Task<?> task)
  {
    return 0;
  }

  @Override
  public void setParent(int task, int parent)
  {

  }

  @Override
  public void addPotentialParent(int task, int parent)
  {

  }

  @Override
  public void addPredecessors(int successor, List<Integer> predecessors)
  {

  }

  @Override
  public void addPotentialPredecessors(int successor, List<Integer> predecessors)
  {

  }

  @Override
  public Trace getTrace()
  {
    return null;
  }
}

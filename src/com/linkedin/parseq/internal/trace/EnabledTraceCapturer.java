package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.internal.TaskListener;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class EnabledTraceCapturer implements TraceCapturer, TaskListener
{
  private final int _rootTaskId;

  private final Object _lock = new Object();
  private final Map<Integer, TraceData> _traces = new HashMap<Integer, TraceData>();
  private final WeakHashMap<Task<?>, Integer> _traceIdMap = new WeakHashMap<Task<?>, Integer>();
  private int _nextId = 0;

  public EnabledTraceCapturer(Task<?> rootTask)
  {
    _rootTaskId = tryRegisterTask(rootTask);
  }

  @Override
  public int registerTask(Task<?> task)
  {
    return tryRegisterTask(task);
  }

  @Override
  public void onUpdate(Task<?> task, ShallowTrace shallowTrace)
  {
    synchronized(_lock)
    {
      Integer traceId = _traceIdMap.get(task);
      if (traceId == null)
      {
        throw new IllegalArgumentException("Task was not previously registered with this capturer: " + task);
      }

      final TraceData entry = getTraceData(traceId);
      final ShallowTraceBuilder builder = new ShallowTraceBuilder(entry._shallowTrace);

      if (shallowTrace.getResultType() != ResultType.UNFINISHED)
      {
        builder.setResultType(shallowTrace.getResultType());
        builder.setValue(shallowTrace.getValue());
      }

      if (shallowTrace.getStartNanos() != null)
        builder.setStartNanos(shallowTrace.getStartNanos());

      if (shallowTrace.getPendingNanos() != null)
        builder.setPendingNanos(shallowTrace.getPendingNanos());

      if (shallowTrace.getEndNanos() != null)
        builder.setEndNanos(shallowTrace.getEndNanos());

      entry._shallowTrace = builder.build();
    }
  }

  @Override
  public void setParent(int task, int parent)
  {
    synchronized(_lock)
    {
      getTraceData(task)._parent = parent;
    }
  }

  @Override
  public void addPotentialParent(int task, int parent)
  {
    synchronized(_lock)
    {
      getTraceData(task)._potentialParents.add(parent);
    }
  }

  @Override
  public void addPredecessors(int successor, List<Integer> predecessors)
  {
    synchronized(_lock)
    {
      getTraceData(successor)._predecessors.addAll(predecessors);
    }
  }

  @Override
  public void addPotentialPredecessors(int successor, List<Integer> predecessors)
  {
    synchronized(_lock)
    {
      getTraceData(successor)._potentialPredecessors.addAll(predecessors);
    }
  }

  @Override
  public Trace getTrace()
  {
    synchronized(_lock)
    {
      // Here we need to:
      //
      // 1. Ensure that each task has a parent

      final MutableTrace traceGraph = new MutableTrace();

      for (Map.Entry<Integer, TraceData> entry : _traces.entrySet())
      {
        traceGraph.addTrace(entry.getKey(), entry.getValue()._shallowTrace);
      }

      for (Map.Entry<Integer, TraceData> entry : _traces.entrySet())
      {
        int id = entry.getKey();
        TraceData value = entry.getValue();

        boolean parentSet = false;
        if (value._parent != null)
        {
          traceGraph.addRelationship(value._parent, id, Relationship.PARENT_OF);
          parentSet = true;
        }

        for (int potentialParent : value._potentialParents)
        {
          // For backwards compatibility we arbitrarily set the parent of a task
          // to the first potential parent we find.
          if (!parentSet)
          {
            traceGraph.addRelationship(potentialParent, id, Relationship.PARENT_OF);
            parentSet = true;
          }
          else if (value._parent != null && potentialParent != value._parent)
          {
            traceGraph.addRelationship(potentialParent, id, Relationship.POTENTIAL_PARENT_OF);
          }
        }

        if (!parentSet && id != _rootTaskId)
        {
          traceGraph.addRelationship(_rootTaskId, id, Relationship.PARENT_OF);
        }

        for (int predecessor : value._predecessors)
        {
          traceGraph.addRelationship(id, predecessor, Relationship.SUCCESSOR_OF);
        }

        for (int potentialPredecessor : value._potentialPredecessors)
        {
          if (!value._predecessors.contains(potentialPredecessor))
          {
            traceGraph.addRelationship(id, potentialPredecessor, Relationship.POSSIBLE_SUCCESSOR_OF);
          }
        }
      }

      traceGraph.setSnapshotNanos(System.nanoTime());
      return traceGraph;
    }
  }

  // _lock must be held to call this method
  private TraceData getTraceData(int taskId)
  {
    return _traces.get(taskId);
  }

  private int tryRegisterTask(Task<?> task)
  {
    Integer id;
    boolean added = false;
    synchronized(_lock)
    {
      id = _traceIdMap.get(task);
      if (id == null)
      {
        id = _nextId++;
        _traceIdMap.put(task, id);
        _traces.put(id, new TraceData(task.getShallowTrace()));
        added = true;
      }
    }

    if (added)
    {
      task.addTaskListener(this);
    }

    return id;
  }

  private static class TraceData
  {
    private ShallowTrace _shallowTrace;
    private Integer _parent;

    private final Set<Integer> _potentialParents = new HashSet<Integer>();
    private final Set<Integer> _predecessors = new HashSet<Integer>();
    private final Set<Integer> _potentialPredecessors = new HashSet<Integer>();

    public TraceData(ShallowTrace shallowTrace)
    {
      _shallowTrace = shallowTrace;
    }
  }
}

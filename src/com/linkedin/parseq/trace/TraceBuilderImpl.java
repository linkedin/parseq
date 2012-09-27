/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq.trace;

import com.linkedin.parseq.EarlyFinishException;
import com.linkedin.parseq.Task;

import java.util.Set;

/**
 * An Object that can be used to construct {@link Trace}s for {@link Task}s.
 *
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class TraceBuilderImpl implements TraceBuilder
{
  @Override
  public Trace getTrace(Task<?> task)
  {
    final TraceRelationshipBuilder<Task<?>> traceBuilder = new TraceRelationshipBuilder<Task<?>>();
    addToTraceBuilder(traceBuilder, task);
    return traceBuilder.buildTrace(task);
  }

  private void addToTraceBuilder(final TraceRelationshipBuilder<Task<?>> traceBuilder, Task<?> task)
  {
    if (traceBuilder.containsKey(task))
    {
      return;
    }

    final ShallowTraceBuilder shallowBuilder = new ShallowTraceBuilder(task.getShallowTrace());
    shallowBuilder.setValue(valueToString(task));

    if (shallowBuilder.getResultType() == ResultType.UNFINISHED && shallowBuilder.getStartNanos() != null)
    {
      // If we've started a task, we need to bound its end time, even if
      // it's not finished.
      shallowBuilder.setEndNanos(System.nanoTime());
    }
    traceBuilder.addTrace(task, shallowBuilder.build());

    for (Related<Task<?>> related : task.getRelationships())
    {
      Relationship relationship = Relationship.valueOf(related.getRelationship());
      switch(relationship)
      {
        case SUCCESSOR_OF:
          addToTraceBuilder(traceBuilder, related.getRelated());
          if (traceBuilder.containsRelationship(Relationship.POSSIBLE_SUCCESSOR_OF.name(), task, related.getRelated()))
          {
            traceBuilder.removeRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, task, related.getRelated());
          }
          traceBuilder.addRelationship(Relationship.SUCCESSOR_OF, task, related.getRelated());
          break;
        case POSSIBLE_SUCCESSOR_OF:
          addToTraceBuilder(traceBuilder, related.getRelated());
          if (!traceBuilder.containsRelationship(Relationship.SUCCESSOR_OF.name(), task, related.getRelated()))
          {
            traceBuilder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, task, related.getRelated());
          }

          break;
        case CHILD_OF:
          addToTraceBuilder(traceBuilder, related.getRelated());
          traceBuilder.addRelationship(Relationship.PARENT_OF, related.getRelated(), task);
          break;
        case POTENTIAL_CHILD_OF:
          addToTraceBuilder(traceBuilder, related.getRelated());
          //make the potential as the parent if we do not have a parent.
          Relationship parentRelationship = getParent(task.getRelationships()) == related.getRelated()
                  ? Relationship.PARENT_OF
                  : Relationship.POTENTIAL_PARENT_OF;

          traceBuilder.addRelationship(parentRelationship, related.getRelated(), task);
          break;
        case POTENTIAL_PARENT_OF:
          final Task<?> parentRelated = getParent(related.getRelated().getRelationships());
          if (parentRelated != null && parentRelated == task)
          {
            addToTraceBuilder(traceBuilder, related.getRelated());
          }
          break;
        default:
          throw new IllegalStateException("Unknown relationship type: " + relationship);
      }
    }
  }

  private Task<?> getParent(Set<Related<Task<?>>> relationships)
  {
    Task<?> potentialParent = null;
    for(Related<Task<?>> related : relationships)
    {
      if (related.getRelationship().equals(Relationship.CHILD_OF.name()))
      {
        return related.getRelated();
      }
      else if (related.getRelationship().equals(Relationship.POTENTIAL_CHILD_OF.name()))
      {
        potentialParent = related.getRelated();
      }
    }
    return potentialParent;
  }

  private String valueToString(Task<?> task)
  {
    if (task.isDone())
    {
      if (task.isFailed())
      {
        if (task.getError() instanceof EarlyFinishException)
        {
          return null;
        }
        return task.getError().toString();
      }
      else
      {
        // TODO: consider bounding the size of the success value
        return task.get() == null ? null : task.get().toString();
      }
    }
    else
    {
      return null;
    }
  }
}

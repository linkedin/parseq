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

package com.linkedin.parseq.example.legacy.composite;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;

import static com.linkedin.parseq.Tasks.callable;

/**
 * The merge sort example demonstrates how branching and recursive plan
 * execution work. It is not intended as a model for doing parallel
 * computation!
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class MergeSortExample extends AbstractExample
{
  public static void main(String[] args) throws Exception
  {
    new MergeSortExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception
  {
    final int[] toSort = createRandomArray(10, new Random());

    final Task<int[]> mergeSort = new MergeSortPlan(toSort);
    engine.run(mergeSort);
    mergeSort.await();

    System.out.println("Before sort: " + Arrays.toString(toSort));
    System.out.println("After sort:  " + Arrays.toString(mergeSort.get()));

    ExampleUtil.printTracingResults(mergeSort);
  }

  private static int[] createRandomArray(final int arraySize, final Random random)
  {
    final int[] nums = new int[arraySize];
    for (int i = 0; i < arraySize; i++)
    {
      nums[i] = random.nextInt();
    }
    return nums;
  }

  private static class MergeSortPlan extends BaseTask<int[]>
  {
    private final int[] _toSort;
    private final Range _range;

    public MergeSortPlan(final int[] toSort)
    {
      this(toSort, new Range(0, toSort.length));
    }

    private MergeSortPlan(final int[] toSort, final Range range)
    {
      super("MergeSort " + range);
      _toSort = toSort;
      _range = range;
    }

    @Override
    public Promise<int[]> run(final Context ctx)
    {
      if (_range.size() == 0)
      {
        return Promises.value(new int[0]);
      }
      else if (_range.size() == 1)
      {
        return Promises.value(new int[] {_toSort[_range.start()]});
      }
      else
      {
        // Neither base case applied, so recursively split this problem into
        // smaller problems and then merge the results.
        final Range fstRange = _range.firstHalf();
        final Range sndRange = _range.secondHalf();
        final Task<int[]> fst = new MergeSortPlan(_toSort, fstRange);
        final Task<int[]> snd = new MergeSortPlan(_toSort, sndRange);
        final Task<int[]> merge = mergePlan(fstRange, fst, sndRange, snd);
        ctx.after(fst, snd).run(merge);
        ctx.run(fst, snd);
        return merge;
      }
    }

    private Task<int[]> mergePlan(final Range fstRange,
                                  final Promise<int[]> fstPromise,
                                  final Range sndRange,
                                  final Promise<int[]> sndPromise)
    {
      return callable("Merge " + fstRange + " " + sndRange, new Callable<int[]>()
      {
        @Override
        public int[] call() throws Exception
        {
          final int[] fst = fstPromise.get();
          final int[] snd = sndPromise.get();
          final int[] results = new int[fst.length + snd.length];
          for (int i = 0, l = 0, r = 0; i < results.length; i++)
          {
            if (l == fst.length)
              results[i] = snd[r++];
            else if (r == snd.length)
              results[i] = fst[l++];
            else
              results[i] = fst[l] < snd[r] ? fst[l++] : snd[r++];
          }
          return results;
        }
      });
    }
  }

  private static class Range
  {
    private final int _start;
    private final int _end;

    public Range(int start, int end)
    {
      _start = start;
      _end = end;
    }

    public int start()
    {
      return _start;
    }

    public Range firstHalf()
    {
      return new Range(_start, midpoint());
    }

    public Range secondHalf()
    {
      return new Range(midpoint(), _end);
    }

    public int size()
    {
      return _end - _start;
    }

    public String toString()
    {
      return  "[" + _start + "," + _end + ")";
    }

    private int midpoint()
    {
      return (_end - _start) / 2 + _start;
    }
  }
}

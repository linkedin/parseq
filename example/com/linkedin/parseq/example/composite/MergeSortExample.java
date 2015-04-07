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

package com.linkedin.parseq.example.composite;

import java.util.Arrays;
import java.util.Random;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.function.Tuples;


/**
 * The merge sort example demonstrates how branching and recursive plan
 * execution work. It is not intended as a model for doing parallel
 * computation!
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class MergeSortExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new MergeSortExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final int[] toSort = createRandomArray(10, new Random());

    final Task<int[]> mergeSort = mergeSort(toSort, new Range(0, toSort.length));
    engine.run(mergeSort);
    mergeSort.await();

    System.out.println("Before sort: " + Arrays.toString(toSort));
    System.out.println("After  sort: " + Arrays.toString(mergeSort.get()));
    Arrays.sort(toSort);
    System.out.println("Java   sort: " + Arrays.toString(toSort));

    ExampleUtil.printTracingResults(mergeSort);
  }

  private Task<int[]> mergeSort(final int[] toSort, final Range range) {
    if (range.size() == 0) {
      return Task.callable("leaf", () -> new int[0]);
    } else if (range.size() == 1) {
      return Task.callable("leaf", () -> new int[] { toSort[range.start()] });
    } else {
      // Neither base case applied, so recursively split this problem into
      // smaller problems and then merge the results.
      return Task.callable("split", () -> Tuples.tuple(range.firstHalf(), range.secondHalf()))
          .flatMap(ranges -> Task.par(mergeSort(toSort, ranges._1()), mergeSort(toSort, ranges._2())).map("merge",
              parts -> merge(ranges._1(), parts._1(), ranges._2(), parts._2())));
    }
  }

  public int[] merge(final Range fstRange, final int[] fstHalf, final Range sndRange, final int[] sndHalf) {
    final int[] fst = fstHalf;
    final int[] snd = sndHalf;
    final int[] results = new int[fst.length + snd.length];
    for (int i = 0, l = 0, r = 0; i < results.length; i++) {
      if (l == fst.length)
        results[i] = snd[r++];
      else if (r == snd.length)
        results[i] = fst[l++];
      else
        results[i] = fst[l] < snd[r] ? fst[l++] : snd[r++];
    }
    return results;
  }

  private int[] createRandomArray(final int arraySize, final Random random) {
    final int[] nums = new int[arraySize];
    for (int i = 0; i < arraySize; i++) {
      nums[i] = random.nextInt();
    }
    return nums;
  }

  private static class Range {
    private final int _start;
    private final int _end;

    public Range(int start, int end) {
      _start = start;
      _end = end;
    }

    public int start() {
      return _start;
    }

    public Range firstHalf() {
      return new Range(_start, midpoint());
    }

    public Range secondHalf() {
      return new Range(midpoint(), _end);
    }

    public int size() {
      return _end - _start;
    }

    public String toString() {
      return "[" + _start + "," + _end + ")";
    }

    private int midpoint() {
      return (_end - _start) / 2 + _start;
    }
  }
}

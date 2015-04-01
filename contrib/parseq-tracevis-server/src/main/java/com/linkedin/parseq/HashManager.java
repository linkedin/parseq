package com.linkedin.parseq;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class HashManager {

  private final Consumer<String> _evictor;
  private final Deque<String> _hashDeque = new ArrayDeque<>();
  private final Set<String> _hashSet = new HashSet<>();
  private final int _max;

  public HashManager(Consumer<String> evictor, int max) {
    _evictor = evictor;
    _max = max;
  }

  public synchronized boolean contains(String hash) {
    if (_hashSet.contains(hash)) {
      _hashDeque.remove(hash);
      _hashDeque.add(hash);
      return true;
    } else {
      return false;
    }
  }

  public synchronized void add(String hash) {
    if (!contains(hash)) {
      _hashSet.add(hash);
      _hashDeque.add(hash);
      if (_hashDeque.size() > _max) {
        _evictor.accept(_hashDeque.poll());
      }
    }
  }

}

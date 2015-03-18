package com.linkedin.parseq.internal;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Id generator that avoids inter-thread coordination.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public class IdGenerator {

  private static final long CHUNK_SIZE = 1000;
  private static final AtomicLong _nextFreeId = new AtomicLong();

  private static class Chunk {
    private long _current;
    private final long _max;

    public Chunk(long current, long max) {
      _current = current;
      _max = max;
    }

    public boolean isEmpty() {
      return _current == _max;
    }

    public long next() {
      return _current++;
    }
  }

  private static final ThreadLocal<Chunk> CHUNK = new ThreadLocal<Chunk>() {
    @Override
    protected Chunk initialValue() {
      return claim();
    };
  };

  private static Chunk claim() {
    long newMax = _nextFreeId.addAndGet(CHUNK_SIZE);
    return new Chunk(newMax - CHUNK_SIZE, newMax);
  }

  public static long getNextId() {
    Chunk chunk = CHUNK.get();
    if (chunk.isEmpty()) {
      chunk = claim();
      CHUNK.set(chunk);
    }
    return chunk.next();
  }
}

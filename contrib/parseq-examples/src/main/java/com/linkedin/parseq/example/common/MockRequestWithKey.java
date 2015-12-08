package com.linkedin.parseq.example.common;

public class MockRequestWithKey<K, RES> implements MockRequest<RES> {

  private final MockRequest<RES> _request;
  private final K _key;

  public MockRequestWithKey(K key, MockRequest<RES> request) {
    _key = key;
    _request = request;
  }

  @Override
  public long getLatency() {
    return _request.getLatency();
  }

  @Override
  public RES getResult() throws Exception {
    return _request.getResult();
  }

  public K getKey() {
    return _key;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_key == null) ? 0 : _key.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MockRequestWithKey other = (MockRequestWithKey) obj;
    if (_key == null) {
      if (other._key != null)
        return false;
    } else if (!_key.equals(other._key))
      return false;
    return true;
  }

}

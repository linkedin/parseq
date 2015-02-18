package com.linkedin.parseq.collection.async;

public interface Subscriber<T> {

  /**
   * Called before first element is sent to the Subscriber, allows stopping subscription before
   * even first element gets sent.
   * @param subscription
   */
  public void onSubscribe(Subscription subscription);

  /**
   * The Publisher calls this method to pass one element to this Subscriber. The element
   * must not be <code>null</code>.
   * @param element The element that is passed from publisher to subscriber.
   */
  public void onNext(T element);

  /**
   * The Publisher calls this method in order to signal that it terminated normally.
   * No more elements will be forthcoming and none of the Subscriber’s methods will be called hereafter.
   */
  public void onComplete();

  /**
   * The Publisher calls this method to signal that the stream of elements has failed
   * and is being aborted. The Subscriber should abort its processing as soon as possible.
   * No more elements will be forthcoming and none of the Subscriber’s methods will be called hereafter.
  * @param cause An exception which describes the reason for tearing down this stream.
   */
  public void onError(Throwable cause);

}

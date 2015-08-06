package com.linkedin.parseq;

/**
 * The class GraphvizResponse contains the HTTP response information of GraphvizEngine.
 */
public class GraphvizResponse {

  /**
   * The field _status is the HTTP response status code.
   */
  private final Integer _status;

  /**
   * The field _body is the HTTP response body.
   */
  private final String _body;

  /**
   * The constructor GraphvizResponse initializes both the status code and the body.
   *
   * @param status The HTTP response status code
   * @param body The HTTP response body
   */
  public GraphvizResponse(final Integer status, final String body) {
    this._status = status;
    this._body = body;
  }

  /**
   * The method getStatus returns the HTTP response status code.
   *
   * @return The HTTP response status code
   */
  public Integer getStatus() {
    return this._status;
  }

  /**
   * The method getBody returns the HTTP response body.
   *
   * @return the HTTP response body
   */
  public String getBody() {
    return this._body;
  }
}

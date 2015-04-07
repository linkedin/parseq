package com.linkedin.parseq.example.domain;

public class Message {
  final int _fromId;
  final int _toId;
  final String _title;
  final String _contents;

  public Message(int fromId, int toId, String title, String contents) {
    _fromId = fromId;
    _toId = toId;
    _title = title;
    _contents = contents;
  }

  public int getFromId() {
    return _fromId;
  }

  public int getToId() {
    return _toId;
  }

  public String getTitle() {
    return _title;
  }

  public String getContents() {
    return _contents;
  }

}

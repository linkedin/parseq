package com.linkedin.parseq.example.domain;

import java.util.List;

public class Person {

  final int _id;
  final String _firstName;
  final String _lastName;
  final int _companyId;
  final List<Integer> _connections;

  public Person(int id, String firstName, String lastName, int companyId, List<Integer> connections) {
    _firstName = firstName;
    _lastName = lastName;
    _companyId = companyId;
    _connections = connections;
    _id = id;
  }

  public String getFirstName() {
    return _firstName;
  }

  public String getLastName() {
    return _lastName;
  }

  public int getCompanyId() {
    return _companyId;
  }

  public List<Integer> getConnections() {
    return _connections;
  }

  public int getId() {
    return _id;
  }

  @Override
  public String toString() {
    return "Person [_id=" + _id + ", _firstName=" + _firstName + ", _lastName=" + _lastName + ", _companyId="
        + _companyId + ", _connections=" + _connections + "]";
  }


}

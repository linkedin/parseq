package com.linkedin.parseq.example.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB {

  public static final Map<Integer, Person> personDB = new HashMap<Integer, Person>() {
    private static final long serialVersionUID = 1L;
    {
      put(1, new Person(1, "Bob", "Shmidt", 1, Arrays.asList(2, 3)));
      put(2, new Person(2, "Garry", "Smith", 1, Arrays.asList(1)));
      put(3, new Person(3, "Scott", "Adams", 2, Arrays.asList(1, 4)));
      put(4, new Person(4, "Rick", "Evans", 3, Arrays.asList(2)));
    }
  };

  public static final List<Integer> personIds = new ArrayList<>(personDB.keySet());

  public static final Map<Integer, Company> companyDB = new HashMap<Integer, Company>() {
    private static final long serialVersionUID = 1L;
    {
      put(1, new Company("LinkedIn"));
      put(2, new Company("Twitter"));
      put(3, new Company("Google"));
    }
  };

  public static final Map<Integer, Message> messageDB = new HashMap<Integer, Message>() {
    private static final long serialVersionUID = 1L;
    {
      put(1, new Message(1, 2, "Hi", "Hi, how are you?"));
      put(2, new Message(2, 1, "Re: Hi", "Hi, I'm great!"));
      put(3, new Message(1, 3, "Meeting?", "Does 2pm work for you?"));
      put(4, new Message(3, 4, "Interesting", "Hi, Check this out!"));
    }
  };

  public static final Map<Integer, List<Integer>> mailboxDB = new HashMap<Integer, List<Integer>>() {
    private static final long serialVersionUID = 1L;
    {
      put(1, Arrays.asList(1, 2, 3));
      put(2, Arrays.asList(2));
      put(3, Arrays.asList(3, 4));
      put(4, Arrays.asList(4));
    }
  };

}

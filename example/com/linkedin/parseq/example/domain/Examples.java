/* $Id$ */
package com.linkedin.parseq.example.domain;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.collection.ParSeqCollections;
import com.linkedin.parseq.example.common.AbstractDomainExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.function.Tuples;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class Examples extends AbstractDomainExample
{
  public static void main(String[] args) throws Exception
  {
    new Examples().runExample();
  }

  //---------------------------------------------------------------

  //create summary for a person: "<first name> <last name>"
  Task<String> createSummary(int id) {
    return fetchPerson(id)
      .map(this::shortSummary);
  }

  String shortSummary(Person person) {
    return person.getFirstName() + " " + person.getLastName();
  }

  //---------------------------------------------------------------

  //handles failures delivering degraded experience
  Task<String> createResilientSummary(int id) {
    return fetchPerson(id)
        .map(this::shortSummary)
        .recover(e -> "Member " + id);
  }

  //---------------------------------------------------------------

  //handles failures delivering degraded experience in timely fashion
  Task<String> createResponsiveSummary(int id) {
    return fetchPerson(id)
        .withTimeout(100, TimeUnit.MILLISECONDS)
        .map(this::shortSummary)
        .recover(e -> "Member " + id);
  }

  //---------------------------------------------------------------

  /** Tasks composition */

  //create extended summary for a person: "<first name> <last name> working at <company name>"
  Task<String> createExtendedSummary(int id) {
    return fetchPerson(id).flatMap(this::createExtendedSummary);
  }

  Task<String> createExtendedSummary(final Person p) {
    return fetchCompany(p.getCompanyId())
        .map(company -> shortSummary(p) + " working at " + company.getName());
  }

  //---------------------------------------------------------------

  //create mailbox summary for a person: "<first name> <last name> has <X> messages"
  Task<String> createMailboxSummary(int id) {
    return Task.par(createSummary(id), fetchMailbox(id))
      .map((summary, mailbox) -> summary + " has " + mailbox.size() + " messages");

  }

  //---------------------------------------------------------------

  /** Task collections */

  //---------------------------------------------------------------

  //create summary of connections
  //<first name> <last name> working at <company name>
  //<first name> <last name> working at <company name>
  //(...)
  Task<String> createSummariesOfConnections(Integer id) {
    return fetchPerson(id)
      .flatMap(person -> createSummaries(person.getConnections()));
  }

  Task<String> createSummaries(List<Integer> ids) {
     return ParSeqCollections.fromValues(ids)
       .mapTask(id -> createExtendedSummary(id))
       .within(200, TimeUnit.MILLISECONDS)
       .reduce((a, b) -> a + "\n" + b);
  }

  //---------------------------------------------------------------

  //Find a message which contains given word
  Task<String> findMessageWithWord(String word) {
    return ParSeqCollections.fromValues(DB.personIds)
        .mapTask(id -> fetchMailbox(id))
        .flatMap(list -> ParSeqCollections.fromValues(list))
        .mapTask(msgId -> fetchMessage(msgId))
        .map(msg -> msg.getContents())
        .find(s -> s.contains(word));
  }

  //---------------------------------------------------------------

  //given list of their ids, get list of N People working at LinkedIn who have at least 2 connections and 1 message
  Task<List<Person>> getNPeopleWorkingAtLinkedIn(List<Integer> ids, int N) {
    return ParSeqCollections.fromValues(ids)
      .mapTask(id -> fetchPerson(id))
      .filter(person -> person.getConnections().size() > 2)
      .mapTask(person ->
        Task.par(fetchMailbox(person.getId()),fetchCompany(person.getCompanyId()))
          .map(tuple -> Tuples.tuple(person, tuple)))
      .filter(tuple -> tuple._2()._1().size() >= 1 &&
                       tuple._2()._2().getName().equals("LinkedIn"))
      .map(tuple -> tuple._1())
      .take(N)
      .toList();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    Task<String> task = createSummariesOfConnections(1);

    runTaskAndPrintResults(engine, task);
  }

  private void runTaskAndPrintResults(final Engine engine, Task<?> task) throws InterruptedException {
    Task<?> printRsults = task.andThen("println", System.out::println);
    engine.run(printRsults);
    printRsults.await();
    ExampleUtil.printTracingResults(printRsults);
  }

}

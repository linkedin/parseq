/* $Id$ */
package com.linkedin.parseq.example.domain;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.function.Tuple2;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class Examples extends AbstractDomainExample {

  public static void main(String[] args) throws Exception {
    new Examples(true).runExample();
  }

  public Examples(boolean useBatching) {
    super(useBatching);
  }


  //---------------------------------------------------------------

  //create summary for a person: "<first name> <last name>"
  Task<String> createSummary(int id) {
    return fetchPerson(id).map("shortSummary", this::shortSummary);
  }

  String shortSummary(Person person) {
    return person.getFirstName() + " " + person.getLastName();
  }

  //---------------------------------------------------------------

  //handles failures delivering degraded experience
  Task<String> createResilientSummary(int id) {
    return fetchPerson(id).map(this::shortSummary).recover(e -> "Member " + id);
  }

  //---------------------------------------------------------------

  //handles failures delivering degraded experience in timely fashion
  Task<String> createResponsiveSummary(int id) {
    return fetchPerson(id).withTimeout(100, TimeUnit.MILLISECONDS).map(this::shortSummary).recover(e -> "Member " + id);
  }

  //---------------------------------------------------------------

  /** Tasks composition */

  //create extended summary for a person: "<first name> <last name> working at <company name>"
  Task<String> createExtendedSummary(int id) {
    return fetchPerson(id).flatMap("createExtendedSummary", this::createExtendedSummary);
  }

  Task<String> createExtendedSummary(final Person p) {
    return fetchCompany(p.getCompanyId()).map("summary", company -> shortSummary(p) + " working at " + company.getName());
  }

  //---------------------------------------------------------------

  //create mailbox summary for a person: "<first name> <last name> has <X> messages"
  Task<String> createMailboxSummary(int id) {
    return Task.par(createExtendedSummary(id), fetchMailbox(id))
        .map("createMailboxSummary", (summary, mailbox) -> summary + " has " + mailbox.size() + " messages");
  }

  //create list of summaries, one per each connection
  Task<List<String>> createConnectionsSummaries(int id) {
    return fetchPerson(id).flatMap("createConnectionsSummaries", person -> createConnectionsSummaries(person.getConnections()));
  }

  Task<List<String>> createConnectionsSummaries(List<Integer> connections) {
    return Tasks.par(createConnectionsSummariesTasks(connections));
  }

  List<Task<String>> createConnectionsSummariesTasks(List<Integer> connections) {
    return connections.stream().map(this::createExtendedSummary).collect(Collectors.toList());
  }

  //---------------------------------------------------------------

  Task<Tuple2<String, List<String>>> createFullSummary(int id) {
    return Task.par(createMailboxSummary(id), createConnectionsSummaries(id));
  }

  //---------------------------------------------------------------

  /** Task collections */

  //---------------------------------------------------------------

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    Task<?> task = Task.par(createFullSummary(2), createFullSummary(3));

    runTaskAndPrintResults(engine, task);
  }

  private void runTaskAndPrintResults(final Engine engine, Task<?> task) throws InterruptedException {
    engine.run(task);
    task.await();
    System.out.println(task.get());
    ExampleUtil.printTracingResults(task);
  }

}

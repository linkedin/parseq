/* $Id$ */
package com.linkedin.parseq.example.domain;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.ExampleUtil;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class Examples extends AbstractDomainExample {
  public static void main(String[] args) throws Exception {
    new Examples().runExample();
  }

  //---------------------------------------------------------------

  //create summary for a person: "<first name> <last name>"
  Task<String> createSummary(int id) {
    return fetchPerson(id).map(this::shortSummary);
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
    return fetchPerson(id).flatMap(this::createExtendedSummary);
  }

  Task<String> createExtendedSummary(final Person p) {
    return fetchCompany(p.getCompanyId()).map(company -> shortSummary(p) + " working at " + company.getName());
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

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    Task<String> task = createSummary(1);

    runTaskAndPrintResults(engine, task);
  }

  private void runTaskAndPrintResults(final Engine engine, Task<?> task) throws InterruptedException {
    Task<?> printRsults = task.andThen("println", System.out::println);
    engine.run(printRsults);
    printRsults.await();
    ExampleUtil.printTracingResults(printRsults);
  }

}

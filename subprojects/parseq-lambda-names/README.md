Parseq Lambda Names
==========================

One of the fields in Parseq trace is task name, an optional, human readable field. The intention behind this field was to:
* Provide developers a way to "uniquely" identify a task across different Parseq traces. This is critical for running any analysis like figure out that longest task from Parseq traces for an API call.
* Make it easier for developer to go to source code provided the name. This helps in debugging issues like why a task failed etc.

Parseq uses generated Lambda class name as default value for name field. This was not working for multiple reasons:
* Lambda classes are generated at runtime, so although Lambda class name would be unique across traces sent from one instance of the service, it would not be the same as name in traces emitted from other instances.
* There was no way to point to task in source code by looking at name.

This project aims to provide more meaningful default descriptions for Parseq tasks. Using ASM, this project tries to locate where lambda expression is defined in source code and also infer some details about its execution like function call within lambda expression with number of arguments.

Examples
==========================
For the following code:
```java
  public Task<BatchResult<Long, JobPosting>> fetchJobPostings(Set<Long> jobPostingIds, PathSpecSet projectionSet) { 
    return decorator.batchGet(jobPostingIds, new JobPostingsContext(Optional.empty()), projectionSet) 
        .andThen(batchResult -> 
          updateJobPostingDecoratorSensor(jobPostingIds.size(), batchResult.getErrors().size()) 
        ) 
        .onFailure(t -> updateJobPostingDecoratorSensor(jobPostingIds.size(), jobPostingIds.size())); 
  } 
```

| Before | After |
| ------ | ----- |
| `andThen: com.linkedin.voyager.jobs.services.JobPostingsService$$Lambda$2760/928179328` | `andThen: fetchJobPostings(JobPostingsService:112)` |
| `onFailure: com.linkedin.voyager.jobs.services.JobPostingsService$$Lambda$2761/813689833` | `onFailure: fetchJobPostings(JobPostingsService:114)` |

For the following code:

```java
    return lixTreatmentsTask.map(lixTreatments -> MapHelpers.mergeMaps(lixTreatments, lixOverrides)) 
      .map(treatmentsWithOverrides -> lixTestKeys.stream().filter(k -> treatmentsWithOverrides.containsKey(k.getKey())) 
          .collect(Collectors.toMap(Function.identity(), k -> treatmentsWithOverrides.get(k.getKey())))); 
```

| Before | After |
| ------ | ----- |
| `map: com.linkedin.pemberly.api.server.lix.LixServiceImpl$$Lambda$1211/1604155334` | `map: MapHelpers.mergeMaps(_,_) fetchTreatments(LixServiceImpl:124)` |


How to use
==========================

The shaded jar of parseq-lambda-names should be present on classpath along with parseq jar in order to analyze
generated Lambda classes once when Lambda is executed for first time. If parseq-lambda-names jar is not present
on classpath, then parseq will behave as usual i.e. uses Lambda class name as task description.

Limitations
==========================

As this project uses ASM to analyze generated Lambda bytecode, it is a very fragile mechanism that can potentially break between minor JVM versions.
Currently its tested for jvm versions: 1.8.0_5, 1.8.0_40, 1.8.0_72

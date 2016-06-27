### Id Generation

commnand:
```
mvn clean install
java -jar target/benchmarks.jar  ".*" -t 4
```

result:
```
(...)

Result "getNextId":
  46790699.050 ±(99.9%) 407080.865 ops/s [Average]
  (min, avg, max) = (40626145.225, 46790699.050, 51498108.670), stdev = 1723605.871
  CI (99.9%): [46383618.186, 47197779.915] (assumes normal distribution)


Run complete. Total time: 00:13:27

Benchmark                                Mode  Cnt          Score         Error  Units
IdGeneratorBenchmark.getNextId      thrpt  200  331533289.736 ± 3360147.670  ops/s
LongIdGeneratorBenchmark.getNextId  thrpt  200   46790699.050 ±  407080.865  ops/s
```
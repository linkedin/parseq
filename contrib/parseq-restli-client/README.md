ParSeq Rest.li Client
==========================

This project provides implementation of ParSeq rest.li client.

Configuration
=============
ParSeq rest.li client implementation allows configuration of the following properties:
 * timeoutMs (long) - timeout in milliseconds
 * batchingEnabled (boolean) - is batching enabled
 * maxBatchSize (int) - max batch size

Each property is defined by a set of Key-Value pairs where Key has the following form:

```
<INBOUND_RESOURCE>.<OPERATION>/<OUTBOUND_RESOURCE>.<OPERATION>
```

Every part of the Key can be substituted with wildcard symbol: \*.
More formally, Key is specified by the following grammar:

```
grammar RequestConfigKey;

key             : inbound '/' outbound EOF;
inbound         : ( Name | '*' ) '.' ( operationIn | '*' );
outbound        : ( Name | '*' ) '.' ( operationOut | '*' );
operationIn     : simpleOp | complex | httpExtraOp;
operationOut    : simpleOp | complex;
simpleOp        : 'GET' | 'BATCH_GET' | 'CREATE' | 'BATCH_CREATE' |
                  'PARTIAL_UPDATE' | 'UPDATE' | 'BATCH_UPDATE' |
                  'DELETE' | 'BATCH_PARTIAL_UPDATE' | 'BATCH_DELETE' |
                  'GET_ALL' | 'OPTIONS';
httpExtraOp     : 'HEAD' | 'POST' | 'PUT' | 'TRACE' | 'CONNECT';
complex         : complexOp '-' ( Name | '*' );
complexOp       : 'FINDER' | 'ACTION';
Name            : [a-zA-Z0-9]+;
```

Examples:
```
*.*/*.*            fallback configuration
*.*/*.GET          configuration for all outgoing GET requests
profileView.*/*.*  configuration for all downstream requests if 'profileView' resource was called
```

The format consists of fixed number of parts, is explicit and resembles familiar file-path structure to make it easier for humans to understand and manipulate.

Each key is assigned a priority and key with highest priority is used at runtime. General principle behind priorities is that more specific key should have higher priority than less specific one. More formally, the following rules apply:
* resource name is more specific than operation type
* outbound resource is more specific than inbound resource

What follows is that each part of the key can be assigned a priority score where higher priority means it is more specific:

```
<2>.<0>/<3>.<1>
```

It means that outbound resource name is most specific part of the key and operation type of inbound resource is least specific.

Defining priorities this way makes them unambiguous - there is a deterministic order for all applicable keys for every request. In other words, the decision which key will be used is structurally deterministic and does not depend on order of the keys in configuration source.

In examples below, keys are sorted by their priority (highest priority - most specific ones are on top):

```
profileView.*/profile.FINDER-firstDegree
*.*/profile.GET
profileView.*/*.*
*.*/*.GET
*.*/*.*
```


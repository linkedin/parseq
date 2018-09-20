ParSeq Rest.li Client
==========================

This project provides implementation of ParSeq rest.li client. It provides two features on top of regular rest.li client:
 * Batching
 * Configuration

Batching
========

ParSeq rest.li client is using ParSeq [Batching](https://github.com/linkedin/parseq/tree/master/subprojects/parseq-batching) feature to transparently aggregate individual requests into BATCH requests. Currently only GET and BATCH_GET operations are supported. Batching functionality can be selectively enabled for subset of requests made by ParSeq rest.li client.

Configuration
=============
ParSeq rest.li client implementation allows fine-grained configuration of the following properties:
 * timeoutMs (long) - timeout in milliseconds. Returned Task will complete with TimeoutException if response is not available within specified amount of time. TimeoutException contains information about what configuration has caused it.
 * batchingEnabled (boolean) - is batching enabled. Enables batching functionality for specified subset of requests. See [Batching](https://github.com/linkedin/parseq/tree/master/subprojects/parseq-batching) for more information about this feature. Currently only GET and BATCH_GET operations are supported.
 * maxBatchSize (int) - Max batch size. Maximum number of keys that will be aggregated together into one BATCH request. If there are more requests that can be batched then they will be grouped into number of BATCH requests, for example, if maxBatchSize is 100 and there are 256 GET requests, then they will be aggregated into 3 BATCH_GET requests containing respectively: 100, 100, 56 elements. However, maxBatchSize does not affect existing rest.li BATCH_GET requests, and it is only used to limit size of batch requests created as a result of aggregation. For example, if maxBatchSize is 100 and there are a BATCH_GET request with 120 elements and 120 GET requests, then they will be aggregated into 3 BATCH_GET requests containing respectively 120, 100, 20 elements. Note that the original 120-element BATCH_GET request will not be splitted into smaller batches.

Each property is defined by a set of Key-Value pairs where Key has the following form:

```
<INBOUND_RESOURCE>.<OPERATION>/<OUTBOUND_RESOURCE>.<OPERATION>
```

Every part of the Key can be substituted with wildcard symbol: `*`.

Inbound and outbound resource names may consist of more than one part if they refer to a sub-resources. In this case path components are separated with a `:` symbol. If name consist of one part and is a name of a parent resource then it will apply to all it's sub-resources.  

More formally, Key is specified by the following grammar:

```
grammar RequestConfigKey;

key 				: inbound '/' outbound EOF;
inbound			: ( restResource | '*' ) '.' ( operationIn | '*' );
outbound			: ( restResource | '*' ) '.' ( operationOut | '*' );
restResource		: Name ( '-' Name )* ( ':' Name )*;
operationIn		: simpleOp | complex | httpExtraOp;
operationOut		: simpleOp | complex;
simpleOp   		: 'GET' | 'BATCH_GET' | 'CREATE' | 'BATCH_CREATE' |
				  'PARTIAL_UPDATE' | 'UPDATE' | 'BATCH_UPDATE' |
				  'DELETE' | 'BATCH_PARTIAL_UPDATE' | 'BATCH_DELETE' |
				  'GET_ALL' | 'OPTIONS';
httpExtraOp		: 'HEAD' | 'POST' | 'PUT' | 'TRACE' | 'CONNECT';
complex 			: complexOp '-' ( Name | '*' );
complexOp   		: 'FINDER' | 'ACTION';
Name 			: [a-zA-Z0-9]+;
```

Examples:
```
*.*/*.*                fallback configuration
*.*/*.GET              configuration for all outgoing GET requests
*.*/assets:media.GET   configuration for all outgoing GET requests to assets/media sub-resource
*.*/assets.GET         configuration for all outgoing GET requests to all assets resource
                       and all it's sub-resources
profileView.*/*.*      configuration for all downstream requests if 'profileView' resource was called
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


grammar ResourceConfigKey;

key 			: path '/' property;
property 		: 'timeoutNs' | 'batchingEnabled' | 'maxBatchSize' | 'batchingDryRun';
path 			: ( inbound  | '*' ) '/' (outbound | '*');
inbound			: ( Name | '*' ) '.' ( operation | '*' );
outbound		: ( Name | '*' ) '.' ( operation | '*' );
operation		: simpleOp | complex;
simpleOp   		: 'GET' | 'BATCH_GET' | 'CREATE' | 'BATCH_CREATE' |
				  'PARTIAL_UPDATE' | 'UPDATE' | 'BATCH_UPDATE' |
				  'DELETE' | 'BATCH_PARTIAL_UPDATE' | 'BATCH_DELETE' |
				  'GET_ALL' | 'OPTIONS';
complex 		: complexOp '-' ( Name | '*' );
complexOp   	: 'FINDER' | 'ACTION';
Name 			: [a-zA-Z0-9]+;


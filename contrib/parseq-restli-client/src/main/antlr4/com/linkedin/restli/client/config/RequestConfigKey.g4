grammar RequestConfigKey;

key 			: inbound '/' outbound EOF;
inbound			: ( Name | '*' ) '.' ( operationIn | '*' );
outbound		: ( Name | '*' ) '.' ( operationOut | '*' );
operationIn		: simpleOp | complex | httpExtraOp;
operationOut    : simpleOp | complex;
simpleOp   		: 'GET' | 'BATCH_GET' | 'CREATE' | 'BATCH_CREATE' |
				  'PARTIAL_UPDATE' | 'UPDATE' | 'BATCH_UPDATE' |
				  'DELETE' | 'BATCH_PARTIAL_UPDATE' | 'BATCH_DELETE' |
				  'GET_ALL' | 'OPTIONS';
httpExtraOp     : 'HEAD' | 'POST' | 'PUT' | 'TRACE' | 'CONNECT';
complex 		: complexOp '-' ( Name | '*' );
complexOp   	: 'FINDER' | 'ACTION';
Name 			: [a-zA-Z0-9]+;

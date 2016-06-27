
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * Class for testing the CREATE method that returns the entity.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.CreateGreetingResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.createGreeting.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CreateGreetingRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "createGreeting";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public CreateGreetingRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CreateGreetingRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public CreateGreetingRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CreateGreetingRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public CreateGreetingBatchCreateRequestBuilder batchCreate() {
        return new CreateGreetingBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CreateGreetingBatchCreateAndGetRequestBuilder batchCreateAndGet() {
        return new CreateGreetingBatchCreateAndGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CreateGreetingCreateRequestBuilder create() {
        return new CreateGreetingCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CreateGreetingCreateAndGetRequestBuilder createAndGet() {
        return new CreateGreetingCreateAndGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}


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
import com.linkedin.restli.examples.greetings.api.ValidationDemo;


/**
 * Free-form resource for testing Rest.li data validation.
 *  This class shows how to validate data manually by injecting the validator as a resource method parameter.
 *  Outgoing data that fails validation is corrected before it is sent to the client.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ValidationDemoResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.validationDemos.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ValidationDemosRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "validationDemos";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Integer.class, null, null, ValidationDemo.class, keyParts);
    }

    public ValidationDemosRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ValidationDemosRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ValidationDemosRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ValidationDemosRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ValidationDemosBatchGetRequestBuilder batchGet() {
        return new ValidationDemosBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosBatchUpdateRequestBuilder batchUpdate() {
        return new ValidationDemosBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosGetAllRequestBuilder getAll() {
        return new ValidationDemosGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosBatchCreateRequestBuilder batchCreate() {
        return new ValidationDemosBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosCreateRequestBuilder create() {
        return new ValidationDemosCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosUpdateRequestBuilder update() {
        return new ValidationDemosUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosGetRequestBuilder get() {
        return new ValidationDemosGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new ValidationDemosBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosPartialUpdateRequestBuilder partialUpdate() {
        return new ValidationDemosPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosFindBySearchRequestBuilder findBySearch() {
        return new ValidationDemosFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

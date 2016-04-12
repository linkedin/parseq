
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
 *  This class shows how to validate data automatically by using the validation filters.
 *  Invalid incoming data or outgoing data are rejected, and an error response is returned to the client.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.AutomaticValidationDemoResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.autoValidationDemos.restspec.json.", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AutoValidationDemosRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "autoValidationDemos";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Integer.class, null, null, ValidationDemo.class, keyParts);
    }

    public AutoValidationDemosRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AutoValidationDemosRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public AutoValidationDemosRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AutoValidationDemosRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public AutoValidationDemosBatchGetRequestBuilder batchGet() {
        return new AutoValidationDemosBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchUpdateRequestBuilder batchUpdate() {
        return new AutoValidationDemosBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosGetAllRequestBuilder getAll() {
        return new AutoValidationDemosGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchCreateRequestBuilder batchCreate() {
        return new AutoValidationDemosBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchCreateAndGetRequestBuilder batchCreateAndGet() {
        return new AutoValidationDemosBatchCreateAndGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosCreateRequestBuilder create() {
        return new AutoValidationDemosCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosCreateAndGetRequestBuilder createAndGet() {
        return new AutoValidationDemosCreateAndGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosUpdateRequestBuilder update() {
        return new AutoValidationDemosUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosGetRequestBuilder get() {
        return new AutoValidationDemosGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new AutoValidationDemosBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosPartialUpdateRequestBuilder partialUpdate() {
        return new AutoValidationDemosPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosFindBySearchRequestBuilder findBySearch() {
        return new AutoValidationDemosFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

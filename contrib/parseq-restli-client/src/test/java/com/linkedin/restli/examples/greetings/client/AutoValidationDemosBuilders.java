
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.ValidationDemo;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Free-form resource for testing Rest.li data validation.
 *  This class shows how to validate data automatically by using the validation filters.
 *  Invalid incoming data or outgoing data are rejected, and an error response is returned to the client.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.AutomaticValidationDemoResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.autoValidationDemos.restspec.json.", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AutoValidationDemosBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "autoValidationDemos";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Integer.class, null, null, ValidationDemo.class, keyParts);
    }

    public AutoValidationDemosBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AutoValidationDemosBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public AutoValidationDemosBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AutoValidationDemosBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = primaryResourceName;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    private String getBaseUriTemplate() {
        return _baseUriTemplate;
    }

    public RestliRequestOptions getRequestOptions() {
        return _requestOptions;
    }

    public String[] getPathComponents() {
        return URIParamUtils.extractPathComponentsFromUriTemplate(_baseUriTemplate);
    }

    private static RestliRequestOptions assignRequestOptions(RestliRequestOptions requestOptions) {
        if (requestOptions == null) {
            return RestliRequestOptions.DEFAULT_OPTIONS;
        } else {
            return requestOptions;
        }
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public AutoValidationDemosBatchGetBuilder batchGet() {
        return new AutoValidationDemosBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchUpdateBuilder batchUpdate() {
        return new AutoValidationDemosBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosGetAllBuilder getAll() {
        return new AutoValidationDemosGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchCreateBuilder batchCreate() {
        return new AutoValidationDemosBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchCreateAndGetBuilder batchCreateAndGet() {
        return new AutoValidationDemosBatchCreateAndGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosCreateBuilder create() {
        return new AutoValidationDemosCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosCreateAndGetBuilder createAndGet() {
        return new AutoValidationDemosCreateAndGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosUpdateBuilder update() {
        return new AutoValidationDemosUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosGetBuilder get() {
        return new AutoValidationDemosGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosBatchPartialUpdateBuilder batchPartialUpdate() {
        return new AutoValidationDemosBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosPartialUpdateBuilder partialUpdate() {
        return new AutoValidationDemosPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AutoValidationDemosFindBySearchBuilder findBySearch() {
        return new AutoValidationDemosFindBySearchBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

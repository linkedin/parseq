
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
 *  This class shows how to validate data manually by injecting the validator as a resource method parameter.
 *  Outgoing data that fails validation is corrected before it is sent to the client.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ValidationDemoResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.validationDemos.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ValidationDemosBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "validationDemos";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Integer.class, null, null, ValidationDemo.class, keyParts);
    }

    public ValidationDemosBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ValidationDemosBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public ValidationDemosBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ValidationDemosBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public ValidationDemosBatchGetBuilder batchGet() {
        return new ValidationDemosBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosBatchUpdateBuilder batchUpdate() {
        return new ValidationDemosBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosGetAllBuilder getAll() {
        return new ValidationDemosGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosBatchCreateBuilder batchCreate() {
        return new ValidationDemosBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosCreateBuilder create() {
        return new ValidationDemosCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosUpdateBuilder update() {
        return new ValidationDemosUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosGetBuilder get() {
        return new ValidationDemosGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosBatchPartialUpdateBuilder batchPartialUpdate() {
        return new ValidationDemosBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosPartialUpdateBuilder partialUpdate() {
        return new ValidationDemosPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ValidationDemosFindBySearchBuilder findBySearch() {
        return new ValidationDemosFindBySearchBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

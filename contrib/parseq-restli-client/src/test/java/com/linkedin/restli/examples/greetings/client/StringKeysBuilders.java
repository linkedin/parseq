
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
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Demonstrates a resource keyed by a string.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.StringKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.stringKeys.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class StringKeysBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "stringKeys";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE), requestMetadataMap, responseMetadataMap, java.lang.String.class, null, null, Message.class, keyParts);
    }

    public StringKeysBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public StringKeysBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public StringKeysBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public StringKeysBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = primaryResourceName;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    private java.lang.String getBaseUriTemplate() {
        return _baseUriTemplate;
    }

    public RestliRequestOptions getRequestOptions() {
        return _requestOptions;
    }

    public java.lang.String[] getPathComponents() {
        return URIParamUtils.extractPathComponentsFromUriTemplate(_baseUriTemplate);
    }

    private static RestliRequestOptions assignRequestOptions(RestliRequestOptions requestOptions) {
        if (requestOptions == null) {
            return RestliRequestOptions.DEFAULT_OPTIONS;
        } else {
            return requestOptions;
        }
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public StringKeysBatchGetBuilder batchGet() {
        return new StringKeysBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchUpdateBuilder batchUpdate() {
        return new StringKeysBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchDeleteBuilder batchDelete() {
        return new StringKeysBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysDeleteBuilder delete() {
        return new StringKeysDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchCreateBuilder batchCreate() {
        return new StringKeysBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysCreateBuilder create() {
        return new StringKeysCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysUpdateBuilder update() {
        return new StringKeysUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysGetBuilder get() {
        return new StringKeysGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchPartialUpdateBuilder batchPartialUpdate() {
        return new StringKeysBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysPartialUpdateBuilder partialUpdate() {
        return new StringKeysPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysFindBySearchBuilder findBySearch() {
        return new StringKeysFindBySearchBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

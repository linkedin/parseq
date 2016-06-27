
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
import com.linkedin.restli.examples.greetings.api.Message;


/**
 * Demonstrates a resource keyed by a string.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.StringKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.stringKeys.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class StringKeysRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "stringKeys";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE), requestMetadataMap, responseMetadataMap, java.lang.String.class, null, null, Message.class, keyParts);
    }

    public StringKeysRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public StringKeysRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public StringKeysRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public StringKeysRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public StringKeysBatchGetRequestBuilder batchGet() {
        return new StringKeysBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchUpdateRequestBuilder batchUpdate() {
        return new StringKeysBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchDeleteRequestBuilder batchDelete() {
        return new StringKeysBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysDeleteRequestBuilder delete() {
        return new StringKeysDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchCreateRequestBuilder batchCreate() {
        return new StringKeysBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysCreateRequestBuilder create() {
        return new StringKeysCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysUpdateRequestBuilder update() {
        return new StringKeysUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysGetRequestBuilder get() {
        return new StringKeysGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new StringKeysBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysPartialUpdateRequestBuilder partialUpdate() {
        return new StringKeysPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StringKeysFindBySearchRequestBuilder findBySearch() {
        return new StringKeysFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

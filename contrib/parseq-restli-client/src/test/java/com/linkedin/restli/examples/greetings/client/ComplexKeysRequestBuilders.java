
package com.linkedin.restli.examples.greetings.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;


/**
 * Demonstrates a resource with a complex key.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ComplexKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.complexKeys.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexKeysRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "complexKeys";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> entityActionParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("entityAction", new DynamicRecordMetadata("entityAction", entityActionParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("entityAction", new DynamicRecordMetadata("entityAction", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, TwoPartKey.class, TwoPartKey.class, Message.class, keyParts);
    }

    public ComplexKeysRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ComplexKeysRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ComplexKeysBatchGetRequestBuilder batchGet() {
        return new ComplexKeysBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchUpdateRequestBuilder batchUpdate() {
        return new ComplexKeysBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchDeleteRequestBuilder batchDelete() {
        return new ComplexKeysBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysGetAllRequestBuilder getAll() {
        return new ComplexKeysGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchCreateRequestBuilder batchCreate() {
        return new ComplexKeysBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysCreateRequestBuilder create() {
        return new ComplexKeysCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysGetRequestBuilder get() {
        return new ComplexKeysGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new ComplexKeysBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysPartialUpdateRequestBuilder partialUpdate() {
        return new ComplexKeysPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysFindByPrefixRequestBuilder findByPrefix() {
        return new ComplexKeysFindByPrefixRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysDoEntityActionRequestBuilder actionEntityAction() {
        return new ComplexKeysDoEntityActionRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

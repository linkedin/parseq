
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
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Demonstrates a resource with a complex key.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ComplexKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.complexKeys.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexKeysBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public ComplexKeysBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public ComplexKeysBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public ComplexKeysBatchGetBuilder batchGet() {
        return new ComplexKeysBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchUpdateBuilder batchUpdate() {
        return new ComplexKeysBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchDeleteBuilder batchDelete() {
        return new ComplexKeysBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysGetAllBuilder getAll() {
        return new ComplexKeysGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchCreateBuilder batchCreate() {
        return new ComplexKeysBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysCreateBuilder create() {
        return new ComplexKeysCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysGetBuilder get() {
        return new ComplexKeysGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysBatchPartialUpdateBuilder batchPartialUpdate() {
        return new ComplexKeysBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysPartialUpdateBuilder partialUpdate() {
        return new ComplexKeysPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysFindByPrefixBuilder findByPrefix() {
        return new ComplexKeysFindByPrefixBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexKeysDoEntityActionBuilder actionEntityAction() {
        return new ComplexKeysDoEntityActionBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

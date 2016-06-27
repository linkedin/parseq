
package com.linkedin.restli.examples.greetings.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.data.template.StringArray;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Tests to observe restli's resilience for resource methods returning null. We are simply reusing
 *  the Greetings model here for our own null-generating purposes.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.NullGreetingsResourceImpl
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.nullGreeting.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class NullGreetingBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "nullGreeting";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> returnActionResultWithNullStatusParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnActionResultWithNullStatus", new DynamicRecordMetadata("returnActionResultWithNullStatus", returnActionResultWithNullStatusParams));
        ArrayList<FieldDef<?>> returnActionResultWithNullValueParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnActionResultWithNullValue", new DynamicRecordMetadata("returnActionResultWithNullValue", returnActionResultWithNullValueParams));
        ArrayList<FieldDef<?>> returnNullActionResultParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnNullActionResult", new DynamicRecordMetadata("returnNullActionResult", returnNullActionResultParams));
        ArrayList<FieldDef<?>> returnNullStringArrayParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnNullStringArray", new DynamicRecordMetadata("returnNullStringArray", returnNullStringArrayParams));
        ArrayList<FieldDef<?>> returnStringArrayWithNullElementParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnStringArrayWithNullElement", new DynamicRecordMetadata("returnStringArrayWithNullElement", returnStringArrayWithNullElementParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("returnActionResultWithNullStatus", new DynamicRecordMetadata("returnActionResultWithNullStatus", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("returnActionResultWithNullValue", new DynamicRecordMetadata("returnActionResultWithNullValue", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("returnNullActionResult", new DynamicRecordMetadata("returnNullActionResult", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("returnNullStringArray", new DynamicRecordMetadata("returnNullStringArray", Collections.singletonList(new FieldDef<StringArray>("value", StringArray.class, DataTemplateUtil.getSchema(StringArray.class)))));
        responseMetadataMap.put("returnStringArrayWithNullElement", new DynamicRecordMetadata("returnStringArrayWithNullElement", Collections.singletonList(new FieldDef<StringArray>("value", StringArray.class, DataTemplateUtil.getSchema(StringArray.class)))));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public NullGreetingBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public NullGreetingBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public NullGreetingBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public NullGreetingBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public NullGreetingBatchGetBuilder batchGet() {
        return new NullGreetingBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchUpdateBuilder batchUpdate() {
        return new NullGreetingBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchDeleteBuilder batchDelete() {
        return new NullGreetingBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingGetAllBuilder getAll() {
        return new NullGreetingGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingDeleteBuilder delete() {
        return new NullGreetingDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchCreateBuilder batchCreate() {
        return new NullGreetingBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingCreateBuilder create() {
        return new NullGreetingCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingUpdateBuilder update() {
        return new NullGreetingUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingGetBuilder get() {
        return new NullGreetingGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchPartialUpdateBuilder batchPartialUpdate() {
        return new NullGreetingBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindByFinderCallbackNullListBuilder findByFinderCallbackNullList() {
        return new NullGreetingFindByFinderCallbackNullListBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindByFinderPromiseNullListBuilder findByFinderPromiseNullList() {
        return new NullGreetingFindByFinderPromiseNullListBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindByFinderTaskNullListBuilder findByFinderTaskNullList() {
        return new NullGreetingFindByFinderTaskNullListBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindBySearchReturnNullCollectionListBuilder findBySearchReturnNullCollectionList() {
        return new NullGreetingFindBySearchReturnNullCollectionListBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindBySearchReturnNullListBuilder findBySearchReturnNullList() {
        return new NullGreetingFindBySearchReturnNullListBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnActionResultWithNullStatusBuilder actionReturnActionResultWithNullStatus() {
        return new NullGreetingDoReturnActionResultWithNullStatusBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnActionResultWithNullValueBuilder actionReturnActionResultWithNullValue() {
        return new NullGreetingDoReturnActionResultWithNullValueBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnNullActionResultBuilder actionReturnNullActionResult() {
        return new NullGreetingDoReturnNullActionResultBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnNullStringArrayBuilder actionReturnNullStringArray() {
        return new NullGreetingDoReturnNullStringArrayBuilder(getBaseUriTemplate(), StringArray.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnStringArrayWithNullElementBuilder actionReturnStringArrayWithNullElement() {
        return new NullGreetingDoReturnStringArrayWithNullElementBuilder(getBaseUriTemplate(), StringArray.class, _resourceSpec, getRequestOptions());
    }

}

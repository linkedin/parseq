
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
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * Tests to observe restli's resilience for resource methods returning null. We are simply reusing
 *  the Greetings model here for our own null-generating purposes.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.NullGreetingsResourceImpl
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.nullGreeting.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class NullGreetingRequestBuilders
    extends BuilderBase
{

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

    public NullGreetingRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public NullGreetingRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public NullGreetingRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public NullGreetingRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public NullGreetingBatchGetRequestBuilder batchGet() {
        return new NullGreetingBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchUpdateRequestBuilder batchUpdate() {
        return new NullGreetingBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchDeleteRequestBuilder batchDelete() {
        return new NullGreetingBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingGetAllRequestBuilder getAll() {
        return new NullGreetingGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingDeleteRequestBuilder delete() {
        return new NullGreetingDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchCreateRequestBuilder batchCreate() {
        return new NullGreetingBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingCreateRequestBuilder create() {
        return new NullGreetingCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingUpdateRequestBuilder update() {
        return new NullGreetingUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingGetRequestBuilder get() {
        return new NullGreetingGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new NullGreetingBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindByFinderCallbackNullListRequestBuilder findByFinderCallbackNullList() {
        return new NullGreetingFindByFinderCallbackNullListRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindByFinderPromiseNullListRequestBuilder findByFinderPromiseNullList() {
        return new NullGreetingFindByFinderPromiseNullListRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindByFinderTaskNullListRequestBuilder findByFinderTaskNullList() {
        return new NullGreetingFindByFinderTaskNullListRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindBySearchReturnNullCollectionListRequestBuilder findBySearchReturnNullCollectionList() {
        return new NullGreetingFindBySearchReturnNullCollectionListRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingFindBySearchReturnNullListRequestBuilder findBySearchReturnNullList() {
        return new NullGreetingFindBySearchReturnNullListRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnActionResultWithNullStatusRequestBuilder actionReturnActionResultWithNullStatus() {
        return new NullGreetingDoReturnActionResultWithNullStatusRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnActionResultWithNullValueRequestBuilder actionReturnActionResultWithNullValue() {
        return new NullGreetingDoReturnActionResultWithNullValueRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnNullActionResultRequestBuilder actionReturnNullActionResult() {
        return new NullGreetingDoReturnNullActionResultRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnNullStringArrayRequestBuilder actionReturnNullStringArray() {
        return new NullGreetingDoReturnNullStringArrayRequestBuilder(getBaseUriTemplate(), StringArray.class, _resourceSpec, getRequestOptions());
    }

    public NullGreetingDoReturnStringArrayWithNullElementRequestBuilder actionReturnStringArrayWithNullElement() {
        return new NullGreetingDoReturnStringArrayWithNullElementRequestBuilder(getBaseUriTemplate(), StringArray.class, _resourceSpec, getRequestOptions());
    }

}

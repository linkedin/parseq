
package com.linkedin.restli.examples.greetings.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.BooleanArray;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.data.template.StringMap;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.GreetingsResourceCallback
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greetingsCallback.restspec.json.", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsCallbackRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "greetingsCallback";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> anotherActionParams = new ArrayList<FieldDef<?>>();
        anotherActionParams.add(new FieldDef<BooleanArray>("bitfield", BooleanArray.class, DataTemplateUtil.getSchema(BooleanArray.class)));
        anotherActionParams.add(new FieldDef<TransferOwnershipRequest>("request", TransferOwnershipRequest.class, DataTemplateUtil.getSchema(TransferOwnershipRequest.class)));
        anotherActionParams.add(new FieldDef<java.lang.String>("someString", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        anotherActionParams.add(new FieldDef<StringMap>("stringMap", StringMap.class, DataTemplateUtil.getSchema(StringMap.class)));
        requestMetadataMap.put("anotherAction", new DynamicRecordMetadata("anotherAction", anotherActionParams));
        ArrayList<FieldDef<?>> exceptionTestParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("exceptionTest", new DynamicRecordMetadata("exceptionTest", exceptionTestParams));
        ArrayList<FieldDef<?>> purgeParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("purge", new DynamicRecordMetadata("purge", purgeParams));
        ArrayList<FieldDef<?>> someActionParams = new ArrayList<FieldDef<?>>();
        someActionParams.add(new FieldDef<Integer>("a", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        someActionParams.add(new FieldDef<java.lang.String>("b", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        someActionParams.add(new FieldDef<TransferOwnershipRequest>("c", TransferOwnershipRequest.class, DataTemplateUtil.getSchema(TransferOwnershipRequest.class)));
        someActionParams.add(new FieldDef<TransferOwnershipRequest>("d", TransferOwnershipRequest.class, DataTemplateUtil.getSchema(TransferOwnershipRequest.class)));
        someActionParams.add(new FieldDef<Integer>("e", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        requestMetadataMap.put("someAction", new DynamicRecordMetadata("someAction", someActionParams));
        ArrayList<FieldDef<?>> updateToneParams = new ArrayList<FieldDef<?>>();
        updateToneParams.add(new FieldDef<Tone>("newTone", Tone.class, DataTemplateUtil.getSchema(Tone.class)));
        updateToneParams.add(new FieldDef<Boolean>("delOld", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)));
        requestMetadataMap.put("updateTone", new DynamicRecordMetadata("updateTone", updateToneParams));
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        responseMetadataMap.put("anotherAction", new DynamicRecordMetadata("anotherAction", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("exceptionTest", new DynamicRecordMetadata("exceptionTest", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("purge", new DynamicRecordMetadata("purge", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("someAction", new DynamicRecordMetadata("someAction", Collections.singletonList(new FieldDef<Greeting>("value", Greeting.class, DataTemplateUtil.getSchema(Greeting.class)))));
        responseMetadataMap.put("updateTone", new DynamicRecordMetadata("updateTone", Collections.singletonList(new FieldDef<Greeting>("value", Greeting.class, DataTemplateUtil.getSchema(Greeting.class)))));
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public GreetingsCallbackRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsCallbackRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GreetingsCallbackRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsCallbackRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GreetingsCallbackBatchGetRequestBuilder batchGet() {
        return new GreetingsCallbackBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackBatchUpdateRequestBuilder batchUpdate() {
        return new GreetingsCallbackBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackBatchDeleteRequestBuilder batchDelete() {
        return new GreetingsCallbackBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackGetAllRequestBuilder getAll() {
        return new GreetingsCallbackGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackDeleteRequestBuilder delete() {
        return new GreetingsCallbackDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackBatchCreateRequestBuilder batchCreate() {
        return new GreetingsCallbackBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackCreateRequestBuilder create() {
        return new GreetingsCallbackCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackUpdateRequestBuilder update() {
        return new GreetingsCallbackUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackGetRequestBuilder get() {
        return new GreetingsCallbackGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new GreetingsCallbackBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackPartialUpdateRequestBuilder partialUpdate() {
        return new GreetingsCallbackPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackFindByEmptyRequestBuilder findByEmpty() {
        return new GreetingsCallbackFindByEmptyRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackFindBySearchRequestBuilder findBySearch() {
        return new GreetingsCallbackFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackFindBySearchWithDefaultRequestBuilder findBySearchWithDefault() {
        return new GreetingsCallbackFindBySearchWithDefaultRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackFindBySearchWithFacetsRequestBuilder findBySearchWithFacets() {
        return new GreetingsCallbackFindBySearchWithFacetsRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackFindBySearchWithPostFilterRequestBuilder findBySearchWithPostFilter() {
        return new GreetingsCallbackFindBySearchWithPostFilterRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackFindBySearchWithTonesRequestBuilder findBySearchWithTones() {
        return new GreetingsCallbackFindBySearchWithTonesRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackDoAnotherActionRequestBuilder actionAnotherAction() {
        return new GreetingsCallbackDoAnotherActionRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackDoExceptionTestRequestBuilder actionExceptionTest() {
        return new GreetingsCallbackDoExceptionTestRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackDoPurgeRequestBuilder actionPurge() {
        return new GreetingsCallbackDoPurgeRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackDoSomeActionRequestBuilder actionSomeAction() {
        return new GreetingsCallbackDoSomeActionRequestBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsCallbackDoUpdateToneRequestBuilder actionUpdateTone() {
        return new GreetingsCallbackDoUpdateToneRequestBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

}

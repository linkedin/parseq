
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
 * generated from: com.linkedin.restli.examples.greetings.server.GreetingsResourcePromiseCtx
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greetingsPromiseCtx.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class GreetingsPromiseCtxRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "greetingsPromiseCtx";
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

    public GreetingsPromiseCtxRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsPromiseCtxRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GreetingsPromiseCtxRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsPromiseCtxRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GreetingsPromiseCtxBatchGetRequestBuilder batchGet() {
        return new GreetingsPromiseCtxBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchUpdateRequestBuilder batchUpdate() {
        return new GreetingsPromiseCtxBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchDeleteRequestBuilder batchDelete() {
        return new GreetingsPromiseCtxBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxGetAllRequestBuilder getAll() {
        return new GreetingsPromiseCtxGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDeleteRequestBuilder delete() {
        return new GreetingsPromiseCtxDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchCreateRequestBuilder batchCreate() {
        return new GreetingsPromiseCtxBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxCreateRequestBuilder create() {
        return new GreetingsPromiseCtxCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxUpdateRequestBuilder update() {
        return new GreetingsPromiseCtxUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxGetRequestBuilder get() {
        return new GreetingsPromiseCtxGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new GreetingsPromiseCtxBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxPartialUpdateRequestBuilder partialUpdate() {
        return new GreetingsPromiseCtxPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindByEmptyRequestBuilder findByEmpty() {
        return new GreetingsPromiseCtxFindByEmptyRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchRequestBuilder findBySearch() {
        return new GreetingsPromiseCtxFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithDefaultRequestBuilder findBySearchWithDefault() {
        return new GreetingsPromiseCtxFindBySearchWithDefaultRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithFacetsRequestBuilder findBySearchWithFacets() {
        return new GreetingsPromiseCtxFindBySearchWithFacetsRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithPostFilterRequestBuilder findBySearchWithPostFilter() {
        return new GreetingsPromiseCtxFindBySearchWithPostFilterRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithTonesRequestBuilder findBySearchWithTones() {
        return new GreetingsPromiseCtxFindBySearchWithTonesRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoAnotherActionRequestBuilder actionAnotherAction() {
        return new GreetingsPromiseCtxDoAnotherActionRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoExceptionTestRequestBuilder actionExceptionTest() {
        return new GreetingsPromiseCtxDoExceptionTestRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoPurgeRequestBuilder actionPurge() {
        return new GreetingsPromiseCtxDoPurgeRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoSomeActionRequestBuilder actionSomeAction() {
        return new GreetingsPromiseCtxDoSomeActionRequestBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoUpdateToneRequestBuilder actionUpdateTone() {
        return new GreetingsPromiseCtxDoUpdateToneRequestBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

}

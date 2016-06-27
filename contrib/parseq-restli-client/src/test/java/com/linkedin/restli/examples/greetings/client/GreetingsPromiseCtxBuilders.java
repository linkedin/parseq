
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
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.GreetingsResourcePromiseCtx
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greetingsPromiseCtx.restspec.json.", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsPromiseCtxBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public GreetingsPromiseCtxBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsPromiseCtxBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public GreetingsPromiseCtxBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsPromiseCtxBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public GreetingsPromiseCtxBatchGetBuilder batchGet() {
        return new GreetingsPromiseCtxBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchUpdateBuilder batchUpdate() {
        return new GreetingsPromiseCtxBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchDeleteBuilder batchDelete() {
        return new GreetingsPromiseCtxBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxGetAllBuilder getAll() {
        return new GreetingsPromiseCtxGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDeleteBuilder delete() {
        return new GreetingsPromiseCtxDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchCreateBuilder batchCreate() {
        return new GreetingsPromiseCtxBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxCreateBuilder create() {
        return new GreetingsPromiseCtxCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxUpdateBuilder update() {
        return new GreetingsPromiseCtxUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxGetBuilder get() {
        return new GreetingsPromiseCtxGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxBatchPartialUpdateBuilder batchPartialUpdate() {
        return new GreetingsPromiseCtxBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxPartialUpdateBuilder partialUpdate() {
        return new GreetingsPromiseCtxPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindByEmptyBuilder findByEmpty() {
        return new GreetingsPromiseCtxFindByEmptyBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchBuilder findBySearch() {
        return new GreetingsPromiseCtxFindBySearchBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithDefaultBuilder findBySearchWithDefault() {
        return new GreetingsPromiseCtxFindBySearchWithDefaultBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithFacetsBuilder findBySearchWithFacets() {
        return new GreetingsPromiseCtxFindBySearchWithFacetsBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithPostFilterBuilder findBySearchWithPostFilter() {
        return new GreetingsPromiseCtxFindBySearchWithPostFilterBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxFindBySearchWithTonesBuilder findBySearchWithTones() {
        return new GreetingsPromiseCtxFindBySearchWithTonesBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoAnotherActionBuilder actionAnotherAction() {
        return new GreetingsPromiseCtxDoAnotherActionBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoExceptionTestBuilder actionExceptionTest() {
        return new GreetingsPromiseCtxDoExceptionTestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoPurgeBuilder actionPurge() {
        return new GreetingsPromiseCtxDoPurgeBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoSomeActionBuilder actionSomeAction() {
        return new GreetingsPromiseCtxDoSomeActionBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsPromiseCtxDoUpdateToneBuilder actionUpdateTone() {
        return new GreetingsPromiseCtxDoUpdateToneBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

}

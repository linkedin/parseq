
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
 * generated from: com.linkedin.restli.examples.greetings.server.GreetingsResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greetings.restspec.json.", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "greetings";
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

    public GreetingsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GreetingsRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingsRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GreetingsBatchGetRequestBuilder batchGet() {
        return new GreetingsBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsBatchUpdateRequestBuilder batchUpdate() {
        return new GreetingsBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsBatchDeleteRequestBuilder batchDelete() {
        return new GreetingsBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsGetAllRequestBuilder getAll() {
        return new GreetingsGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsDeleteRequestBuilder delete() {
        return new GreetingsDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsBatchCreateRequestBuilder batchCreate() {
        return new GreetingsBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsCreateRequestBuilder create() {
        return new GreetingsCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsUpdateRequestBuilder update() {
        return new GreetingsUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsGetRequestBuilder get() {
        return new GreetingsGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new GreetingsBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsPartialUpdateRequestBuilder partialUpdate() {
        return new GreetingsPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsFindByEmptyRequestBuilder findByEmpty() {
        return new GreetingsFindByEmptyRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsFindBySearchRequestBuilder findBySearch() {
        return new GreetingsFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsFindBySearchWithDefaultRequestBuilder findBySearchWithDefault() {
        return new GreetingsFindBySearchWithDefaultRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsFindBySearchWithFacetsRequestBuilder findBySearchWithFacets() {
        return new GreetingsFindBySearchWithFacetsRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsFindBySearchWithPostFilterRequestBuilder findBySearchWithPostFilter() {
        return new GreetingsFindBySearchWithPostFilterRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsFindBySearchWithTonesRequestBuilder findBySearchWithTones() {
        return new GreetingsFindBySearchWithTonesRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GreetingsDoAnotherActionRequestBuilder actionAnotherAction() {
        return new GreetingsDoAnotherActionRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsDoExceptionTestRequestBuilder actionExceptionTest() {
        return new GreetingsDoExceptionTestRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsDoPurgeRequestBuilder actionPurge() {
        return new GreetingsDoPurgeRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsDoSomeActionRequestBuilder actionSomeAction() {
        return new GreetingsDoSomeActionRequestBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

    public GreetingsDoUpdateToneRequestBuilder actionUpdateTone() {
        return new GreetingsDoUpdateToneRequestBuilder(getBaseUriTemplate(), Greeting.class, _resourceSpec, getRequestOptions());
    }

}

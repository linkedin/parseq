
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
import com.linkedin.restli.examples.greetings.api.ComplexArray;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.ComplexArrayResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.complexArray.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexArrayRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "complexArray";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> actionParams = new ArrayList<FieldDef<?>>();
        actionParams.add(new FieldDef<ComplexArray>("array", ComplexArray.class, DataTemplateUtil.getSchema(ComplexArray.class)));
        requestMetadataMap.put("action", new DynamicRecordMetadata("action", actionParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("action", new DynamicRecordMetadata("action", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, ComplexArray.class, ComplexArray.class, Greeting.class, keyParts);
    }

    public ComplexArrayRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexArrayRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ComplexArrayRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexArrayRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ComplexArrayBatchGetRequestBuilder batchGet() {
        return new ComplexArrayBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexArrayGetRequestBuilder get() {
        return new ComplexArrayGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexArrayFindByFinderRequestBuilder findByFinder() {
        return new ComplexArrayFindByFinderRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ComplexArrayDoActionRequestBuilder actionAction() {
        return new ComplexArrayDoActionRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

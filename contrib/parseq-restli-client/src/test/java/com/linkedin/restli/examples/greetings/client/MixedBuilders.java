
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
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * This resource demonstrates mixing of various method signatures: synchronous, callback,
 *  promise
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.MixedResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.mixed.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class MixedBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "mixed";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> theActionParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("theAction", new DynamicRecordMetadata("theAction", theActionParams));
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        responseMetadataMap.put("theAction", new DynamicRecordMetadata("theAction", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.CREATE, ResourceMethod.UPDATE, ResourceMethod.DELETE), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public MixedBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public MixedBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public MixedBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public MixedBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public MixedDeleteBuilder delete() {
        return new MixedDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public MixedCreateBuilder create() {
        return new MixedCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public MixedUpdateBuilder update() {
        return new MixedUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public MixedGetBuilder get() {
        return new MixedGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public MixedFindBySearchBuilder findBySearch() {
        return new MixedFindBySearchBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public MixedDoTheActionBuilder actionTheAction() {
        return new MixedDoTheActionBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

}

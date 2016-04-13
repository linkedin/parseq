
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
 * generated from: com.linkedin.restli.examples.greetings.server.StreamingGreetings
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.streamingGreetings.restspec.json.", date = "Wed Apr 06 14:21:38 PDT 2016")
public class StreamingGreetingsBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "streamingGreetings";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> actionAttachmentsAllowedButDislikedParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("actionAttachmentsAllowedButDisliked", new DynamicRecordMetadata("actionAttachmentsAllowedButDisliked", actionAttachmentsAllowedButDislikedParams));
        ArrayList<FieldDef<?>> actionNoAttachmentsAllowedParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("actionNoAttachmentsAllowed", new DynamicRecordMetadata("actionNoAttachmentsAllowed", actionNoAttachmentsAllowedParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("actionAttachmentsAllowedButDisliked", new DynamicRecordMetadata("actionAttachmentsAllowedButDisliked", Collections.singletonList(new FieldDef<Boolean>("value", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)))));
        responseMetadataMap.put("actionNoAttachmentsAllowed", new DynamicRecordMetadata("actionNoAttachmentsAllowed", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.CREATE, ResourceMethod.UPDATE, ResourceMethod.DELETE), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public StreamingGreetingsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public StreamingGreetingsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public StreamingGreetingsBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public StreamingGreetingsBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public StreamingGreetingsUpdateBuilder update() {
        return new StreamingGreetingsUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StreamingGreetingsDeleteBuilder delete() {
        return new StreamingGreetingsDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StreamingGreetingsGetBuilder get() {
        return new StreamingGreetingsGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StreamingGreetingsCreateBuilder create() {
        return new StreamingGreetingsCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public StreamingGreetingsDoActionAttachmentsAllowedButDislikedBuilder actionActionAttachmentsAllowedButDisliked() {
        return new StreamingGreetingsDoActionAttachmentsAllowedButDislikedBuilder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public StreamingGreetingsDoActionNoAttachmentsAllowedBuilder actionActionNoAttachmentsAllowed() {
        return new StreamingGreetingsDoActionNoAttachmentsAllowedBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

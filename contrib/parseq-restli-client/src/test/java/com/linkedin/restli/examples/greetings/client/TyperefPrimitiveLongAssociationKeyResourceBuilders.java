
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.typeref.api.LongRef;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.TyperefPrimitiveLongAssociationKeyResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.typerefPrimitiveLongAssociationKeyResource.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class TyperefPrimitiveLongAssociationKeyResourceBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "typerefPrimitiveLongAssociationKeyResource";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, CompoundKey.TypeInfo> keyParts = new HashMap<String, CompoundKey.TypeInfo>();
        keyParts.put("src", new CompoundKey.TypeInfo(Long.class, LongRef.class));
        keyParts.put("dest", new CompoundKey.TypeInfo(Long.class, LongRef.class));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, CompoundKey.class, null, null, Message.class, keyParts);
    }

    public TyperefPrimitiveLongAssociationKeyResourceBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefPrimitiveLongAssociationKeyResourceBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public TyperefPrimitiveLongAssociationKeyResourceBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefPrimitiveLongAssociationKeyResourceBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public TyperefPrimitiveLongAssociationKeyResourceGetBuilder get() {
        return new TyperefPrimitiveLongAssociationKeyResourceGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public TyperefPrimitiveLongAssociationKeyResourceBuilders.Key setDest(Long dest) {
            append("dest", dest);
            return this;
        }

        public Long getDest() {
            return ((Long) getPart("dest"));
        }

        public TyperefPrimitiveLongAssociationKeyResourceBuilders.Key setSrc(Long src) {
            append("src", src);
            return this;
        }

        public Long getSrc() {
            return ((Long) getPart("src"));
        }

    }

}

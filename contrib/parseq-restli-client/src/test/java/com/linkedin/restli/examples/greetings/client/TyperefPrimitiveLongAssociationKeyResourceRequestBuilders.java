
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.typeref.api.LongRef;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.TyperefPrimitiveLongAssociationKeyResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.typerefPrimitiveLongAssociationKeyResource.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class TyperefPrimitiveLongAssociationKeyResourceRequestBuilders
    extends BuilderBase
{

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

    public TyperefPrimitiveLongAssociationKeyResourceRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefPrimitiveLongAssociationKeyResourceRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public TyperefPrimitiveLongAssociationKeyResourceRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefPrimitiveLongAssociationKeyResourceRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public TyperefPrimitiveLongAssociationKeyResourceGetRequestBuilder get() {
        return new TyperefPrimitiveLongAssociationKeyResourceGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public TyperefPrimitiveLongAssociationKeyResourceRequestBuilders.Key setDest(Long dest) {
            append("dest", dest);
            return this;
        }

        public Long getDest() {
            return ((Long) getPart("dest"));
        }

        public TyperefPrimitiveLongAssociationKeyResourceRequestBuilders.Key setSrc(Long src) {
            append("src", src);
            return this;
        }

        public Long getSrc() {
            return ((Long) getPart("src"));
        }

    }

}


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
import com.linkedin.restli.examples.typeref.api.CustomDoubleRef;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.TyperefCustomDoubleAssociationKeyResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.typerefCustomDoubleAssociationKeyResource.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class TyperefCustomDoubleAssociationKeyResourceRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "typerefCustomDoubleAssociationKeyResource";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, CompoundKey.TypeInfo> keyParts = new HashMap<String, CompoundKey.TypeInfo>();
        keyParts.put("src", new CompoundKey.TypeInfo(com.linkedin.restli.examples.custom.types.CustomDouble.class, CustomDoubleRef.class));
        keyParts.put("dest", new CompoundKey.TypeInfo(com.linkedin.restli.examples.custom.types.CustomDouble.class, CustomDoubleRef.class));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, CompoundKey.class, null, null, Message.class, keyParts);
    }

    public TyperefCustomDoubleAssociationKeyResourceRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefCustomDoubleAssociationKeyResourceRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public TyperefCustomDoubleAssociationKeyResourceRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefCustomDoubleAssociationKeyResourceRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public TyperefCustomDoubleAssociationKeyResourceGetRequestBuilder get() {
        return new TyperefCustomDoubleAssociationKeyResourceGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public TyperefCustomDoubleAssociationKeyResourceRequestBuilders.Key setDest(com.linkedin.restli.examples.custom.types.CustomDouble dest) {
            append("dest", dest);
            return this;
        }

        public com.linkedin.restli.examples.custom.types.CustomDouble getDest() {
            return ((com.linkedin.restli.examples.custom.types.CustomDouble) getPart("dest"));
        }

        public TyperefCustomDoubleAssociationKeyResourceRequestBuilders.Key setSrc(com.linkedin.restli.examples.custom.types.CustomDouble src) {
            append("src", src);
            return this;
        }

        public com.linkedin.restli.examples.custom.types.CustomDouble getSrc() {
            return ((com.linkedin.restli.examples.custom.types.CustomDouble) getPart("src"));
        }

    }

}


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
import com.linkedin.restli.examples.typeref.api.CustomDoubleRef;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.TyperefCustomDoubleAssociationKeyResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.typerefCustomDoubleAssociationKeyResource.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class TyperefCustomDoubleAssociationKeyResourceBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public TyperefCustomDoubleAssociationKeyResourceBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefCustomDoubleAssociationKeyResourceBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public TyperefCustomDoubleAssociationKeyResourceBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefCustomDoubleAssociationKeyResourceBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public TyperefCustomDoubleAssociationKeyResourceGetBuilder get() {
        return new TyperefCustomDoubleAssociationKeyResourceGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public TyperefCustomDoubleAssociationKeyResourceBuilders.Key setDest(com.linkedin.restli.examples.custom.types.CustomDouble dest) {
            append("dest", dest);
            return this;
        }

        public com.linkedin.restli.examples.custom.types.CustomDouble getDest() {
            return ((com.linkedin.restli.examples.custom.types.CustomDouble) getPart("dest"));
        }

        public TyperefCustomDoubleAssociationKeyResourceBuilders.Key setSrc(com.linkedin.restli.examples.custom.types.CustomDouble src) {
            append("src", src);
            return this;
        }

        public com.linkedin.restli.examples.custom.types.CustomDouble getSrc() {
            return ((com.linkedin.restli.examples.custom.types.CustomDouble) getPart("src"));
        }

    }

}

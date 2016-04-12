
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


/**
 * Demonstrates an assocation resource keyed by string.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.AssociationsResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.associations.restspec.json.", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AssociationsRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "associations";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, CompoundKey.TypeInfo>();
        keyParts.put("src", new CompoundKey.TypeInfo(java.lang.String.class, java.lang.String.class));
        keyParts.put("dest", new CompoundKey.TypeInfo(java.lang.String.class, java.lang.String.class));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE), requestMetadataMap, responseMetadataMap, CompoundKey.class, null, null, Message.class, keyParts);
    }

    public AssociationsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AssociationsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public AssociationsRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AssociationsRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public AssociationsBatchGetRequestBuilder batchGet() {
        return new AssociationsBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsBatchUpdateRequestBuilder batchUpdate() {
        return new AssociationsBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsCreateRequestBuilder create() {
        return new AssociationsCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsGetRequestBuilder get() {
        return new AssociationsGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new AssociationsBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsFindByAssocKeyFinderRequestBuilder findByAssocKeyFinder() {
        return new AssociationsFindByAssocKeyFinderRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsFindByAssocKeyFinderOptRequestBuilder findByAssocKeyFinderOpt() {
        return new AssociationsFindByAssocKeyFinderOptRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public AssociationsRequestBuilders.Key setDest(java.lang.String dest) {
            append("dest", dest);
            return this;
        }

        public java.lang.String getDest() {
            return ((java.lang.String) getPart("dest"));
        }

        public AssociationsRequestBuilders.Key setSrc(java.lang.String src) {
            append("src", src);
            return this;
        }

        public java.lang.String getSrc() {
            return ((java.lang.String) getPart("src"));
        }

    }

}

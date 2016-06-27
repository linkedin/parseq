
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
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Demonstrates an assocation resource keyed by string.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.AssociationsResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.associations.restspec.json.", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AssociationsBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public AssociationsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AssociationsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public AssociationsBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AssociationsBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public AssociationsBatchGetBuilder batchGet() {
        return new AssociationsBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsBatchUpdateBuilder batchUpdate() {
        return new AssociationsBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsCreateBuilder create() {
        return new AssociationsCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsGetBuilder get() {
        return new AssociationsGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsBatchPartialUpdateBuilder batchPartialUpdate() {
        return new AssociationsBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsFindByAssocKeyFinderBuilder findByAssocKeyFinder() {
        return new AssociationsFindByAssocKeyFinderBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AssociationsFindByAssocKeyFinderOptBuilder findByAssocKeyFinderOpt() {
        return new AssociationsFindByAssocKeyFinderOptBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public AssociationsBuilders.Key setDest(java.lang.String dest) {
            append("dest", dest);
            return this;
        }

        public java.lang.String getDest() {
            return ((java.lang.String) getPart("dest"));
        }

        public AssociationsBuilders.Key setSrc(java.lang.String src) {
            append("src", src);
            return this;
        }

        public java.lang.String getSrc() {
            return ((java.lang.String) getPart("src"));
        }

    }

}

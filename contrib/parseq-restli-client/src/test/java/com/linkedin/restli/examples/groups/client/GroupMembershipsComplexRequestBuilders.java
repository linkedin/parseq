
package com.linkedin.restli.examples.groups.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.groups.api.ComplexKeyGroupMembership;
import com.linkedin.restli.examples.groups.api.GroupMembershipKey;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;


/**
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupMembershipsResource3
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groupMembershipsComplex.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsComplexRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "groupMembershipsComplex";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, GroupMembershipKey.class, GroupMembershipParam.class, ComplexKeyGroupMembership.class, keyParts);
    }

    public GroupMembershipsComplexRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsComplexRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GroupMembershipsComplexRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsComplexRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GroupMembershipsComplexBatchGetRequestBuilder batchGet() {
        return new GroupMembershipsComplexBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchUpdateRequestBuilder batchUpdate() {
        return new GroupMembershipsComplexBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchDeleteRequestBuilder batchDelete() {
        return new GroupMembershipsComplexBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexDeleteRequestBuilder delete() {
        return new GroupMembershipsComplexDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchCreateRequestBuilder batchCreate() {
        return new GroupMembershipsComplexBatchCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexCreateRequestBuilder create() {
        return new GroupMembershipsComplexCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexUpdateRequestBuilder update() {
        return new GroupMembershipsComplexUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexGetRequestBuilder get() {
        return new GroupMembershipsComplexGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new GroupMembershipsComplexBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexPartialUpdateRequestBuilder partialUpdate() {
        return new GroupMembershipsComplexPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

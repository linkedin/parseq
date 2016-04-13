
package com.linkedin.restli.examples.groups.client;

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
import com.linkedin.restli.examples.groups.api.GroupMembership;


/**
 * Association between members and groups
 * 
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupMembershipsResource2
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groupMemberships.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "groupMemberships";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, CompoundKey.TypeInfo> keyParts = new HashMap<String, CompoundKey.TypeInfo>();
        keyParts.put("groupID", new CompoundKey.TypeInfo(Integer.class, Integer.class));
        keyParts.put("memberID", new CompoundKey.TypeInfo(Integer.class, Integer.class));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE, ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, CompoundKey.class, null, null, GroupMembership.class, keyParts);
    }

    public GroupMembershipsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GroupMembershipsRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GroupMembershipsBatchGetRequestBuilder batchGet() {
        return new GroupMembershipsBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsBatchUpdateRequestBuilder batchUpdate() {
        return new GroupMembershipsBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsBatchDeleteRequestBuilder batchDelete() {
        return new GroupMembershipsBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsGetAllRequestBuilder getAll() {
        return new GroupMembershipsGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsDeleteRequestBuilder delete() {
        return new GroupMembershipsDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsUpdateRequestBuilder update() {
        return new GroupMembershipsUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsGetRequestBuilder get() {
        return new GroupMembershipsGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new GroupMembershipsBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsPartialUpdateRequestBuilder partialUpdate() {
        return new GroupMembershipsPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsFindByGroupRequestBuilder findByGroup() {
        return new GroupMembershipsFindByGroupRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsFindByMemberRequestBuilder findByMember() {
        return new GroupMembershipsFindByMemberRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public GroupMembershipsRequestBuilders.Key setGroupId(Integer groupID) {
            append("groupID", groupID);
            return this;
        }

        public Integer getGroupId() {
            return ((Integer) getPart("groupID"));
        }

        public GroupMembershipsRequestBuilders.Key setMemberId(Integer memberID) {
            append("memberID", memberID);
            return this;
        }

        public Integer getMemberId() {
            return ((Integer) getPart("memberID"));
        }

    }

}


package com.linkedin.restli.examples.groups.client;

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
import com.linkedin.restli.examples.groups.api.GroupMembership;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Association between members and groups
 * 
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupMembershipsResource2
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groupMemberships.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public GroupMembershipsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public GroupMembershipsBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public GroupMembershipsBatchGetBuilder batchGet() {
        return new GroupMembershipsBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsBatchUpdateBuilder batchUpdate() {
        return new GroupMembershipsBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsBatchDeleteBuilder batchDelete() {
        return new GroupMembershipsBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsGetAllBuilder getAll() {
        return new GroupMembershipsGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsDeleteBuilder delete() {
        return new GroupMembershipsDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsUpdateBuilder update() {
        return new GroupMembershipsUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsGetBuilder get() {
        return new GroupMembershipsGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsBatchPartialUpdateBuilder batchPartialUpdate() {
        return new GroupMembershipsBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsPartialUpdateBuilder partialUpdate() {
        return new GroupMembershipsPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsFindByGroupBuilder findByGroup() {
        return new GroupMembershipsFindByGroupBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsFindByMemberBuilder findByMember() {
        return new GroupMembershipsFindByMemberBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public GroupMembershipsBuilders.Key setGroupId(Integer groupID) {
            append("groupID", groupID);
            return this;
        }

        public Integer getGroupId() {
            return ((Integer) getPart("groupID"));
        }

        public GroupMembershipsBuilders.Key setMemberId(Integer memberID) {
            append("memberID", memberID);
            return this;
        }

        public Integer getMemberId() {
            return ((Integer) getPart("memberID"));
        }

    }

}

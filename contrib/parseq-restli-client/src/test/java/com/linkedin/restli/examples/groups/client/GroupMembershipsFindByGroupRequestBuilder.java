
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupMembership;
import com.linkedin.restli.examples.groups.api.MembershipSortOrder;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsFindByGroupRequestBuilder
    extends FindRequestBuilderBase<CompoundKey, GroupMembership, GroupMembershipsFindByGroupRequestBuilder>
{


    public GroupMembershipsFindByGroupRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupMembership.class, resourceSpec, requestOptions);
        super.name("group");
    }

    public GroupMembershipsFindByGroupRequestBuilder groupIdKey(Integer key) {
        super.assocKey("groupID", key);
        return this;
    }

    public GroupMembershipsFindByGroupRequestBuilder levelParam(java.lang.String value) {
        super.setParam("level", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupRequestBuilder firstNameParam(java.lang.String value) {
        super.setParam("firstName", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupRequestBuilder lastNameParam(java.lang.String value) {
        super.setParam("lastName", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupRequestBuilder emailParam(java.lang.String value) {
        super.setParam("email", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupRequestBuilder sortParam(MembershipSortOrder value) {
        super.setParam("sort", value, MembershipSortOrder.class);
        return this;
    }

}

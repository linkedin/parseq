
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupMembership;
import com.linkedin.restli.examples.groups.api.MembershipSortOrder;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsFindByGroupBuilder
    extends FindRequestBuilderBase<CompoundKey, GroupMembership, GroupMembershipsFindByGroupBuilder>
{


    public GroupMembershipsFindByGroupBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupMembership.class, resourceSpec, requestOptions);
        super.name("group");
    }

    public GroupMembershipsFindByGroupBuilder groupIdKey(Integer key) {
        super.assocKey("groupID", key);
        return this;
    }

    public GroupMembershipsFindByGroupBuilder levelParam(java.lang.String value) {
        super.setParam("level", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupBuilder firstNameParam(java.lang.String value) {
        super.setParam("firstName", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupBuilder lastNameParam(java.lang.String value) {
        super.setParam("lastName", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupBuilder emailParam(java.lang.String value) {
        super.setParam("email", value, java.lang.String.class);
        return this;
    }

    public GroupMembershipsFindByGroupBuilder sortParam(MembershipSortOrder value) {
        super.setParam("sort", value, MembershipSortOrder.class);
        return this;
    }

}

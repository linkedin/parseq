
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.ComplexKeyGroupMembership;
import com.linkedin.restli.examples.groups.api.GroupMembershipKey;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;
import com.linkedin.restli.examples.groups.api.GroupMembershipQueryParam;
import com.linkedin.restli.examples.groups.api.GroupMembershipQueryParamArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsComplexGetBuilder
    extends GetRequestBuilderBase<ComplexResourceKey<GroupMembershipKey, GroupMembershipParam> , ComplexKeyGroupMembership, GroupMembershipsComplexGetBuilder>
{


    public GroupMembershipsComplexGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ComplexKeyGroupMembership.class, resourceSpec, requestOptions);
    }

    public GroupMembershipsComplexGetBuilder testParamParam(GroupMembershipParam value) {
        super.setParam("testParam", value, GroupMembershipParam.class);
        return this;
    }

    public GroupMembershipsComplexGetBuilder testParamArrayParam(GroupMembershipQueryParamArray value) {
        super.setParam("testParamArray", value, GroupMembershipQueryParamArray.class);
        return this;
    }

    public GroupMembershipsComplexGetBuilder testParamArrayParam(Iterable<GroupMembershipQueryParam> value) {
        super.setParam("testParamArray", value, GroupMembershipQueryParam.class);
        return this;
    }

    public GroupMembershipsComplexGetBuilder addTestParamArrayParam(GroupMembershipQueryParam value) {
        super.addParam("testParamArray", value, GroupMembershipQueryParam.class);
        return this;
    }

}

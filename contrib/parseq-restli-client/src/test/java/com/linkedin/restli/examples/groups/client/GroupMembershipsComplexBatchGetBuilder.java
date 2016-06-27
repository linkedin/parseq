
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BatchGetRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.ComplexKeyGroupMembership;
import com.linkedin.restli.examples.groups.api.GroupMembershipKey;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsComplexBatchGetBuilder
    extends BatchGetRequestBuilderBase<ComplexResourceKey<GroupMembershipKey, GroupMembershipParam> , ComplexKeyGroupMembership, GroupMembershipsComplexBatchGetBuilder>
{


    public GroupMembershipsComplexBatchGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ComplexKeyGroupMembership.class, resourceSpec, requestOptions);
    }

}

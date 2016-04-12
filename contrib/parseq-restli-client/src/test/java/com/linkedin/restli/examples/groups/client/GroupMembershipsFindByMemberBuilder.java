
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupMembership;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsFindByMemberBuilder
    extends FindRequestBuilderBase<CompoundKey, GroupMembership, GroupMembershipsFindByMemberBuilder>
{


    public GroupMembershipsFindByMemberBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupMembership.class, resourceSpec, requestOptions);
        super.name("member");
    }

    public GroupMembershipsFindByMemberBuilder memberIdKey(Integer key) {
        super.assocKey("memberID", key);
        return this;
    }

}

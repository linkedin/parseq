
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.DeleteRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupMembership;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsDeleteBuilder
    extends DeleteRequestBuilderBase<CompoundKey, GroupMembership, GroupMembershipsDeleteBuilder>
{


    public GroupMembershipsDeleteBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupMembership.class, resourceSpec, requestOptions);
    }

}

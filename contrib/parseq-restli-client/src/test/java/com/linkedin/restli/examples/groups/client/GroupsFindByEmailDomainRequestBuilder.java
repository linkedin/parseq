
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.Group;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsFindByEmailDomainRequestBuilder
    extends FindRequestBuilderBase<Integer, Group, GroupsFindByEmailDomainRequestBuilder>
{


    public GroupsFindByEmailDomainRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Group.class, resourceSpec, requestOptions);
        super.name("emailDomain");
    }

    public GroupsFindByEmailDomainRequestBuilder emailDomainParam(java.lang.String value) {
        super.setReqParam("emailDomain", value, java.lang.String.class);
        return this;
    }

}

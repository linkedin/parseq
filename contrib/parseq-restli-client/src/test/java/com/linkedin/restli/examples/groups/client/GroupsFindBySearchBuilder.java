
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.Group;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsFindBySearchBuilder
    extends FindRequestBuilderBase<Integer, Group, GroupsFindBySearchBuilder>
{


    public GroupsFindBySearchBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Group.class, resourceSpec, requestOptions);
        super.name("search");
    }

    public GroupsFindBySearchBuilder keywordsParam(java.lang.String value) {
        super.setParam("keywords", value, java.lang.String.class);
        return this;
    }

    public GroupsFindBySearchBuilder nameKeywordsParam(java.lang.String value) {
        super.setParam("nameKeywords", value, java.lang.String.class);
        return this;
    }

    public GroupsFindBySearchBuilder groupIdParam(Integer value) {
        super.setParam("groupID", value, Integer.class);
        return this;
    }

}

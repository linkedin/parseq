
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsAuthGetRequestBuilder
    extends GetRequestBuilderBase<Long, Greeting, GreetingsAuthGetRequestBuilder>
{


    public GreetingsAuthGetRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public GreetingsAuthGetRequestBuilder authParam(java.lang.String value) {
        super.setParam("auth", value, java.lang.String.class);
        return this;
    }

    public GreetingsAuthGetRequestBuilder testComplexParam(GroupMembershipParam value) {
        super.setParam("testComplex", value, GroupMembershipParam.class);
        return this;
    }

}

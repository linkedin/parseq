
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.DeleteRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsAuthDeleteBuilder
    extends DeleteRequestBuilderBase<Long, Greeting, GreetingsAuthDeleteBuilder>
{


    public GreetingsAuthDeleteBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public GreetingsAuthDeleteBuilder authParam(java.lang.String value) {
        super.setParam("auth", value, java.lang.String.class);
        return this;
    }

}

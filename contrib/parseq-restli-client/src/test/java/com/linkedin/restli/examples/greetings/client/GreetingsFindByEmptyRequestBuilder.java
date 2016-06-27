
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Empty;
import com.linkedin.restli.examples.greetings.api.EmptyArray;
import com.linkedin.restli.examples.greetings.api.EmptyMap;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsFindByEmptyRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, GreetingsFindByEmptyRequestBuilder>
{


    public GreetingsFindByEmptyRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("empty");
    }

    public GreetingsFindByEmptyRequestBuilder arrayParam(EmptyArray value) {
        super.setReqParam("array", value, EmptyArray.class);
        return this;
    }

    public GreetingsFindByEmptyRequestBuilder arrayParam(Iterable<Empty> value) {
        super.setReqParam("array", value, Empty.class);
        return this;
    }

    public GreetingsFindByEmptyRequestBuilder addArrayParam(Empty value) {
        super.addReqParam("array", value, Empty.class);
        return this;
    }

    public GreetingsFindByEmptyRequestBuilder mapParam(EmptyMap value) {
        super.setReqParam("map", value, EmptyMap.class);
        return this;
    }

}

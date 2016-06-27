
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
public class GreetingsPromiseFindByEmptyRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, GreetingsPromiseFindByEmptyRequestBuilder>
{


    public GreetingsPromiseFindByEmptyRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("empty");
    }

    public GreetingsPromiseFindByEmptyRequestBuilder arrayParam(EmptyArray value) {
        super.setReqParam("array", value, EmptyArray.class);
        return this;
    }

    public GreetingsPromiseFindByEmptyRequestBuilder arrayParam(Iterable<Empty> value) {
        super.setReqParam("array", value, Empty.class);
        return this;
    }

    public GreetingsPromiseFindByEmptyRequestBuilder addArrayParam(Empty value) {
        super.addReqParam("array", value, Empty.class);
        return this;
    }

    public GreetingsPromiseFindByEmptyRequestBuilder mapParam(EmptyMap value) {
        super.setReqParam("map", value, EmptyMap.class);
        return this;
    }

}

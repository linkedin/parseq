
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class SubgreetingsFindBySearchBuilder
    extends FindRequestBuilderBase<Long, Greeting, SubgreetingsFindBySearchBuilder>
{


    public SubgreetingsFindBySearchBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("search");
    }

    public SubgreetingsFindBySearchBuilder toneParam(Tone value) {
        super.setParam("tone", value, Tone.class);
        return this;
    }

    public SubgreetingsFindBySearchBuilder complexQueryParamParam(Greeting value) {
        super.setParam("complexQueryParam", value, Greeting.class);
        return this;
    }

}

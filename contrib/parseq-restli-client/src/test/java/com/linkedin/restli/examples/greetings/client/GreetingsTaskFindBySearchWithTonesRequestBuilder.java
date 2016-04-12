
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;
import com.linkedin.restli.examples.greetings.api.ToneArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class GreetingsTaskFindBySearchWithTonesRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, GreetingsTaskFindBySearchWithTonesRequestBuilder>
{


    public GreetingsTaskFindBySearchWithTonesRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("searchWithTones");
    }

    public GreetingsTaskFindBySearchWithTonesRequestBuilder tonesParam(ToneArray value) {
        super.setParam("tones", value, ToneArray.class);
        return this;
    }

    public GreetingsTaskFindBySearchWithTonesRequestBuilder tonesParam(Iterable<Tone> value) {
        super.setParam("tones", value, Tone.class);
        return this;
    }

    public GreetingsTaskFindBySearchWithTonesRequestBuilder addTonesParam(Tone value) {
        super.addParam("tones", value, Tone.class);
        return this;
    }

}

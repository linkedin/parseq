
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;
import com.linkedin.restli.examples.greetings.api.ToneArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsCallbackFindBySearchWithTonesBuilder
    extends FindRequestBuilderBase<Long, Greeting, GreetingsCallbackFindBySearchWithTonesBuilder>
{


    public GreetingsCallbackFindBySearchWithTonesBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("searchWithTones");
    }

    public GreetingsCallbackFindBySearchWithTonesBuilder tonesParam(ToneArray value) {
        super.setParam("tones", value, ToneArray.class);
        return this;
    }

    public GreetingsCallbackFindBySearchWithTonesBuilder tonesParam(Iterable<Tone> value) {
        super.setParam("tones", value, Tone.class);
        return this;
    }

    public GreetingsCallbackFindBySearchWithTonesBuilder addTonesParam(Tone value) {
        super.addParam("tones", value, Tone.class);
        return this;
    }

}

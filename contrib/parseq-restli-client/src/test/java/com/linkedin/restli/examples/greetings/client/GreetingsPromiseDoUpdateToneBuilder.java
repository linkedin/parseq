
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsPromiseDoUpdateToneBuilder
    extends ActionRequestBuilderBase<Long, Greeting, GreetingsPromiseDoUpdateToneBuilder>
{


    public GreetingsPromiseDoUpdateToneBuilder(String baseUriTemplate, Class<Greeting> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("updateTone");
    }

    public GreetingsPromiseDoUpdateToneBuilder paramNewTone(Tone value) {
        super.setParam(_resourceSpec.getRequestMetadata("updateTone").getFieldDef("newTone"), value);
        return this;
    }

    public GreetingsPromiseDoUpdateToneBuilder paramDelOld(Boolean value) {
        super.setParam(_resourceSpec.getRequestMetadata("updateTone").getFieldDef("delOld"), value);
        return this;
    }

}

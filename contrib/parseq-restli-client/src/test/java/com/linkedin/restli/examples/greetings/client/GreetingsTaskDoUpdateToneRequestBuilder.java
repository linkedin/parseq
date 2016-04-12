
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Tone;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class GreetingsTaskDoUpdateToneRequestBuilder
    extends ActionRequestBuilderBase<Long, Greeting, GreetingsTaskDoUpdateToneRequestBuilder>
{


    public GreetingsTaskDoUpdateToneRequestBuilder(String baseUriTemplate, Class<Greeting> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("updateTone");
    }

    public GreetingsTaskDoUpdateToneRequestBuilder newToneParam(Tone value) {
        super.setParam(_resourceSpec.getRequestMetadata("updateTone").getFieldDef("newTone"), value);
        return this;
    }

    public GreetingsTaskDoUpdateToneRequestBuilder delOldParam(Boolean value) {
        super.setParam(_resourceSpec.getRequestMetadata("updateTone").getFieldDef("delOld"), value);
        return this;
    }

}

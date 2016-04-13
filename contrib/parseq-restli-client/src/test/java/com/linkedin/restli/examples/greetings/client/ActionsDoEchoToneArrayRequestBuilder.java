
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoEchoToneArrayRequestBuilder
    extends ActionRequestBuilderBase<Void, com.linkedin.restli.examples.greetings.api.ToneArray, ActionsDoEchoToneArrayRequestBuilder>
{


    public ActionsDoEchoToneArrayRequestBuilder(String baseUriTemplate, Class<com.linkedin.restli.examples.greetings.api.ToneArray> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("echoToneArray");
    }

    public ActionsDoEchoToneArrayRequestBuilder tonesParam(com.linkedin.restli.examples.greetings.api.ToneArray value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("echoToneArray").getFieldDef("tones"), value);
        return this;
    }

}

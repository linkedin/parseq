
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Wed Apr 06 14:21:38 PDT 2016")
public class StreamingGreetingsDoActionAttachmentsAllowedButDislikedBuilder
    extends ActionRequestBuilderBase<Void, Boolean, StreamingGreetingsDoActionAttachmentsAllowedButDislikedBuilder>
{


    public StreamingGreetingsDoActionAttachmentsAllowedButDislikedBuilder(String baseUriTemplate, Class<Boolean> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("actionAttachmentsAllowedButDisliked");
    }

}

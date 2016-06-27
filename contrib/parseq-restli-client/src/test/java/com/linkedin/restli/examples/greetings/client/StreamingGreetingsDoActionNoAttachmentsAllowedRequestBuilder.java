
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Wed Apr 06 14:21:38 PDT 2016")
public class StreamingGreetingsDoActionNoAttachmentsAllowedRequestBuilder
    extends ActionRequestBuilderBase<Void, Integer, StreamingGreetingsDoActionNoAttachmentsAllowedRequestBuilder>
{


    public StreamingGreetingsDoActionNoAttachmentsAllowedRequestBuilder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("actionNoAttachmentsAllowed");
    }

}

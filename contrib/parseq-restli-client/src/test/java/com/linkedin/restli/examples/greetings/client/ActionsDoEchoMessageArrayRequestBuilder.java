
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoEchoMessageArrayRequestBuilder
    extends ActionRequestBuilderBase<Void, com.linkedin.restli.examples.greetings.api.MessageArray, ActionsDoEchoMessageArrayRequestBuilder>
{


    public ActionsDoEchoMessageArrayRequestBuilder(String baseUriTemplate, Class<com.linkedin.restli.examples.greetings.api.MessageArray> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("echoMessageArray");
    }

    public ActionsDoEchoMessageArrayRequestBuilder messagesParam(com.linkedin.restli.examples.greetings.api.MessageArray value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("echoMessageArray").getFieldDef("messages"), value);
        return this;
    }

}

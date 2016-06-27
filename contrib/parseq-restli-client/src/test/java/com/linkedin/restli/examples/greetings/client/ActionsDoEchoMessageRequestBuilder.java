
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoEchoMessageRequestBuilder
    extends ActionRequestBuilderBase<Void, Message, ActionsDoEchoMessageRequestBuilder>
{


    public ActionsDoEchoMessageRequestBuilder(String baseUriTemplate, Class<Message> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("echoMessage");
    }

    public ActionsDoEchoMessageRequestBuilder messageParam(Message value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("echoMessage").getFieldDef("message"), value);
        return this;
    }

}

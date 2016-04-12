
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AsyncErrorsDoTaskBuilder
    extends ActionRequestBuilderBase<Void, Greeting, AsyncErrorsDoTaskBuilder>
{


    public AsyncErrorsDoTaskBuilder(java.lang.String baseUriTemplate, Class<Greeting> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("task");
    }

    public AsyncErrorsDoTaskBuilder paramId(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("task").getFieldDef("id"), value);
        return this;
    }

}

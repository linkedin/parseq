
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsPromiseCtxDoSomeActionBuilder
    extends ActionRequestBuilderBase<Long, Greeting, GreetingsPromiseCtxDoSomeActionBuilder>
{


    public GreetingsPromiseCtxDoSomeActionBuilder(java.lang.String baseUriTemplate, Class<Greeting> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("someAction");
    }

    public GreetingsPromiseCtxDoSomeActionBuilder paramA(Integer value) {
        super.setParam(_resourceSpec.getRequestMetadata("someAction").getFieldDef("a"), value);
        return this;
    }

    public GreetingsPromiseCtxDoSomeActionBuilder paramB(java.lang.String value) {
        super.setParam(_resourceSpec.getRequestMetadata("someAction").getFieldDef("b"), value);
        return this;
    }

    public GreetingsPromiseCtxDoSomeActionBuilder paramC(TransferOwnershipRequest value) {
        super.setParam(_resourceSpec.getRequestMetadata("someAction").getFieldDef("c"), value);
        return this;
    }

    public GreetingsPromiseCtxDoSomeActionBuilder paramD(TransferOwnershipRequest value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("someAction").getFieldDef("d"), value);
        return this;
    }

    public GreetingsPromiseCtxDoSomeActionBuilder paramE(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("someAction").getFieldDef("e"), value);
        return this;
    }

}

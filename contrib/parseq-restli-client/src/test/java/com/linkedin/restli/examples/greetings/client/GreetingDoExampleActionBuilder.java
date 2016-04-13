
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;


/**
 * An example action on the greeting.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class GreetingDoExampleActionBuilder
    extends ActionRequestBuilderBase<Void, Integer, GreetingDoExampleActionBuilder>
{


    public GreetingDoExampleActionBuilder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("exampleAction");
    }

    public GreetingDoExampleActionBuilder paramParam1(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("exampleAction").getFieldDef("param1"), value);
        return this;
    }

}


package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoReturnIntOptionalParamBuilder
    extends ActionRequestBuilderBase<Void, Integer, ActionsDoReturnIntOptionalParamBuilder>
{


    public ActionsDoReturnIntOptionalParamBuilder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("returnIntOptionalParam");
    }

    public ActionsDoReturnIntOptionalParamBuilder paramParam(Integer value) {
        super.setParam(_resourceSpec.getRequestMetadata("returnIntOptionalParam").getFieldDef("param"), value);
        return this;
    }

}

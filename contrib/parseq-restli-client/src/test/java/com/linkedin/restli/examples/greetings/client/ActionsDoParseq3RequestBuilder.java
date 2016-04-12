
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;


/**
 * Performs three "slow" tasks and collects the results. This returns a task and lets
 *  the RestLi server invoke it.
 * Service Returns: Concatenation of binary representation of a, all caps of b, and string value
 * of c
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoParseq3RequestBuilder
    extends ActionRequestBuilderBase<Void, java.lang.String, ActionsDoParseq3RequestBuilder>
{


    public ActionsDoParseq3RequestBuilder(java.lang.String baseUriTemplate, Class<java.lang.String> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("parseq3");
    }

    public ActionsDoParseq3RequestBuilder aParam(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("parseq3").getFieldDef("a"), value);
        return this;
    }

    public ActionsDoParseq3RequestBuilder bParam(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("parseq3").getFieldDef("b"), value);
        return this;
    }

    public ActionsDoParseq3RequestBuilder cParam(Boolean value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("parseq3").getFieldDef("c"), value);
        return this;
    }

}

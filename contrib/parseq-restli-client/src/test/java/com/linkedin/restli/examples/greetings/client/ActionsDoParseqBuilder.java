
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;


/**
 * Performs three "slow" tasks and collects the results. This uses the passed context
 *  parameter to execute tasks. The position of the context argument is arbitrary.
 * Service Returns: Concatenation of binary representation of a, all caps of b, and string value
 * of c
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoParseqBuilder
    extends ActionRequestBuilderBase<Void, java.lang.String, ActionsDoParseqBuilder>
{


    public ActionsDoParseqBuilder(java.lang.String baseUriTemplate, Class<java.lang.String> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("parseq");
    }

    public ActionsDoParseqBuilder paramA(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("parseq").getFieldDef("a"), value);
        return this;
    }

    public ActionsDoParseqBuilder paramB(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("parseq").getFieldDef("b"), value);
        return this;
    }

    public ActionsDoParseqBuilder paramC(Boolean value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("parseq").getFieldDef("c"), value);
        return this;
    }

}

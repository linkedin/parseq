
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;


/**
 * An example action on the greeting.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class SubsubgreetingDoExampleActionRequestBuilder
    extends ActionRequestBuilderBase<Void, Integer, SubsubgreetingDoExampleActionRequestBuilder>
{


    public SubsubgreetingDoExampleActionRequestBuilder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("exampleAction");
    }

    public SubsubgreetingDoExampleActionRequestBuilder param1Param(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("exampleAction").getFieldDef("param1"), value);
        return this;
    }

    public SubsubgreetingDoExampleActionRequestBuilder subgreetingsIdKey(Long key) {
        super.pathKey("subgreetingsId", key);
        return this;
    }

}

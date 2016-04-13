
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.ComplexArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexArrayDoActionBuilder
    extends ActionRequestBuilderBase<Void, Integer, ComplexArrayDoActionBuilder>
{


    public ComplexArrayDoActionBuilder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("action");
    }

    public ComplexArrayDoActionBuilder paramArray(ComplexArray value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("action").getFieldDef("array"), value);
        return this;
    }

}

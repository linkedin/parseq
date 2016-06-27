
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.ComplexArray;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexArrayFindByFinderBuilder
    extends FindRequestBuilderBase<ComplexResourceKey<ComplexArray, ComplexArray> , Greeting, ComplexArrayFindByFinderBuilder>
{


    public ComplexArrayFindByFinderBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("finder");
    }

    public ComplexArrayFindByFinderBuilder arrayParam(ComplexArray value) {
        super.setReqParam("array", value, ComplexArray.class);
        return this;
    }

}

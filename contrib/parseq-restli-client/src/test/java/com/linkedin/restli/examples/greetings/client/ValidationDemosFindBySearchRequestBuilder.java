
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.ValidationDemo;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ValidationDemosFindBySearchRequestBuilder
    extends FindRequestBuilderBase<Integer, ValidationDemo, ValidationDemosFindBySearchRequestBuilder>
{


    public ValidationDemosFindBySearchRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ValidationDemo.class, resourceSpec, requestOptions);
        super.name("search");
    }

    public ValidationDemosFindBySearchRequestBuilder intAParam(Integer value) {
        super.setReqParam("intA", value, Integer.class);
        return this;
    }

}

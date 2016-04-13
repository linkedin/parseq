
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class ManualProjectionsGetBuilder
    extends GetRequestBuilderBase<Long, Greeting, ManualProjectionsGetBuilder>
{


    public ManualProjectionsGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public ManualProjectionsGetBuilder ignoreProjectionParam(Boolean value) {
        super.setParam("ignoreProjection", value, Boolean.class);
        return this;
    }

}

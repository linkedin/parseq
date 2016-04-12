
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.custom.types.CustomLong;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesFindByCustomLongBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomTypesFindByCustomLongBuilder>
{


    public CustomTypesFindByCustomLongBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("customLong");
    }

    public CustomTypesFindByCustomLongBuilder lParam(CustomLong value) {
        super.setReqParam("l", value, CustomLong.class);
        return this;
    }

}

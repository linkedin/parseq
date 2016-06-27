
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.ValidationDemo;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AutoValidationDemosGetBuilder
    extends GetRequestBuilderBase<Integer, ValidationDemo, AutoValidationDemosGetBuilder>
{


    public AutoValidationDemosGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ValidationDemo.class, resourceSpec, requestOptions);
    }

}


package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.ValidationDemo;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AutoValidationDemosGetRequestBuilder
    extends GetRequestBuilderBase<Integer, ValidationDemo, AutoValidationDemosGetRequestBuilder>
{


    public AutoValidationDemosGetRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ValidationDemo.class, resourceSpec, requestOptions);
    }

}

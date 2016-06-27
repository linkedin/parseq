
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetAllRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.ValidationDemo;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ValidationDemosGetAllRequestBuilder
    extends GetAllRequestBuilderBase<Integer, ValidationDemo, ValidationDemosGetAllRequestBuilder>
{


    public ValidationDemosGetAllRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ValidationDemo.class, resourceSpec, requestOptions);
    }

}

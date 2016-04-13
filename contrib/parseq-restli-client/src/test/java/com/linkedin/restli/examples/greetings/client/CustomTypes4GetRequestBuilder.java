
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypes4GetRequestBuilder
    extends GetRequestBuilderBase<com.linkedin.restli.examples.custom.types.CustomLong, Greeting, CustomTypes4GetRequestBuilder>
{


    public CustomTypes4GetRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public CustomTypes4GetRequestBuilder customTypes2IdKey(com.linkedin.restli.examples.custom.types.CustomLong key) {
        super.pathKey("customTypes2Id", key);
        return this;
    }

}

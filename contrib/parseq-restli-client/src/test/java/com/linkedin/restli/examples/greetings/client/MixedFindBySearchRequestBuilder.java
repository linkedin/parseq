
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class MixedFindBySearchRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, MixedFindBySearchRequestBuilder>
{


    public MixedFindBySearchRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("search");
    }

    public MixedFindBySearchRequestBuilder whatParam(java.lang.String value) {
        super.setReqParam("what", value, java.lang.String.class);
        return this;
    }

}

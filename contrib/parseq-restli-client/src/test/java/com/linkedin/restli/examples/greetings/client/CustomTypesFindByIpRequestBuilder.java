
package com.linkedin.restli.examples.greetings.client;

import java.net.InetAddress;
import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesFindByIpRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomTypesFindByIpRequestBuilder>
{


    public CustomTypesFindByIpRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("ip");
    }

    public CustomTypesFindByIpRequestBuilder ipParam(InetAddress value) {
        super.setReqParam("ip", value, InetAddress.class);
        return this;
    }

}

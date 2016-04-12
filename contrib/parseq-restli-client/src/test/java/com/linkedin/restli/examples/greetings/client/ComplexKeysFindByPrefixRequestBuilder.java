
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexKeysFindByPrefixRequestBuilder
    extends FindRequestBuilderBase<ComplexResourceKey<TwoPartKey, TwoPartKey> , Message, ComplexKeysFindByPrefixRequestBuilder>
{


    public ComplexKeysFindByPrefixRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
        super.name("prefix");
    }

    public ComplexKeysFindByPrefixRequestBuilder prefixParam(java.lang.String value) {
        super.setReqParam("prefix", value, java.lang.String.class);
        return this;
    }

}

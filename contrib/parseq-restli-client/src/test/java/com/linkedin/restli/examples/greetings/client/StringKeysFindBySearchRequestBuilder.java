
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class StringKeysFindBySearchRequestBuilder
    extends FindRequestBuilderBase<java.lang.String, Message, StringKeysFindBySearchRequestBuilder>
{


    public StringKeysFindBySearchRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
        super.name("search");
    }

    public StringKeysFindBySearchRequestBuilder keywordParam(java.lang.String value) {
        super.setParam("keyword", value, java.lang.String.class);
        return this;
    }

}

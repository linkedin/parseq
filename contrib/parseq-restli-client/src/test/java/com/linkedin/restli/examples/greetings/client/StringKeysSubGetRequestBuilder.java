
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class StringKeysSubGetRequestBuilder
    extends GetRequestBuilderBase<java.lang.String, Message, StringKeysSubGetRequestBuilder>
{


    public StringKeysSubGetRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
    }

    public StringKeysSubGetRequestBuilder parentKeyKey(java.lang.String key) {
        super.pathKey("parentKey", key);
        return this;
    }

}

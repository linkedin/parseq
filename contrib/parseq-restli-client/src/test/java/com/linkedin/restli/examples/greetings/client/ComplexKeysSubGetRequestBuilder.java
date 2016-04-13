
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexKeysSubGetRequestBuilder
    extends GetRequestBuilderBase<java.lang.String, TwoPartKey, ComplexKeysSubGetRequestBuilder>
{


    public ComplexKeysSubGetRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, TwoPartKey.class, resourceSpec, requestOptions);
    }

    public ComplexKeysSubGetRequestBuilder keysKey(ComplexResourceKey<TwoPartKey, TwoPartKey> key) {
        super.pathKey("keys", key);
        return this;
    }

}

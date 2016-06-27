
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * Gets the greeting.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class SubsubgreetingGetBuilder
    extends GetRequestBuilderBase<Void, Greeting, SubsubgreetingGetBuilder>
{


    public SubsubgreetingGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public SubsubgreetingGetBuilder subgreetingsIdKey(Long key) {
        super.pathKey("subgreetingsId", key);
        return this;
    }

}

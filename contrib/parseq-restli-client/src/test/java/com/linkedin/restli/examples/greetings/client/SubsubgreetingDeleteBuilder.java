
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.DeleteRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * Deletes the greeting.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class SubsubgreetingDeleteBuilder
    extends DeleteRequestBuilderBase<Void, Greeting, SubsubgreetingDeleteBuilder>
{


    public SubsubgreetingDeleteBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public SubsubgreetingDeleteBuilder subgreetingsIdKey(Long key) {
        super.pathKey("subgreetingsId", key);
        return this;
    }

}

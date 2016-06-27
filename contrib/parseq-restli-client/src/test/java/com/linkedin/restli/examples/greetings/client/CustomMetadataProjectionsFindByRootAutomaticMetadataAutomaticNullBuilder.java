
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method performs automatic projection for the root object entities and automatic on the metadata
 *  as well. The caveat here is that the metadata returned by the resource method is null. We want to make sure
 *  restli doesn't fall over when it sees the null later on.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullBuilder>
{


    public CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("rootAutomaticMetadataAutomaticNull");
    }

}

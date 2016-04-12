
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method is used to create additional paging metadata for fields such as links. Client side
 *  tests can use this method to potentially project on fields inside of links.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsFindBySearchWithLinksResultBuilder
    extends FindRequestBuilderBase<Long, Greeting, PagingMetadataProjectionsFindBySearchWithLinksResultBuilder>
{


    public PagingMetadataProjectionsFindBySearchWithLinksResultBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("searchWithLinksResult");
    }

}


package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method performs automatic projection for the custom metadata and complete automatic projection
 *  for paging. This means that it will provide a total in its construction of CollectionResult.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsFindByMetadataAutomaticPagingFullyAutomaticBuilder
    extends FindRequestBuilderBase<Long, Greeting, PagingMetadataProjectionsFindByMetadataAutomaticPagingFullyAutomaticBuilder>
{


    public PagingMetadataProjectionsFindByMetadataAutomaticPagingFullyAutomaticBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("metadataAutomaticPagingFullyAutomatic");
    }

}

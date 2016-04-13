
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method performs automatic projection for the custom metadata and automatic projection
 *  for paging. This particular resource method also varies on what it sets total to.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullRequestBuilder>
{


    public PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("metadataAutomaticPagingAutomaticPartialNull");
    }

}

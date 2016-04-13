
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method performs automatic projection for the custom metadata and automatic projection
 *  for paging. This particular resource method also varies on what it sets total to.
 *  The caveat with this test is that it incorrectly assigns a non null value for the total
 *  even though the MaskTree says to exclude it.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullIncorrectBuilder
    extends FindRequestBuilderBase<Long, Greeting, PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullIncorrectBuilder>
{


    public PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullIncorrectBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("metadataAutomaticPagingAutomaticPartialNullIncorrect");
    }

}

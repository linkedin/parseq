
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method performs manual projection for the custom metadata and automatic projection
 *  for paging. This particular resource method also varies on what it sets total to.
 *  Comments excluded since its combining behavior from the previous tests.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsFindByMetadataManualPagingAutomaticPartialNullBuilder
    extends FindRequestBuilderBase<Long, Greeting, PagingMetadataProjectionsFindByMetadataManualPagingAutomaticPartialNullBuilder>
{


    public PagingMetadataProjectionsFindByMetadataManualPagingAutomaticPartialNullBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("metadataManualPagingAutomaticPartialNull");
    }

}

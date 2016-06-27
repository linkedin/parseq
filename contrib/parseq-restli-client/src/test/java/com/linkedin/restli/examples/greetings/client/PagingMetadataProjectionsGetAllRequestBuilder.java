
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetAllRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * Same as the test above except that this test is to make sure that GET_ALL observes the same code path in
 *  restli as FINDER does for custom metadata and paging projection.
 *  Redundant comments excluded for the sake of brevity.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsGetAllRequestBuilder
    extends GetAllRequestBuilderBase<Long, Greeting, PagingMetadataProjectionsGetAllRequestBuilder>
{


    public PagingMetadataProjectionsGetAllRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

}

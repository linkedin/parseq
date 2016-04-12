
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource method performs manual projection for the root object entities and automatic projection for the
 *  custom metadata.
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomMetadataProjectionsFindByRootManualMetadataAutomaticRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomMetadataProjectionsFindByRootManualMetadataAutomaticRequestBuilder>
{


    public CustomMetadataProjectionsFindByRootManualMetadataAutomaticRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("rootManualMetadataAutomatic");
    }

}


package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;


/**
 * Simulates a delay in an asynchronous resource. The delay is simulated using a scheduled task (asynchronously).
 *  That is how a typical async resource looks like in terms of delays.
 * Service Returns: Nothing
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoTaskExecutionDelayRequestBuilder
    extends ActionRequestBuilderBase<Void, Void, ActionsDoTaskExecutionDelayRequestBuilder>
{


    public ActionsDoTaskExecutionDelayRequestBuilder(String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("taskExecutionDelay");
    }

    public ActionsDoTaskExecutionDelayRequestBuilder delayParam(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("taskExecutionDelay").getFieldDef("delay"), value);
        return this;
    }

}

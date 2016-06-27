
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;


/**
 * Simulates a delay in an asynchronous resource caused by ParSeq execution plan creation. The delay is simulated as
 *  {@link Thread#sleep(long)} because execution plan creation is a synchronous operation.
 * Service Returns: Nothing
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoTaskCreationDelayRequestBuilder
    extends ActionRequestBuilderBase<Void, Void, ActionsDoTaskCreationDelayRequestBuilder>
{


    public ActionsDoTaskCreationDelayRequestBuilder(String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("taskCreationDelay");
    }

    public ActionsDoTaskCreationDelayRequestBuilder delayParam(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("taskCreationDelay").getFieldDef("delay"), value);
        return this;
    }

}

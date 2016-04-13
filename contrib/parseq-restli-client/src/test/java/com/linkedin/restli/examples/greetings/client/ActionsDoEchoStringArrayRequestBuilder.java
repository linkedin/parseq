
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.data.template.StringArray;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsDoEchoStringArrayRequestBuilder
    extends ActionRequestBuilderBase<Void, StringArray, ActionsDoEchoStringArrayRequestBuilder>
{


    public ActionsDoEchoStringArrayRequestBuilder(String baseUriTemplate, Class<StringArray> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("echoStringArray");
    }

    public ActionsDoEchoStringArrayRequestBuilder stringsParam(StringArray value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("echoStringArray").getFieldDef("strings"), value);
        return this;
    }

}

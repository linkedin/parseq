
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.custom.types.CustomLong;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesDoActionRequestBuilder
    extends ActionRequestBuilderBase<Void, Long, CustomTypesDoActionRequestBuilder>
{


    public CustomTypesDoActionRequestBuilder(String baseUriTemplate, Class<Long> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("action");
    }

    public CustomTypesDoActionRequestBuilder lParam(CustomLong value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("action").getFieldDef("l"), value);
        return this;
    }

}


package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesDoArrayActionBuilder
    extends ActionRequestBuilderBase<Void, com.linkedin.restli.examples.typeref.api.CustomLongRefArray, CustomTypesDoArrayActionBuilder>
{


    public CustomTypesDoArrayActionBuilder(String baseUriTemplate, Class<com.linkedin.restli.examples.typeref.api.CustomLongRefArray> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("arrayAction");
    }

    public CustomTypesDoArrayActionBuilder paramLs(com.linkedin.restli.examples.typeref.api.CustomLongRefArray value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("arrayAction").getFieldDef("ls"), value);
        return this;
    }

}

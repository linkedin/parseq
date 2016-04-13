
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoStringFuncRequestBuilder
    extends ActionRequestBuilderBase<Void, java.lang.String, TyperefDoStringFuncRequestBuilder>
{


    public TyperefDoStringFuncRequestBuilder(java.lang.String baseUriTemplate, Class<java.lang.String> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("StringFunc");
    }

    public TyperefDoStringFuncRequestBuilder arg1Param(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("StringFunc").getFieldDef("arg1"), value);
        return this;
    }

}

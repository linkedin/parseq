
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoFloatFuncRequestBuilder
    extends ActionRequestBuilderBase<Void, Float, TyperefDoFloatFuncRequestBuilder>
{


    public TyperefDoFloatFuncRequestBuilder(String baseUriTemplate, Class<Float> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("floatFunc");
    }

    public TyperefDoFloatFuncRequestBuilder arg1Param(Float value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("floatFunc").getFieldDef("arg1"), value);
        return this;
    }

}

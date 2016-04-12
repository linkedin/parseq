
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoFloatFunc2RequestBuilder
    extends ActionRequestBuilderBase<Void, Float, TyperefDoFloatFunc2RequestBuilder>
{


    public TyperefDoFloatFunc2RequestBuilder(String baseUriTemplate, Class<Float> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("floatFunc2");
    }

    public TyperefDoFloatFunc2RequestBuilder arg1Param(Float value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("floatFunc2").getFieldDef("arg1"), value);
        return this;
    }

}

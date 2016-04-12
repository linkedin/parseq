
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoDoubleFuncBuilder
    extends ActionRequestBuilderBase<Void, Double, TyperefDoDoubleFuncBuilder>
{


    public TyperefDoDoubleFuncBuilder(String baseUriTemplate, Class<Double> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("doubleFunc");
    }

    public TyperefDoDoubleFuncBuilder paramArg1(Double value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("doubleFunc").getFieldDef("arg1"), value);
        return this;
    }

}

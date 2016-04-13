
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoIntFunc2Builder
    extends ActionRequestBuilderBase<Void, Integer, TyperefDoIntFunc2Builder>
{


    public TyperefDoIntFunc2Builder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("intFunc2");
    }

    public TyperefDoIntFunc2Builder paramArg1(Integer value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("intFunc2").getFieldDef("arg1"), value);
        return this;
    }

}

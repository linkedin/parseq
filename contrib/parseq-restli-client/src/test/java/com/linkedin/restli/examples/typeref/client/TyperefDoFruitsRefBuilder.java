
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.typeref.api.Fruits;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoFruitsRefBuilder
    extends ActionRequestBuilderBase<Void, Fruits, TyperefDoFruitsRefBuilder>
{


    public TyperefDoFruitsRefBuilder(String baseUriTemplate, Class<Fruits> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("FruitsRef");
    }

    public TyperefDoFruitsRefBuilder paramArg1(Fruits value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("FruitsRef").getFieldDef("arg1"), value);
        return this;
    }

}

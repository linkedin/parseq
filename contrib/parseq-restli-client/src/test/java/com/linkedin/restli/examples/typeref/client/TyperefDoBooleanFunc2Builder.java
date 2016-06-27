
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoBooleanFunc2Builder
    extends ActionRequestBuilderBase<Void, Boolean, TyperefDoBooleanFunc2Builder>
{


    public TyperefDoBooleanFunc2Builder(String baseUriTemplate, Class<Boolean> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("booleanFunc2");
    }

    public TyperefDoBooleanFunc2Builder paramArg1(Boolean value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("booleanFunc2").getFieldDef("arg1"), value);
        return this;
    }

}


package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoStringFuncBuilder
    extends ActionRequestBuilderBase<Void, java.lang.String, TyperefDoStringFuncBuilder>
{


    public TyperefDoStringFuncBuilder(java.lang.String baseUriTemplate, Class<java.lang.String> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("StringFunc");
    }

    public TyperefDoStringFuncBuilder paramArg1(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("StringFunc").getFieldDef("arg1"), value);
        return this;
    }

}

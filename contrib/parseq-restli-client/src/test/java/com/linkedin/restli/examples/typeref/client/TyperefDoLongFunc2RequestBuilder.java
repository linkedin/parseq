
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoLongFunc2RequestBuilder
    extends ActionRequestBuilderBase<Void, Long, TyperefDoLongFunc2RequestBuilder>
{


    public TyperefDoLongFunc2RequestBuilder(String baseUriTemplate, Class<Long> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("longFunc2");
    }

    public TyperefDoLongFunc2RequestBuilder arg1Param(Long value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("longFunc2").getFieldDef("arg1"), value);
        return this;
    }

}

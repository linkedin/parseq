
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.data.template.IntegerMap;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoIntMapFuncBuilder
    extends ActionRequestBuilderBase<Void, IntegerMap, TyperefDoIntMapFuncBuilder>
{


    public TyperefDoIntMapFuncBuilder(String baseUriTemplate, Class<IntegerMap> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("IntMapFunc");
    }

    public TyperefDoIntMapFuncBuilder paramArg1(IntegerMap value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("IntMapFunc").getFieldDef("arg1"), value);
        return this;
    }

}

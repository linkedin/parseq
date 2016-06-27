
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.data.ByteString;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoBytesFuncBuilder
    extends ActionRequestBuilderBase<Void, ByteString, TyperefDoBytesFuncBuilder>
{


    public TyperefDoBytesFuncBuilder(String baseUriTemplate, Class<ByteString> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("BytesFunc");
    }

    public TyperefDoBytesFuncBuilder paramArg1(ByteString value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("BytesFunc").getFieldDef("arg1"), value);
        return this;
    }

}


package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.custom.types.CustomString;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.typeref.api.CustomStringRefArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class TyperefCustomDoubleAssociationKeyResourceGetBuilder
    extends GetRequestBuilderBase<CompoundKey, Message, TyperefCustomDoubleAssociationKeyResourceGetBuilder>
{


    public TyperefCustomDoubleAssociationKeyResourceGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
    }

    public TyperefCustomDoubleAssociationKeyResourceGetBuilder arrayParam(CustomStringRefArray value) {
        super.setReqParam("array", value, CustomStringRefArray.class);
        return this;
    }

    public TyperefCustomDoubleAssociationKeyResourceGetBuilder arrayParam(Iterable<CustomString> value) {
        super.setReqParam("array", value, CustomString.class);
        return this;
    }

    public TyperefCustomDoubleAssociationKeyResourceGetBuilder addArrayParam(CustomString value) {
        super.addReqParam("array", value, CustomString.class);
        return this;
    }

}

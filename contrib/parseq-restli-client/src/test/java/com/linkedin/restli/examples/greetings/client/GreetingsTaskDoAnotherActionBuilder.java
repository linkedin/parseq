
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.data.template.BooleanArray;
import com.linkedin.data.template.StringMap;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class GreetingsTaskDoAnotherActionBuilder
    extends ActionRequestBuilderBase<Void, Void, GreetingsTaskDoAnotherActionBuilder>
{


    public GreetingsTaskDoAnotherActionBuilder(java.lang.String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("anotherAction");
    }

    public GreetingsTaskDoAnotherActionBuilder paramBitfield(BooleanArray value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("anotherAction").getFieldDef("bitfield"), value);
        return this;
    }

    public GreetingsTaskDoAnotherActionBuilder paramRequest(TransferOwnershipRequest value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("anotherAction").getFieldDef("request"), value);
        return this;
    }

    public GreetingsTaskDoAnotherActionBuilder paramSomeString(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("anotherAction").getFieldDef("someString"), value);
        return this;
    }

    public GreetingsTaskDoAnotherActionBuilder paramStringMap(StringMap value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("anotherAction").getFieldDef("stringMap"), value);
        return this;
    }

}

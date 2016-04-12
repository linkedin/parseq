
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.custom.types.CustomLong;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CustomLongRefArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesFindByCustomLongArrayBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomTypesFindByCustomLongArrayBuilder>
{


    public CustomTypesFindByCustomLongArrayBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("customLongArray");
    }

    public CustomTypesFindByCustomLongArrayBuilder lsParam(CustomLongRefArray value) {
        super.setReqParam("ls", value, CustomLongRefArray.class);
        return this;
    }

    public CustomTypesFindByCustomLongArrayBuilder lsParam(Iterable<CustomLong> value) {
        super.setReqParam("ls", value, CustomLong.class);
        return this;
    }

    public CustomTypesFindByCustomLongArrayBuilder addLsParam(CustomLong value) {
        super.addReqParam("ls", value, CustomLong.class);
        return this;
    }

}

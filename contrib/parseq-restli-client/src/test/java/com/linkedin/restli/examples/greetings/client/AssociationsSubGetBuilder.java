
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AssociationsSubGetBuilder
    extends GetRequestBuilderBase<java.lang.String, Message, AssociationsSubGetBuilder>
{

    private CompoundKey associationsId = new CompoundKey();

    public AssociationsSubGetBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
    }

    public AssociationsSubGetBuilder srcKey(java.lang.String key) {
        associationsId.append("src", key);
        super.pathKey("src", key);
        super.pathKey("associationsId", associationsId);
        return this;
    }

    public AssociationsSubGetBuilder destKey(java.lang.String key) {
        associationsId.append("dest", key);
        super.pathKey("dest", key);
        super.pathKey("associationsId", associationsId);
        return this;
    }

}

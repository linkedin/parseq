
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.Tone;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AssociationsSubFindByToneBuilder
    extends FindRequestBuilderBase<java.lang.String, Message, AssociationsSubFindByToneBuilder>
{

    private CompoundKey associationsId = new CompoundKey();

    public AssociationsSubFindByToneBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
        super.name("tone");
    }

    public AssociationsSubFindByToneBuilder srcKey(java.lang.String key) {
        associationsId.append("src", key);
        super.pathKey("src", key);
        super.pathKey("associationsId", associationsId);
        return this;
    }

    public AssociationsSubFindByToneBuilder destKey(java.lang.String key) {
        associationsId.append("dest", key);
        super.pathKey("dest", key);
        super.pathKey("associationsId", associationsId);
        return this;
    }

    public AssociationsSubFindByToneBuilder toneParam(Tone value) {
        super.setReqParam("tone", value, Tone.class);
        return this;
    }

}

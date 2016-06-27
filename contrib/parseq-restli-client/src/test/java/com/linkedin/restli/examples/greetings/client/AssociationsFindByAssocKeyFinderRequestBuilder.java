
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AssociationsFindByAssocKeyFinderRequestBuilder
    extends FindRequestBuilderBase<CompoundKey, Message, AssociationsFindByAssocKeyFinderRequestBuilder>
{


    public AssociationsFindByAssocKeyFinderRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
        super.name("assocKeyFinder");
    }

    public AssociationsFindByAssocKeyFinderRequestBuilder srcKey(java.lang.String key) {
        super.assocKey("src", key);
        return this;
    }

}

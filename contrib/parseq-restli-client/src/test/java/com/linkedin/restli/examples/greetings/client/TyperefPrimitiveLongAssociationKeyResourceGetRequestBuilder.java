
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class TyperefPrimitiveLongAssociationKeyResourceGetRequestBuilder
    extends GetRequestBuilderBase<CompoundKey, Message, TyperefPrimitiveLongAssociationKeyResourceGetRequestBuilder>
{


    public TyperefPrimitiveLongAssociationKeyResourceGetRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
    }

}

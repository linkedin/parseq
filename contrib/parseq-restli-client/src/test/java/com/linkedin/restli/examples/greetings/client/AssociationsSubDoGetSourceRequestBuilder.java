
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AssociationsSubDoGetSourceRequestBuilder
    extends ActionRequestBuilderBase<Void, java.lang.String, AssociationsSubDoGetSourceRequestBuilder>
{

    private CompoundKey associationsId = new CompoundKey();

    public AssociationsSubDoGetSourceRequestBuilder(java.lang.String baseUriTemplate, Class<java.lang.String> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("getSource");
    }

    public AssociationsSubDoGetSourceRequestBuilder srcKey(java.lang.String key) {
        associationsId.append("src", key);
        super.pathKey("src", key);
        super.pathKey("associationsId", associationsId);
        return this;
    }

    public AssociationsSubDoGetSourceRequestBuilder destKey(java.lang.String key) {
        associationsId.append("dest", key);
        super.pathKey("dest", key);
        super.pathKey("associationsId", associationsId);
        return this;
    }

}

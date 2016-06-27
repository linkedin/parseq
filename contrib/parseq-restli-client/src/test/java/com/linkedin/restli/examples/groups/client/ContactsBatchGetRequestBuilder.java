
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BatchGetEntityRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupContact;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ContactsBatchGetRequestBuilder
    extends BatchGetEntityRequestBuilderBase<Integer, GroupContact, ContactsBatchGetRequestBuilder>
{


    public ContactsBatchGetRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, resourceSpec, requestOptions);
    }

    public ContactsBatchGetRequestBuilder groupIdKey(Integer key) {
        super.pathKey("groupID", key);
        return this;
    }

}


package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ContactsDoSpamContactsRequestBuilder
    extends ActionRequestBuilderBase<Void, Void, ContactsDoSpamContactsRequestBuilder>
{


    public ContactsDoSpamContactsRequestBuilder(String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("spamContacts");
    }

    public ContactsDoSpamContactsRequestBuilder groupIdKey(Integer key) {
        super.pathKey("groupID", key);
        return this;
    }

}

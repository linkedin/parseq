
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.DeleteRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupContact;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ContactsDeleteBuilder
    extends DeleteRequestBuilderBase<Integer, GroupContact, ContactsDeleteBuilder>
{


    public ContactsDeleteBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupContact.class, resourceSpec, requestOptions);
    }

    public ContactsDeleteBuilder groupIdKey(Integer key) {
        super.pathKey("groupID", key);
        return this;
    }

}

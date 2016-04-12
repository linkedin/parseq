
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.GroupContact;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ContactsGetBuilder
    extends GetRequestBuilderBase<Integer, GroupContact, ContactsGetBuilder>
{


    public ContactsGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupContact.class, resourceSpec, requestOptions);
    }

    public ContactsGetBuilder groupIdKey(Integer key) {
        super.pathKey("groupID", key);
        return this;
    }

}


package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsDoSendTestAnnouncementBuilder
    extends ActionRequestBuilderBase<Integer, Void, GroupsDoSendTestAnnouncementBuilder>
{


    public GroupsDoSendTestAnnouncementBuilder(java.lang.String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("sendTestAnnouncement");
    }

    public GroupsDoSendTestAnnouncementBuilder paramSubject(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("sendTestAnnouncement").getFieldDef("subject"), value);
        return this;
    }

    public GroupsDoSendTestAnnouncementBuilder paramMessage(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("sendTestAnnouncement").getFieldDef("message"), value);
        return this;
    }

    public GroupsDoSendTestAnnouncementBuilder paramEmailAddress(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("sendTestAnnouncement").getFieldDef("emailAddress"), value);
        return this;
    }

}

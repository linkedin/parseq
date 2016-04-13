
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsDoSendTestAnnouncementRequestBuilder
    extends ActionRequestBuilderBase<Integer, Void, GroupsDoSendTestAnnouncementRequestBuilder>
{


    public GroupsDoSendTestAnnouncementRequestBuilder(java.lang.String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("sendTestAnnouncement");
    }

    public GroupsDoSendTestAnnouncementRequestBuilder subjectParam(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("sendTestAnnouncement").getFieldDef("subject"), value);
        return this;
    }

    public GroupsDoSendTestAnnouncementRequestBuilder messageParam(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("sendTestAnnouncement").getFieldDef("message"), value);
        return this;
    }

    public GroupsDoSendTestAnnouncementRequestBuilder emailAddressParam(java.lang.String value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("sendTestAnnouncement").getFieldDef("emailAddress"), value);
        return this;
    }

}

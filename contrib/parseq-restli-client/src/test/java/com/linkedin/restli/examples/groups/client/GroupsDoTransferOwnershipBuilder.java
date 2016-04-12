
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsDoTransferOwnershipBuilder
    extends ActionRequestBuilderBase<Integer, Void, GroupsDoTransferOwnershipBuilder>
{


    public GroupsDoTransferOwnershipBuilder(String baseUriTemplate, Class<Void> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("transferOwnership");
    }

    public GroupsDoTransferOwnershipBuilder paramRequest(TransferOwnershipRequest value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("transferOwnership").getFieldDef("request"), value);
        return this;
    }

}

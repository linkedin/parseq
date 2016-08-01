
package com.linkedin.restli.examples.groups.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.groups.api.Group;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;


/**
 *
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupsResource2
 *
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groups.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "groups";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> sendTestAnnouncementParams = new ArrayList<FieldDef<?>>();
        sendTestAnnouncementParams.add(new FieldDef<java.lang.String>("subject", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        sendTestAnnouncementParams.add(new FieldDef<java.lang.String>("message", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        sendTestAnnouncementParams.add(new FieldDef<java.lang.String>("emailAddress", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        requestMetadataMap.put("sendTestAnnouncement", new DynamicRecordMetadata("sendTestAnnouncement", sendTestAnnouncementParams));
        ArrayList<FieldDef<?>> transferOwnershipParams = new ArrayList<FieldDef<?>>();
        transferOwnershipParams.add(new FieldDef<TransferOwnershipRequest>("request", TransferOwnershipRequest.class, DataTemplateUtil.getSchema(TransferOwnershipRequest.class)));
        requestMetadataMap.put("transferOwnership", new DynamicRecordMetadata("transferOwnership", transferOwnershipParams));
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        responseMetadataMap.put("sendTestAnnouncement", new DynamicRecordMetadata("sendTestAnnouncement", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("transferOwnership", new DynamicRecordMetadata("transferOwnership", Collections.<FieldDef<?>>emptyList()));
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.DELETE), requestMetadataMap, responseMetadataMap, Integer.class, null, null, Group.class, keyParts);
    }

    public GroupsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GroupsRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupsRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GroupsBatchGetRequestBuilder batchGet() {
        return new GroupsBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsDeleteRequestBuilder delete() {
        return new GroupsDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsCreateRequestBuilder create() {
        return new GroupsCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsGetRequestBuilder get() {
        return new GroupsGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsPartialUpdateRequestBuilder partialUpdate() {
        return new GroupsPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Test the default value for various types
     *
     * @return
     *     builder for the resource method
     */
    public GroupsFindByComplexCircuitRequestBuilder findByComplexCircuit() {
        return new GroupsFindByComplexCircuitRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsFindByEmailDomainRequestBuilder findByEmailDomain() {
        return new GroupsFindByEmailDomainRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsFindByManagerRequestBuilder findByManager() {
        return new GroupsFindByManagerRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsFindBySearchRequestBuilder findBySearch() {
        return new GroupsFindBySearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsDoSendTestAnnouncementRequestBuilder actionSendTestAnnouncement() {
        return new GroupsDoSendTestAnnouncementRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GroupsDoTransferOwnershipRequestBuilder actionTransferOwnership() {
        return new GroupsDoTransferOwnershipRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}

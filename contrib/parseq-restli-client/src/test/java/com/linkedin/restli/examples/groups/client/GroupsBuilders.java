
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
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.groups.api.Group;
import com.linkedin.restli.examples.groups.api.TransferOwnershipRequest;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * TODO Derive path, resourceClass and keyName from class names (GroupsResource => /groups, GroupResource.class, "groupId")
 * 
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupsResource2
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groups.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public GroupsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public GroupsBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupsBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = primaryResourceName;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    private java.lang.String getBaseUriTemplate() {
        return _baseUriTemplate;
    }

    public RestliRequestOptions getRequestOptions() {
        return _requestOptions;
    }

    public java.lang.String[] getPathComponents() {
        return URIParamUtils.extractPathComponentsFromUriTemplate(_baseUriTemplate);
    }

    private static RestliRequestOptions assignRequestOptions(RestliRequestOptions requestOptions) {
        if (requestOptions == null) {
            return RestliRequestOptions.DEFAULT_OPTIONS;
        } else {
            return requestOptions;
        }
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GroupsBatchGetBuilder batchGet() {
        return new GroupsBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsDeleteBuilder delete() {
        return new GroupsDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsCreateBuilder create() {
        return new GroupsCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsGetBuilder get() {
        return new GroupsGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsPartialUpdateBuilder partialUpdate() {
        return new GroupsPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Test the default value for various types
     * 
     * @return
     *     builder for the resource method
     */
    public GroupsFindByComplexCircuitBuilder findByComplexCircuit() {
        return new GroupsFindByComplexCircuitBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsFindByEmailDomainBuilder findByEmailDomain() {
        return new GroupsFindByEmailDomainBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsFindByManagerBuilder findByManager() {
        return new GroupsFindByManagerBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsFindBySearchBuilder findBySearch() {
        return new GroupsFindBySearchBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupsDoSendTestAnnouncementBuilder actionSendTestAnnouncement() {
        return new GroupsDoSendTestAnnouncementBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public GroupsDoTransferOwnershipBuilder actionTransferOwnership() {
        return new GroupsDoTransferOwnershipBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}


package com.linkedin.restli.examples.groups.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.groups.api.ComplexKeyGroupMembership;
import com.linkedin.restli.examples.groups.api.GroupMembershipKey;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupMembershipsResource3
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groupMembershipsComplex.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsComplexBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "groupMembershipsComplex";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.BATCH_CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.DELETE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, GroupMembershipKey.class, GroupMembershipParam.class, ComplexKeyGroupMembership.class, keyParts);
    }

    public GroupMembershipsComplexBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsComplexBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public GroupMembershipsComplexBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GroupMembershipsComplexBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = primaryResourceName;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    private String getBaseUriTemplate() {
        return _baseUriTemplate;
    }

    public RestliRequestOptions getRequestOptions() {
        return _requestOptions;
    }

    public String[] getPathComponents() {
        return URIParamUtils.extractPathComponentsFromUriTemplate(_baseUriTemplate);
    }

    private static RestliRequestOptions assignRequestOptions(RestliRequestOptions requestOptions) {
        if (requestOptions == null) {
            return RestliRequestOptions.DEFAULT_OPTIONS;
        } else {
            return requestOptions;
        }
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public GroupMembershipsComplexBatchGetBuilder batchGet() {
        return new GroupMembershipsComplexBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchUpdateBuilder batchUpdate() {
        return new GroupMembershipsComplexBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchDeleteBuilder batchDelete() {
        return new GroupMembershipsComplexBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexDeleteBuilder delete() {
        return new GroupMembershipsComplexDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchCreateBuilder batchCreate() {
        return new GroupMembershipsComplexBatchCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexCreateBuilder create() {
        return new GroupMembershipsComplexCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexUpdateBuilder update() {
        return new GroupMembershipsComplexUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexGetBuilder get() {
        return new GroupMembershipsComplexGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexBatchPartialUpdateBuilder batchPartialUpdate() {
        return new GroupMembershipsComplexBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public GroupMembershipsComplexPartialUpdateBuilder partialUpdate() {
        return new GroupMembershipsComplexPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}


package com.linkedin.restli.examples.groups.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.groups.api.GroupContact;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * TODO Not implemented in MongoDB yet
 * 
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupContactsResource2
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groups.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ContactsBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "groups/{groupID}/contacts";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> spamContactsParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("spamContacts", new DynamicRecordMetadata("spamContacts", spamContactsParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("spamContacts", new DynamicRecordMetadata("spamContacts", Collections.<FieldDef<?>>emptyList()));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.DELETE), requestMetadataMap, responseMetadataMap, Integer.class, null, null, GroupContact.class, keyParts);
    }

    public ContactsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ContactsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public ContactsBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ContactsBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH.replaceFirst("[^/]*/", (primaryResourceName +"/"));
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

    public ContactsBatchGetBuilder batchGet() {
        return new ContactsBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsDeleteBuilder delete() {
        return new ContactsDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsCreateBuilder create() {
        return new ContactsCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsGetBuilder get() {
        return new ContactsGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsPartialUpdateBuilder partialUpdate() {
        return new ContactsPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsDoSpamContactsBuilder actionSpamContacts() {
        return new ContactsDoSpamContactsBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}

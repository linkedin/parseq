
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
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.groups.api.GroupContact;


/**
 * TODO Not implemented in MongoDB yet
 * 
 * generated from: com.linkedin.restli.examples.groups.server.rest.impl.GroupContactsResource2
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groups.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class ContactsRequestBuilders
    extends BuilderBase
{

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

    public ContactsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ContactsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ContactsRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ContactsRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH.replaceFirst("[^/]*/", (primaryResourceName +"/")), requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ContactsBatchGetRequestBuilder batchGet() {
        return new ContactsBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsDeleteRequestBuilder delete() {
        return new ContactsDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsCreateRequestBuilder create() {
        return new ContactsCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsGetRequestBuilder get() {
        return new ContactsGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsPartialUpdateRequestBuilder partialUpdate() {
        return new ContactsPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ContactsDoSpamContactsRequestBuilder actionSpamContacts() {
        return new ContactsDoSpamContactsRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}


package com.linkedin.restli.examples.greetings.client;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.custom.types.CustomLong;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CustomLongRef;
import com.linkedin.restli.examples.typeref.api.DateRef;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.CustomTypesResource3
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes3.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypes3Builders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "customTypes3";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, CompoundKey.TypeInfo> keyParts = new HashMap<String, CompoundKey.TypeInfo>();
        keyParts.put("dateId", new CompoundKey.TypeInfo(Date.class, DateRef.class));
        keyParts.put("longId", new CompoundKey.TypeInfo(CustomLong.class, CustomLongRef.class));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_UPDATE), requestMetadataMap, responseMetadataMap, CompoundKey.class, null, null, Greeting.class, keyParts);
    }

    public CustomTypes3Builders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypes3Builders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public CustomTypes3Builders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypes3Builders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public CustomTypes3BatchUpdateBuilder batchUpdate() {
        return new CustomTypes3BatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypes3GetBuilder get() {
        return new CustomTypes3GetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypes3FindByDateOnlyBuilder findByDateOnly() {
        return new CustomTypes3FindByDateOnlyBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public CustomTypes3Builders.Key setDateId(Date dateId) {
            append("dateId", dateId);
            return this;
        }

        public Date getDateId() {
            return ((Date) getPart("dateId"));
        }

        public CustomTypes3Builders.Key setLongId(CustomLong longId) {
            append("longId", longId);
            return this;
        }

        public CustomLong getLongId() {
            return ((CustomLong) getPart("longId"));
        }

    }

}

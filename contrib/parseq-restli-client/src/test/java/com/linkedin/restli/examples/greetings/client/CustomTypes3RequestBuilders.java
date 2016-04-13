
package com.linkedin.restli.examples.greetings.client;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.custom.types.CustomLong;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CustomLongRef;
import com.linkedin.restli.examples.typeref.api.DateRef;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.CustomTypesResource3
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes3.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypes3RequestBuilders
    extends BuilderBase
{

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

    public CustomTypes3RequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypes3RequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public CustomTypes3RequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypes3RequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public CustomTypes3BatchUpdateRequestBuilder batchUpdate() {
        return new CustomTypes3BatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypes3GetRequestBuilder get() {
        return new CustomTypes3GetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypes3FindByDateOnlyRequestBuilder findByDateOnly() {
        return new CustomTypes3FindByDateOnlyRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public CustomTypes3RequestBuilders.Key setDateId(Date dateId) {
            append("dateId", dateId);
            return this;
        }

        public Date getDateId() {
            return ((Date) getPart("dateId"));
        }

        public CustomTypes3RequestBuilders.Key setLongId(CustomLong longId) {
            append("longId", longId);
            return this;
        }

        public CustomLong getLongId() {
            return ((CustomLong) getPart("longId"));
        }

    }

}

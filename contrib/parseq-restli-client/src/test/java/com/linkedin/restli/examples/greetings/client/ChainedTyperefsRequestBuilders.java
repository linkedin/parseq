
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
import com.linkedin.restli.examples.custom.types.CustomNonNegativeLong;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CustomNonNegativeLongRef;
import com.linkedin.restli.examples.typeref.api.DateRef;


/**
 * Uses CustomNonNegativeLong which is a typeref to CustomLong, which is a typeref to long
 * 
 *  Note that there are no coercers in this typeref chain.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ChainedTyperefResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.chainedTyperefs.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ChainedTyperefsRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "chainedTyperefs";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, CompoundKey.TypeInfo> keyParts = new HashMap<String, CompoundKey.TypeInfo>();
        keyParts.put("birthday", new CompoundKey.TypeInfo(Date.class, DateRef.class));
        keyParts.put("age", new CompoundKey.TypeInfo(CustomNonNegativeLong.class, CustomNonNegativeLongRef.class));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_UPDATE), requestMetadataMap, responseMetadataMap, CompoundKey.class, null, null, Greeting.class, keyParts);
    }

    public ChainedTyperefsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ChainedTyperefsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ChainedTyperefsRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ChainedTyperefsRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ChainedTyperefsBatchUpdateRequestBuilder batchUpdate() {
        return new ChainedTyperefsBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ChainedTyperefsGetRequestBuilder get() {
        return new ChainedTyperefsGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public ChainedTyperefsFindByDateOnlyRequestBuilder findByDateOnly() {
        return new ChainedTyperefsFindByDateOnlyRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public static class Key
        extends CompoundKey
    {


        public Key() {
        }

        public ChainedTyperefsRequestBuilders.Key setAge(CustomNonNegativeLong age) {
            append("age", age);
            return this;
        }

        public CustomNonNegativeLong getAge() {
            return ((CustomNonNegativeLong) getPart("age"));
        }

        public ChainedTyperefsRequestBuilders.Key setBirthday(Date birthday) {
            append("birthday", birthday);
            return this;
        }

        public Date getBirthday() {
            return ((Date) getPart("birthday"));
        }

    }

}

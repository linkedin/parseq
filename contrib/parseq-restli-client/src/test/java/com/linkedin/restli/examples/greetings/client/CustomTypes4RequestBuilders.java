
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CustomLongRef;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.CustomTypesResource4
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes2.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypes4RequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "customTypes2/{customTypes2Id}/customTypes4";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, CustomLongRef.class, null, null, Greeting.class, keyParts);
    }

    public CustomTypes4RequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypes4RequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public CustomTypes4RequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypes4RequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH.replaceFirst("[^/]*/", (primaryResourceName +"/")), requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public CustomTypes4GetRequestBuilder get() {
        return new CustomTypes4GetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

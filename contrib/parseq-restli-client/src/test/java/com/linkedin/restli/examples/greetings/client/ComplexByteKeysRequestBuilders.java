
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;
import com.linkedin.restli.examples.typeref.api.TyperefRecord;


/**
 * Demonstrates a resource with a complex key that consists of a field of Bytes typeref.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ComplexByteKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.complexByteKeys.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexByteKeysRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "complexByteKeys";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, TyperefRecord.class, TwoPartKey.class, TyperefRecord.class, keyParts);
    }

    public ComplexByteKeysRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexByteKeysRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ComplexByteKeysRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexByteKeysRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ComplexByteKeysGetRequestBuilder get() {
        return new ComplexByteKeysGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

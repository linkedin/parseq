
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
import com.linkedin.restli.examples.greetings.api.TwoPartKey;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.ComplexKeysSubResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.complexKeys.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexKeysSubRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "complexKeys/{keys}/complexKeysSub";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, java.lang.String.class, null, null, TwoPartKey.class, keyParts);
    }

    public ComplexKeysSubRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysSubRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ComplexKeysSubRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysSubRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH.replaceFirst("[^/]*/", (primaryResourceName +"/")), requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ComplexKeysSubGetRequestBuilder get() {
        return new ComplexKeysSubGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

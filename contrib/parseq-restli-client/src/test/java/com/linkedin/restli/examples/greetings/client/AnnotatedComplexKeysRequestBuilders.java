
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
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.AnnotatedComplexKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.annotatedComplexKeys.restspec.json.", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AnnotatedComplexKeysRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "annotatedComplexKeys";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, TwoPartKey.class, TwoPartKey.class, Message.class, keyParts);
    }

    public AnnotatedComplexKeysRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AnnotatedComplexKeysRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public AnnotatedComplexKeysRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AnnotatedComplexKeysRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public AnnotatedComplexKeysBatchGetRequestBuilder batchGet() {
        return new AnnotatedComplexKeysBatchGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysBatchUpdateRequestBuilder batchUpdate() {
        return new AnnotatedComplexKeysBatchUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysBatchDeleteRequestBuilder batchDelete() {
        return new AnnotatedComplexKeysBatchDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysCreateRequestBuilder create() {
        return new AnnotatedComplexKeysCreateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysGetRequestBuilder get() {
        return new AnnotatedComplexKeysGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysBatchPartialUpdateRequestBuilder batchPartialUpdate() {
        return new AnnotatedComplexKeysBatchPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysPartialUpdateRequestBuilder partialUpdate() {
        return new AnnotatedComplexKeysPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * <b>Test Method: For integration tests only.</b>
     * Example javadoc
     * 
     * @return
     *     builder for the resource method
     */
    public AnnotatedComplexKeysFindByPrefixRequestBuilder findByPrefix() {
        return new AnnotatedComplexKeysFindByPrefixRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

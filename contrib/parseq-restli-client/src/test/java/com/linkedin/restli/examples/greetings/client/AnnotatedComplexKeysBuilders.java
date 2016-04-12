
package com.linkedin.restli.examples.greetings.client;

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
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.AnnotatedComplexKeysResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.annotatedComplexKeys.restspec.json.", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AnnotatedComplexKeysBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "annotatedComplexKeys";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.BATCH_GET, ResourceMethod.CREATE, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.BATCH_UPDATE, ResourceMethod.BATCH_PARTIAL_UPDATE, ResourceMethod.BATCH_DELETE), requestMetadataMap, responseMetadataMap, ComplexResourceKey.class, TwoPartKey.class, TwoPartKey.class, Message.class, keyParts);
    }

    public AnnotatedComplexKeysBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AnnotatedComplexKeysBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public AnnotatedComplexKeysBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public AnnotatedComplexKeysBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public AnnotatedComplexKeysBatchGetBuilder batchGet() {
        return new AnnotatedComplexKeysBatchGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysBatchUpdateBuilder batchUpdate() {
        return new AnnotatedComplexKeysBatchUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysBatchDeleteBuilder batchDelete() {
        return new AnnotatedComplexKeysBatchDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysCreateBuilder create() {
        return new AnnotatedComplexKeysCreateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysGetBuilder get() {
        return new AnnotatedComplexKeysGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysBatchPartialUpdateBuilder batchPartialUpdate() {
        return new AnnotatedComplexKeysBatchPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public AnnotatedComplexKeysPartialUpdateBuilder partialUpdate() {
        return new AnnotatedComplexKeysPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * <b>Test Method: For integration tests only.</b>
     * Example javadoc
     * 
     * @return
     *     builder for the resource method
     */
    public AnnotatedComplexKeysFindByPrefixBuilder findByPrefix() {
        return new AnnotatedComplexKeysFindByPrefixBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

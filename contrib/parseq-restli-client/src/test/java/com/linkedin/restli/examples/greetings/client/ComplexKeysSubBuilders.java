
package com.linkedin.restli.examples.greetings.client;

import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.ComplexKeysSubResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.complexKeys.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexKeysSubBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "complexKeys/{keys}/complexKeysSub";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, java.lang.String.class, null, null, TwoPartKey.class, keyParts);
    }

    public ComplexKeysSubBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysSubBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public ComplexKeysSubBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ComplexKeysSubBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH.replaceFirst("[^/]*/", (primaryResourceName +"/"));
        _requestOptions = assignRequestOptions(requestOptions);
    }

    private java.lang.String getBaseUriTemplate() {
        return _baseUriTemplate;
    }

    public RestliRequestOptions getRequestOptions() {
        return _requestOptions;
    }

    public java.lang.String[] getPathComponents() {
        return URIParamUtils.extractPathComponentsFromUriTemplate(_baseUriTemplate);
    }

    private static RestliRequestOptions assignRequestOptions(RestliRequestOptions requestOptions) {
        if (requestOptions == null) {
            return RestliRequestOptions.DEFAULT_OPTIONS;
        } else {
            return requestOptions;
        }
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ComplexKeysSubGetBuilder get() {
        return new ComplexKeysSubGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

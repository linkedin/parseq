
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
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Resource methods to apply a mixture of automatic/manual projection for root object entities as well as the custom
 *  metadata entity returned in a CollectionResult.
 *  Note that we intentionally pass in MaskTrees for root object projection, custom metadata projection and paging
 *  projection to verify RestliAnnotationReader's ability to properly construct the correct arguments when
 *  reflectively calling resource methods.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.CustomMetadataProjectionResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customMetadataProjections.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomMetadataProjectionsBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "customMetadataProjections";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public CustomMetadataProjectionsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomMetadataProjectionsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public CustomMetadataProjectionsBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomMetadataProjectionsBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    /**
     * This resource method is a variant of the rootAutomaticMetadataManual finder above, except it uses GET_ALL.
     *  This test is to make sure that GET_ALL observes the same code path in restli as FINDER does for projection.
     *  Redundant comments excluded for the sake of brevity.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsGetAllBuilder getAll() {
        return new CustomMetadataProjectionsGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the root object entities and also the custom metadata.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticBuilder findByRootAutomaticMetadataAutomatic() {
        return new CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the root object entities and automatic on the metadata
     *  as well. The caveat here is that the metadata returned by the resource method is null. We want to make sure
     *  restli doesn't fall over when it sees the null later on.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullBuilder findByRootAutomaticMetadataAutomaticNull() {
        return new CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the root object entities and manual projection for the
     *  custom metadata.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootAutomaticMetadataManualBuilder findByRootAutomaticMetadataManual() {
        return new CustomMetadataProjectionsFindByRootAutomaticMetadataManualBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs manual projection for the root object entities and automatic projection for the
     *  custom metadata.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootManualMetadataAutomaticBuilder findByRootManualMetadataAutomatic() {
        return new CustomMetadataProjectionsFindByRootManualMetadataAutomaticBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs manual projection for the root object entities and manual projection for the
     *  custom metadata. Comments excluded since its combining behavior from the previous tests.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootManualMetadataManualBuilder findByRootManualMetadataManual() {
        return new CustomMetadataProjectionsFindByRootManualMetadataManualBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

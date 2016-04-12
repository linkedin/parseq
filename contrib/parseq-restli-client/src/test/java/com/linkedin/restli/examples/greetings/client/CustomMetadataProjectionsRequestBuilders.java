
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
public class CustomMetadataProjectionsRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "customMetadataProjections";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public CustomMetadataProjectionsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomMetadataProjectionsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public CustomMetadataProjectionsRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomMetadataProjectionsRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
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
    public CustomMetadataProjectionsGetAllRequestBuilder getAll() {
        return new CustomMetadataProjectionsGetAllRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the root object entities and also the custom metadata.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticRequestBuilder findByRootAutomaticMetadataAutomatic() {
        return new CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the root object entities and automatic on the metadata
     *  as well. The caveat here is that the metadata returned by the resource method is null. We want to make sure
     *  restli doesn't fall over when it sees the null later on.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullRequestBuilder findByRootAutomaticMetadataAutomaticNull() {
        return new CustomMetadataProjectionsFindByRootAutomaticMetadataAutomaticNullRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the root object entities and manual projection for the
     *  custom metadata.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootAutomaticMetadataManualRequestBuilder findByRootAutomaticMetadataManual() {
        return new CustomMetadataProjectionsFindByRootAutomaticMetadataManualRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs manual projection for the root object entities and automatic projection for the
     *  custom metadata.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootManualMetadataAutomaticRequestBuilder findByRootManualMetadataAutomatic() {
        return new CustomMetadataProjectionsFindByRootManualMetadataAutomaticRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs manual projection for the root object entities and manual projection for the
     *  custom metadata. Comments excluded since its combining behavior from the previous tests.
     * 
     * @return
     *     builder for the resource method
     */
    public CustomMetadataProjectionsFindByRootManualMetadataManualRequestBuilder findByRootManualMetadataManual() {
        return new CustomMetadataProjectionsFindByRootManualMetadataManualRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

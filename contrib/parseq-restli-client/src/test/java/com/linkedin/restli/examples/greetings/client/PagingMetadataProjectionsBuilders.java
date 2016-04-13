
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
 * Resource methods for automatic projection for paging in addition to a mixture of automatic/manual projection for
 *  custom metadata.
 *  Note that we intentionally pass in MaskTrees for root object entity projection, custom metadata projection and paging
 *  projection to verify RestliAnnotationReader's ability to properly construct the correct arguments when
 *  reflectively calling resource methods.
 *  Also note that resource methods cannot project paging (CollectionMetadata) with the exception of
 *  intentionally setting total to NULL when constructing CollectionResult.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.PagingProjectionResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.pagingMetadataProjections.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class PagingMetadataProjectionsBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "pagingMetadataProjections";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET_ALL), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public PagingMetadataProjectionsBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public PagingMetadataProjectionsBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public PagingMetadataProjectionsBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public PagingMetadataProjectionsBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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
     * Same as the test above except that this test is to make sure that GET_ALL observes the same code path in
     *  restli as FINDER does for custom metadata and paging projection.
     *  Redundant comments excluded for the sake of brevity.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsGetAllBuilder getAll() {
        return new PagingMetadataProjectionsGetAllBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the custom metadata and automatic projection
     *  for paging. This particular resource method also varies on what it sets total to.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullBuilder findByMetadataAutomaticPagingAutomaticPartialNull() {
        return new PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the custom metadata and automatic projection
     *  for paging. This particular resource method also varies on what it sets total to.
     *  The caveat with this test is that it incorrectly assigns a non null value for the total
     *  even though the MaskTree says to exclude it.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullIncorrectBuilder findByMetadataAutomaticPagingAutomaticPartialNullIncorrect() {
        return new PagingMetadataProjectionsFindByMetadataAutomaticPagingAutomaticPartialNullIncorrectBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs automatic projection for the custom metadata and complete automatic projection
     *  for paging. This means that it will provide a total in its construction of CollectionResult.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsFindByMetadataAutomaticPagingFullyAutomaticBuilder findByMetadataAutomaticPagingFullyAutomatic() {
        return new PagingMetadataProjectionsFindByMetadataAutomaticPagingFullyAutomaticBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs manual projection for the custom metadata and automatic projection
     *  for paging. This particular resource method also varies on what it sets total to.
     *  Comments excluded since its combining behavior from the previous tests.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsFindByMetadataManualPagingAutomaticPartialNullBuilder findByMetadataManualPagingAutomaticPartialNull() {
        return new PagingMetadataProjectionsFindByMetadataManualPagingAutomaticPartialNullBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method performs manual projection for the custom metadata and automatic projection
     *  for Paging.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsFindByMetadataManualPagingFullyAutomaticBuilder findByMetadataManualPagingFullyAutomatic() {
        return new PagingMetadataProjectionsFindByMetadataManualPagingFullyAutomaticBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * This resource method is used to create additional paging metadata for fields such as links. Client side
     *  tests can use this method to potentially project on fields inside of links.
     * 
     * @return
     *     builder for the resource method
     */
    public PagingMetadataProjectionsFindBySearchWithLinksResultBuilder findBySearchWithLinksResult() {
        return new PagingMetadataProjectionsFindBySearchWithLinksResultBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

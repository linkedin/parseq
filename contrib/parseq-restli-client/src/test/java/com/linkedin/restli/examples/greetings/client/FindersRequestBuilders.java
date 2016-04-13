
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
 * generated from: com.linkedin.restli.examples.greetings.server.FindersResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.finders.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class FindersRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "finders";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.noneOf(ResourceMethod.class), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public FindersRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public FindersRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public FindersRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public FindersRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public FindersFindByBasicSearchRequestBuilder findByBasicSearch() {
        return new FindersFindByBasicSearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public FindersFindByPredefinedSearchRequestBuilder findByPredefinedSearch() {
        return new FindersFindByPredefinedSearchRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public FindersFindBySearchWithMetadataRequestBuilder findBySearchWithMetadata() {
        return new FindersFindBySearchWithMetadataRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public FindersFindBySearchWithoutMetadataRequestBuilder findBySearchWithoutMetadata() {
        return new FindersFindBySearchWithoutMetadataRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

}

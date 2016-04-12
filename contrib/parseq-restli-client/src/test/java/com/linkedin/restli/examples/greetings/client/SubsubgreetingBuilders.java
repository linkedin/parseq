
package com.linkedin.restli.examples.greetings.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * This resource represents a simple sub-resource.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.SimpleResourceUnderCollectionResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greeting.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class SubsubgreetingBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "greeting/subgreetings/{subgreetingsId}/subsubgreeting";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> exampleActionParams = new ArrayList<FieldDef<?>>();
        exampleActionParams.add(new FieldDef<Integer>("param1", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        requestMetadataMap.put("exampleAction", new DynamicRecordMetadata("exampleAction", exampleActionParams));
        ArrayList<FieldDef<?>> exceptionTestParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("exceptionTest", new DynamicRecordMetadata("exceptionTest", exceptionTestParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("exampleAction", new DynamicRecordMetadata("exampleAction", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("exceptionTest", new DynamicRecordMetadata("exceptionTest", Collections.<FieldDef<?>>emptyList()));
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET, ResourceMethod.PARTIAL_UPDATE, ResourceMethod.UPDATE, ResourceMethod.DELETE), requestMetadataMap, responseMetadataMap, Greeting.class);
    }

    public SubsubgreetingBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public SubsubgreetingBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public SubsubgreetingBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public SubsubgreetingBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH.replaceFirst("[^/]*/", (primaryResourceName +"/"));
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
     * Deletes the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public SubsubgreetingDeleteBuilder delete() {
        return new SubsubgreetingDeleteBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Updates the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public SubsubgreetingUpdateBuilder update() {
        return new SubsubgreetingUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Gets the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public SubsubgreetingGetBuilder get() {
        return new SubsubgreetingGetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Updates the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public SubsubgreetingPartialUpdateBuilder partialUpdate() {
        return new SubsubgreetingPartialUpdateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * An example action on the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public SubsubgreetingDoExampleActionBuilder actionExampleAction() {
        return new SubsubgreetingDoExampleActionBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    /**
     * An example action throwing an exception.
     * 
     * @return
     *     builder for the resource method
     */
    public SubsubgreetingDoExceptionTestBuilder actionExceptionTest() {
        return new SubsubgreetingDoExceptionTestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}

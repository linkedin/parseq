
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
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Greeting;


/**
 * This resource represents a simple root resource.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.RootSimpleResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greeting.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class GreetingRequestBuilders
    extends BuilderBase
{

    private final static String ORIGINAL_RESOURCE_PATH = "greeting";
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

    public GreetingRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public GreetingRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public GreetingRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
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
    public GreetingDeleteRequestBuilder delete() {
        return new GreetingDeleteRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Updates the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public GreetingUpdateRequestBuilder update() {
        return new GreetingUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Gets the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public GreetingGetRequestBuilder get() {
        return new GreetingGetRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Updates the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public GreetingPartialUpdateRequestBuilder partialUpdate() {
        return new GreetingPartialUpdateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * An example action on the greeting.
     * 
     * @return
     *     builder for the resource method
     */
    public GreetingDoExampleActionRequestBuilder actionExampleAction() {
        return new GreetingDoExampleActionRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    /**
     * An example action throwing an exception.
     * 
     * @return
     *     builder for the resource method
     */
    public GreetingDoExceptionTestRequestBuilder actionExceptionTest() {
        return new GreetingDoExceptionTestRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}

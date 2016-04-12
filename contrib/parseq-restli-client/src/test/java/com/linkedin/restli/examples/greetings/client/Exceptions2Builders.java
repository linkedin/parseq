
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
 * generated from: com.linkedin.restli.examples.greetings.server.ExceptionsResource2
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.exceptions2.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class Exceptions2Builders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "exceptions2";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> exceptionWithValueParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("exceptionWithValue", new DynamicRecordMetadata("exceptionWithValue", exceptionWithValueParams));
        ArrayList<FieldDef<?>> exceptionWithoutValueParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("exceptionWithoutValue", new DynamicRecordMetadata("exceptionWithoutValue", exceptionWithoutValueParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("exceptionWithValue", new DynamicRecordMetadata("exceptionWithValue", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("exceptionWithoutValue", new DynamicRecordMetadata("exceptionWithoutValue", Collections.<FieldDef<?>>emptyList()));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.of(ResourceMethod.GET), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public Exceptions2Builders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public Exceptions2Builders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public Exceptions2Builders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public Exceptions2Builders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public Exceptions2GetBuilder get() {
        return new Exceptions2GetBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    /**
     * Action that responds HTTP 500 with integer value
     * 
     * @return
     *     builder for the resource method
     */
    public Exceptions2DoExceptionWithValueBuilder actionExceptionWithValue() {
        return new Exceptions2DoExceptionWithValueBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that responds HTTP 500 without value
     * 
     * @return
     *     builder for the resource method
     */
    public Exceptions2DoExceptionWithoutValueBuilder actionExceptionWithoutValue() {
        return new Exceptions2DoExceptionWithoutValueBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

}

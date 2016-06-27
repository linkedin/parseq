
package com.linkedin.restli.examples.greetings.client;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.linkedin.restli.examples.custom.types.CustomLong;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CalendarRef;
import com.linkedin.restli.examples.typeref.api.CustomLongRef;
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.CustomTypesResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesBuilders {

    private final String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
    private final static String ORIGINAL_RESOURCE_PATH = "customTypes";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<String, DynamicRecordMetadata> requestMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> actionParams = new ArrayList<FieldDef<?>>();
        actionParams.add(new FieldDef<CustomLong>("l", CustomLong.class, DataTemplateUtil.getSchema(CustomLongRef.class)));
        requestMetadataMap.put("action", new DynamicRecordMetadata("action", actionParams));
        ArrayList<FieldDef<?>> arrayActionParams = new ArrayList<FieldDef<?>>();
        arrayActionParams.add(new FieldDef<com.linkedin.restli.examples.typeref.api.CustomLongRefArray>("ls", com.linkedin.restli.examples.typeref.api.CustomLongRefArray.class, DataTemplateUtil.getSchema(com.linkedin.restli.examples.typeref.api.CustomLongRefArray.class)));
        requestMetadataMap.put("arrayAction", new DynamicRecordMetadata("arrayAction", arrayActionParams));
        ArrayList<FieldDef<?>> calendarActionParams = new ArrayList<FieldDef<?>>();
        calendarActionParams.add(new FieldDef<Calendar>("calendar", Calendar.class, DataTemplateUtil.getSchema(CalendarRef.class)));
        requestMetadataMap.put("calendarAction", new DynamicRecordMetadata("calendarAction", calendarActionParams));
        HashMap<String, DynamicRecordMetadata> responseMetadataMap = new HashMap<String, DynamicRecordMetadata>();
        responseMetadataMap.put("action", new DynamicRecordMetadata("action", Collections.singletonList(new FieldDef<Long>("value", Long.class, DataTemplateUtil.getSchema(Long.class)))));
        responseMetadataMap.put("arrayAction", new DynamicRecordMetadata("arrayAction", Collections.singletonList(new FieldDef<com.linkedin.restli.examples.typeref.api.CustomLongRefArray>("value", com.linkedin.restli.examples.typeref.api.CustomLongRefArray.class, DataTemplateUtil.getSchema(com.linkedin.restli.examples.typeref.api.CustomLongRefArray.class)))));
        responseMetadataMap.put("calendarAction", new DynamicRecordMetadata("calendarAction", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.noneOf(ResourceMethod.class), requestMetadataMap, responseMetadataMap, Long.class, null, null, Greeting.class, keyParts);
    }

    public CustomTypesBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypesBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public CustomTypesBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypesBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
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

    public CustomTypesFindByCalendarBuilder findByCalendar() {
        return new CustomTypesFindByCalendarBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByCalendarsBuilder findByCalendars() {
        return new CustomTypesFindByCalendarsBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByCustomLongBuilder findByCustomLong() {
        return new CustomTypesFindByCustomLongBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByCustomLongArrayBuilder findByCustomLongArray() {
        return new CustomTypesFindByCustomLongArrayBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByDateBuilder findByDate() {
        return new CustomTypesFindByDateBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByIpBuilder findByIp() {
        return new CustomTypesFindByIpBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesDoActionBuilder actionAction() {
        return new CustomTypesDoActionBuilder(getBaseUriTemplate(), Long.class, _resourceSpec, getRequestOptions());
    }

    public CustomTypesDoArrayActionBuilder actionArrayAction() {
        return new CustomTypesDoArrayActionBuilder(getBaseUriTemplate(), com.linkedin.restli.examples.typeref.api.CustomLongRefArray.class, _resourceSpec, getRequestOptions());
    }

    public CustomTypesDoCalendarActionBuilder actionCalendarAction() {
        return new CustomTypesDoCalendarActionBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

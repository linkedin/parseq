
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
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.custom.types.CustomLong;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CalendarRef;
import com.linkedin.restli.examples.typeref.api.CustomLongRef;


/**
 * generated from: com.linkedin.restli.examples.greetings.server.CustomTypesResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesRequestBuilders
    extends BuilderBase
{

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

    public CustomTypesRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypesRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public CustomTypesRequestBuilders(String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public CustomTypesRequestBuilders(String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public CustomTypesFindByCalendarRequestBuilder findByCalendar() {
        return new CustomTypesFindByCalendarRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByCalendarsRequestBuilder findByCalendars() {
        return new CustomTypesFindByCalendarsRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByCustomLongRequestBuilder findByCustomLong() {
        return new CustomTypesFindByCustomLongRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByCustomLongArrayRequestBuilder findByCustomLongArray() {
        return new CustomTypesFindByCustomLongArrayRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByDateRequestBuilder findByDate() {
        return new CustomTypesFindByDateRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesFindByIpRequestBuilder findByIp() {
        return new CustomTypesFindByIpRequestBuilder(getBaseUriTemplate(), _resourceSpec, getRequestOptions());
    }

    public CustomTypesDoActionRequestBuilder actionAction() {
        return new CustomTypesDoActionRequestBuilder(getBaseUriTemplate(), Long.class, _resourceSpec, getRequestOptions());
    }

    public CustomTypesDoArrayActionRequestBuilder actionArrayAction() {
        return new CustomTypesDoArrayActionRequestBuilder(getBaseUriTemplate(), com.linkedin.restli.examples.typeref.api.CustomLongRefArray.class, _resourceSpec, getRequestOptions());
    }

    public CustomTypesDoCalendarActionRequestBuilder actionCalendarAction() {
        return new CustomTypesDoCalendarActionRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

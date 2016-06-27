
package com.linkedin.restli.examples.greetings.client;

import java.util.Calendar;
import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesDoCalendarActionBuilder
    extends ActionRequestBuilderBase<Void, Integer, CustomTypesDoCalendarActionBuilder>
{


    public CustomTypesDoCalendarActionBuilder(String baseUriTemplate, Class<Integer> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("calendarAction");
    }

    public CustomTypesDoCalendarActionBuilder paramCalendar(Calendar value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("calendarAction").getFieldDef("calendar"), value);
        return this;
    }

}

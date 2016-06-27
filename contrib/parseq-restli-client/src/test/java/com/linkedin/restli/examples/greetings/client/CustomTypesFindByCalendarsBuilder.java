
package com.linkedin.restli.examples.greetings.client;

import java.util.Calendar;
import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.typeref.api.CalendarRefArray;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesFindByCalendarsBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomTypesFindByCalendarsBuilder>
{


    public CustomTypesFindByCalendarsBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("calendars");
    }

    public CustomTypesFindByCalendarsBuilder calendarsParam(CalendarRefArray value) {
        super.setReqParam("calendars", value, CalendarRefArray.class);
        return this;
    }

    public CustomTypesFindByCalendarsBuilder calendarsParam(Iterable<Calendar> value) {
        super.setReqParam("calendars", value, Calendar.class);
        return this;
    }

    public CustomTypesFindByCalendarsBuilder addCalendarsParam(Calendar value) {
        super.addReqParam("calendars", value, Calendar.class);
        return this;
    }

}

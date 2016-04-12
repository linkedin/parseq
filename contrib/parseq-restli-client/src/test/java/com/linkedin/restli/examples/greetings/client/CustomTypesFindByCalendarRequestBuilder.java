
package com.linkedin.restli.examples.greetings.client;

import java.util.Calendar;
import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomTypesFindByCalendarRequestBuilder
    extends FindRequestBuilderBase<Long, Greeting, CustomTypesFindByCalendarRequestBuilder>
{


    public CustomTypesFindByCalendarRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("calendar");
    }

    public CustomTypesFindByCalendarRequestBuilder calendarParam(Calendar value) {
        super.setReqParam("calendar", value, Calendar.class);
        return this;
    }

}

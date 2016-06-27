
package com.linkedin.restli.examples.typeref.api;

import java.util.Collection;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DirectArrayTemplate;
import com.linkedin.restli.examples.custom.types.CalendarCoercer;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CalendarRefArray
    extends DirectArrayTemplate<java.util.Calendar>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"typeref\",\"name\":\"CalendarRef\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"ref\":\"int\",\"java\":{\"coercerClass\":\"com.linkedin.restli.examples.custom.types.CalendarCoercer\",\"class\":\"java.util.Calendar\"}}}"));

    static {
        Custom.initializeCustomClass(java.util.Calendar.class);
        Custom.initializeCoercerClass(CalendarCoercer.class);
    }

    public CalendarRefArray() {
        this(new DataList());
    }

    public CalendarRefArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public CalendarRefArray(Collection<java.util.Calendar> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public CalendarRefArray(DataList data) {
        super(data, SCHEMA, java.util.Calendar.class, Integer.class);
    }

    @Override
    public CalendarRefArray clone()
        throws CloneNotSupportedException
    {
        return ((CalendarRefArray) super.clone());
    }

    @Override
    public CalendarRefArray copy()
        throws CloneNotSupportedException
    {
        return ((CalendarRefArray) super.copy());
    }

}

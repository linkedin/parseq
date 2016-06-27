
package com.linkedin.restli.examples.typeref.api;

import java.util.Collection;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DirectArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.customTypes.restspec.json.", date = "Thu Mar 31 14:16:21 PDT 2016")
public class CustomLongRefArray
    extends DirectArrayTemplate<com.linkedin.restli.examples.custom.types.CustomLong>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"typeref\",\"name\":\"CustomLongRef\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"ref\":\"long\",\"java\":{\"class\":\"com.linkedin.restli.examples.custom.types.CustomLong\"}}}"));

    static {
        Custom.initializeCustomClass(com.linkedin.restli.examples.custom.types.CustomLong.class);
    }

    public CustomLongRefArray() {
        this(new DataList());
    }

    public CustomLongRefArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public CustomLongRefArray(Collection<com.linkedin.restli.examples.custom.types.CustomLong> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public CustomLongRefArray(DataList data) {
        super(data, SCHEMA, com.linkedin.restli.examples.custom.types.CustomLong.class, Long.class);
    }

    @Override
    public CustomLongRefArray clone()
        throws CloneNotSupportedException
    {
        return ((CustomLongRefArray) super.clone());
    }

    @Override
    public CustomLongRefArray copy()
        throws CloneNotSupportedException
    {
        return ((CustomLongRefArray) super.copy());
    }

}

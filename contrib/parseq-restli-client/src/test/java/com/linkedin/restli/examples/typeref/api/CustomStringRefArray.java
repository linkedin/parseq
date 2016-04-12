
package com.linkedin.restli.examples.typeref.api;

import java.util.Collection;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.template.Custom;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DirectArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.typerefCustomDoubleAssociationKeyResource.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class CustomStringRefArray
    extends DirectArrayTemplate<com.linkedin.restli.examples.custom.types.CustomString>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"typeref\",\"name\":\"CustomStringRef\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"ref\":\"string\",\"java\":{\"class\":\"com.linkedin.restli.examples.custom.types.CustomString\"}}}"));

    static {
        Custom.initializeCustomClass(com.linkedin.restli.examples.custom.types.CustomString.class);
    }

    public CustomStringRefArray() {
        this(new DataList());
    }

    public CustomStringRefArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public CustomStringRefArray(Collection<com.linkedin.restli.examples.custom.types.CustomString> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public CustomStringRefArray(DataList data) {
        super(data, SCHEMA, com.linkedin.restli.examples.custom.types.CustomString.class, String.class);
    }

    @Override
    public CustomStringRefArray clone()
        throws CloneNotSupportedException
    {
        return ((CustomStringRefArray) super.clone());
    }

    @Override
    public CustomStringRefArray copy()
        throws CloneNotSupportedException
    {
        return ((CustomStringRefArray) super.copy());
    }

}

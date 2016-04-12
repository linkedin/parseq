
package com.linkedin.restli.examples.greetings.api;

import java.util.Collection;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DirectArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.greetingsTask.restspec.json.", date = "Thu Mar 31 14:16:23 PDT 2016")
public class ToneArray
    extends DirectArrayTemplate<Tone>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"enum\",\"name\":\"Tone\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]}}"));

    public ToneArray() {
        this(new DataList());
    }

    public ToneArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public ToneArray(Collection<Tone> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public ToneArray(DataList data) {
        super(data, SCHEMA, Tone.class, String.class);
    }

    @Override
    public ToneArray clone()
        throws CloneNotSupportedException
    {
        return ((ToneArray) super.clone());
    }

    @Override
    public ToneArray copy()
        throws CloneNotSupportedException
    {
        return ((ToneArray) super.copy());
    }

}

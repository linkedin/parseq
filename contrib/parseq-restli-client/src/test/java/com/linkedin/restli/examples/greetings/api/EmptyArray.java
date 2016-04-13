
package com.linkedin.restli.examples.greetings.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ArrayOfEmptys.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class EmptyArray
    extends WrappingArrayTemplate<Empty>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Empty\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[]}}"));

    public EmptyArray() {
        this(new DataList());
    }

    public EmptyArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public EmptyArray(Collection<Empty> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public EmptyArray(DataList data) {
        super(data, SCHEMA, Empty.class);
    }

    @Override
    public EmptyArray clone()
        throws CloneNotSupportedException
    {
        return ((EmptyArray) super.clone());
    }

    @Override
    public EmptyArray copy()
        throws CloneNotSupportedException
    {
        return ((EmptyArray) super.copy());
    }

    public static class Fields
        extends PathSpec
    {


        public Fields(List<String> path, String name) {
            super(path, name);
        }

        public Fields() {
            super();
        }

        public com.linkedin.restli.examples.greetings.api.Empty.Fields items() {
            return new com.linkedin.restli.examples.greetings.api.Empty.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

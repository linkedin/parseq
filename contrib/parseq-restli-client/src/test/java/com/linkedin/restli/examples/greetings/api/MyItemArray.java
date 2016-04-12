
package com.linkedin.restli.examples.greetings.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ValidationDemo.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class MyItemArray
    extends WrappingArrayTemplate<myItem>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"myItem\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"bar1\",\"type\":\"string\"},{\"name\":\"bar2\",\"type\":\"string\"}]}}"));

    public MyItemArray() {
        this(new DataList());
    }

    public MyItemArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public MyItemArray(Collection<myItem> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public MyItemArray(DataList data) {
        super(data, SCHEMA, myItem.class);
    }

    @Override
    public MyItemArray clone()
        throws CloneNotSupportedException
    {
        return ((MyItemArray) super.clone());
    }

    @Override
    public MyItemArray copy()
        throws CloneNotSupportedException
    {
        return ((MyItemArray) super.copy());
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

        public com.linkedin.restli.examples.greetings.api.myItem.Fields items() {
            return new com.linkedin.restli.examples.greetings.api.myItem.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

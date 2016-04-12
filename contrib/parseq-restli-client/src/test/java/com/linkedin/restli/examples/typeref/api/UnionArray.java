
package com.linkedin.restli.examples.typeref.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groups.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class UnionArray
    extends WrappingArrayTemplate<Union>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":[\"int\",\"string\"]}"));

    public UnionArray() {
        this(new DataList());
    }

    public UnionArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public UnionArray(Collection<Union> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public UnionArray(DataList data) {
        super(data, SCHEMA, Union.class);
    }

    @Override
    public UnionArray clone()
        throws CloneNotSupportedException
    {
        return ((UnionArray) super.clone());
    }

    @Override
    public UnionArray copy()
        throws CloneNotSupportedException
    {
        return ((UnionArray) super.copy());
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

        public com.linkedin.restli.examples.typeref.api.Union.Fields items() {
            return new com.linkedin.restli.examples.typeref.api.Union.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}


package com.linkedin.restli.examples.greetings.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.actions.restspec.json.", date = "Thu Mar 31 14:16:19 PDT 2016")
public class MessageArray
    extends WrappingArrayTemplate<Message>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Message\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"doc\":\"A message\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]},\"doc\":\"tone\"}]}}"));

    public MessageArray() {
        this(new DataList());
    }

    public MessageArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public MessageArray(Collection<Message> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public MessageArray(DataList data) {
        super(data, SCHEMA, Message.class);
    }

    @Override
    public MessageArray clone()
        throws CloneNotSupportedException
    {
        return ((MessageArray) super.clone());
    }

    @Override
    public MessageArray copy()
        throws CloneNotSupportedException
    {
        return ((MessageArray) super.copy());
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

        public com.linkedin.restli.examples.greetings.api.Message.Fields items() {
            return new com.linkedin.restli.examples.greetings.api.Message.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

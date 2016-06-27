
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.MapDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingMapTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ValidationDemo.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GreetingMap
    extends WrappingMapTemplate<Greeting>
{

    private final static MapDataSchema SCHEMA = ((MapDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"map\",\"values\":{\"type\":\"record\",\"name\":\"Greeting\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"doc\":\"A greeting\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]},\"doc\":\"tone\"}]}}"));

    public GreetingMap() {
        this(new DataMap());
    }

    public GreetingMap(int initialCapacity) {
        this(new DataMap(initialCapacity));
    }

    public GreetingMap(int initialCapacity, float loadFactor) {
        this(new DataMap(initialCapacity, loadFactor));
    }

    public GreetingMap(Map<String, Greeting> m) {
        this(newDataMapOfSize(m.size()));
        putAll(m);
    }

    public GreetingMap(DataMap data) {
        super(data, SCHEMA, Greeting.class);
    }

    @Override
    public GreetingMap clone()
        throws CloneNotSupportedException
    {
        return ((GreetingMap) super.clone());
    }

    @Override
    public GreetingMap copy()
        throws CloneNotSupportedException
    {
        return ((GreetingMap) super.copy());
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

        public com.linkedin.restli.examples.greetings.api.Greeting.Fields values() {
            return new com.linkedin.restli.examples.greetings.api.Greeting.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

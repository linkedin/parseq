
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.MapDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingMapTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/MapOfEmptys.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class EmptyMap
    extends WrappingMapTemplate<Empty>
{

    private final static MapDataSchema SCHEMA = ((MapDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"map\",\"values\":{\"type\":\"record\",\"name\":\"Empty\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[]}}"));

    public EmptyMap() {
        this(new DataMap());
    }

    public EmptyMap(int initialCapacity) {
        this(new DataMap(initialCapacity));
    }

    public EmptyMap(int initialCapacity, float loadFactor) {
        this(new DataMap(initialCapacity, loadFactor));
    }

    public EmptyMap(Map<String, Empty> m) {
        this(newDataMapOfSize(m.size()));
        putAll(m);
    }

    public EmptyMap(DataMap data) {
        super(data, SCHEMA, Empty.class);
    }

    @Override
    public EmptyMap clone()
        throws CloneNotSupportedException
    {
        return ((EmptyMap) super.clone());
    }

    @Override
    public EmptyMap copy()
        throws CloneNotSupportedException
    {
        return ((EmptyMap) super.copy());
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

        public com.linkedin.restli.examples.greetings.api.Empty.Fields values() {
            return new com.linkedin.restli.examples.greetings.api.Empty.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

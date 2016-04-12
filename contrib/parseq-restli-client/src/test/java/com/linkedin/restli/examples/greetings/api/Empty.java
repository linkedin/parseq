
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.RecordTemplate;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/Empty.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Empty
    extends RecordTemplate
{

    private final static Empty.Fields _fields = new Empty.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"Empty\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[]}"));

    public Empty() {
        super(new DataMap(), SCHEMA);
    }

    public Empty(DataMap data) {
        super(data, SCHEMA);
    }

    public static Empty.Fields fields() {
        return _fields;
    }

    @Override
    public Empty clone()
        throws CloneNotSupportedException
    {
        return ((Empty) super.clone());
    }

    @Override
    public Empty copy()
        throws CloneNotSupportedException
    {
        return ((Empty) super.copy());
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

    }

}

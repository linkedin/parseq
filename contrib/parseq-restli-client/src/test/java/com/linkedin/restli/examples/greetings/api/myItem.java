
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.SetMode;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ValidationDemo.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class myItem
    extends RecordTemplate
{

    private final static myItem.Fields _fields = new myItem.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"myItem\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"bar1\",\"type\":\"string\"},{\"name\":\"bar2\",\"type\":\"string\"}]}"));
    private final static RecordDataSchema.Field FIELD_Bar1 = SCHEMA.getField("bar1");
    private final static RecordDataSchema.Field FIELD_Bar2 = SCHEMA.getField("bar2");

    public myItem() {
        super(new DataMap(), SCHEMA);
    }

    public myItem(DataMap data) {
        super(data, SCHEMA);
    }

    public static myItem.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for bar1
     * 
     * @see myItem.Fields#bar1
     */
    public boolean hasBar1() {
        return contains(FIELD_Bar1);
    }

    /**
     * Remover for bar1
     * 
     * @see myItem.Fields#bar1
     */
    public void removeBar1() {
        remove(FIELD_Bar1);
    }

    /**
     * Getter for bar1
     * 
     * @see myItem.Fields#bar1
     */
    public String getBar1(GetMode mode) {
        return obtainDirect(FIELD_Bar1, String.class, mode);
    }

    /**
     * Getter for bar1
     * 
     * @see myItem.Fields#bar1
     */
    public String getBar1() {
        return obtainDirect(FIELD_Bar1, String.class, GetMode.STRICT);
    }

    /**
     * Setter for bar1
     * 
     * @see myItem.Fields#bar1
     */
    public myItem setBar1(String value, SetMode mode) {
        putDirect(FIELD_Bar1, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for bar1
     * 
     * @see myItem.Fields#bar1
     */
    public myItem setBar1(String value) {
        putDirect(FIELD_Bar1, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for bar2
     * 
     * @see myItem.Fields#bar2
     */
    public boolean hasBar2() {
        return contains(FIELD_Bar2);
    }

    /**
     * Remover for bar2
     * 
     * @see myItem.Fields#bar2
     */
    public void removeBar2() {
        remove(FIELD_Bar2);
    }

    /**
     * Getter for bar2
     * 
     * @see myItem.Fields#bar2
     */
    public String getBar2(GetMode mode) {
        return obtainDirect(FIELD_Bar2, String.class, mode);
    }

    /**
     * Getter for bar2
     * 
     * @see myItem.Fields#bar2
     */
    public String getBar2() {
        return obtainDirect(FIELD_Bar2, String.class, GetMode.STRICT);
    }

    /**
     * Setter for bar2
     * 
     * @see myItem.Fields#bar2
     */
    public myItem setBar2(String value, SetMode mode) {
        putDirect(FIELD_Bar2, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for bar2
     * 
     * @see myItem.Fields#bar2
     */
    public myItem setBar2(String value) {
        putDirect(FIELD_Bar2, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public myItem clone()
        throws CloneNotSupportedException
    {
        return ((myItem) super.clone());
    }

    @Override
    public myItem copy()
        throws CloneNotSupportedException
    {
        return ((myItem) super.copy());
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

        public PathSpec bar1() {
            return new PathSpec(getPathComponents(), "bar1");
        }

        public PathSpec bar2() {
            return new PathSpec(getPathComponents(), "bar2");
        }

    }

}

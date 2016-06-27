
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
public class myRecord
    extends RecordTemplate
{

    private final static myRecord.Fields _fields = new myRecord.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"myRecord\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"foo1\",\"type\":\"int\"},{\"name\":\"foo2\",\"type\":\"int\",\"optional\":true}]}"));
    private final static RecordDataSchema.Field FIELD_Foo1 = SCHEMA.getField("foo1");
    private final static RecordDataSchema.Field FIELD_Foo2 = SCHEMA.getField("foo2");

    public myRecord() {
        super(new DataMap(), SCHEMA);
    }

    public myRecord(DataMap data) {
        super(data, SCHEMA);
    }

    public static myRecord.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public boolean hasFoo1() {
        return contains(FIELD_Foo1);
    }

    /**
     * Remover for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public void removeFoo1() {
        remove(FIELD_Foo1);
    }

    /**
     * Getter for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public Integer getFoo1(GetMode mode) {
        return obtainDirect(FIELD_Foo1, Integer.class, mode);
    }

    /**
     * Getter for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public Integer getFoo1() {
        return obtainDirect(FIELD_Foo1, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public myRecord setFoo1(Integer value, SetMode mode) {
        putDirect(FIELD_Foo1, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public myRecord setFoo1(Integer value) {
        putDirect(FIELD_Foo1, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for foo1
     * 
     * @see myRecord.Fields#foo1
     */
    public myRecord setFoo1(int value) {
        putDirect(FIELD_Foo1, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public boolean hasFoo2() {
        return contains(FIELD_Foo2);
    }

    /**
     * Remover for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public void removeFoo2() {
        remove(FIELD_Foo2);
    }

    /**
     * Getter for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public Integer getFoo2(GetMode mode) {
        return obtainDirect(FIELD_Foo2, Integer.class, mode);
    }

    /**
     * Getter for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public Integer getFoo2() {
        return obtainDirect(FIELD_Foo2, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public myRecord setFoo2(Integer value, SetMode mode) {
        putDirect(FIELD_Foo2, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public myRecord setFoo2(Integer value) {
        putDirect(FIELD_Foo2, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for foo2
     * 
     * @see myRecord.Fields#foo2
     */
    public myRecord setFoo2(int value) {
        putDirect(FIELD_Foo2, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public myRecord clone()
        throws CloneNotSupportedException
    {
        return ((myRecord) super.clone());
    }

    @Override
    public myRecord copy()
        throws CloneNotSupportedException
    {
        return ((myRecord) super.copy());
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

        public PathSpec foo1() {
            return new PathSpec(getPathComponents(), "foo1");
        }

        public PathSpec foo2() {
            return new PathSpec(getPathComponents(), "foo2");
        }

    }

}

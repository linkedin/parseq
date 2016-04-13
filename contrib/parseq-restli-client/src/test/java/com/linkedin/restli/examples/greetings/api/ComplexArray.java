
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.LongArray;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.SetMode;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ComplexArray.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class ComplexArray
    extends RecordTemplate
{

    private final static ComplexArray.Fields _fields = new ComplexArray.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"ComplexArray\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"next\",\"type\":\"ComplexArray\",\"optional\":true},{\"name\":\"array\",\"type\":{\"type\":\"array\",\"items\":\"long\"}}]}"));
    private final static RecordDataSchema.Field FIELD_Next = SCHEMA.getField("next");
    private final static RecordDataSchema.Field FIELD_Array = SCHEMA.getField("array");

    public ComplexArray() {
        super(new DataMap(), SCHEMA);
    }

    public ComplexArray(DataMap data) {
        super(data, SCHEMA);
    }

    public static ComplexArray.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for next
     * 
     * @see ComplexArray.Fields#next
     */
    public boolean hasNext() {
        return contains(FIELD_Next);
    }

    /**
     * Remover for next
     * 
     * @see ComplexArray.Fields#next
     */
    public void removeNext() {
        remove(FIELD_Next);
    }

    /**
     * Getter for next
     * 
     * @see ComplexArray.Fields#next
     */
    public ComplexArray getNext(GetMode mode) {
        return obtainWrapped(FIELD_Next, ComplexArray.class, mode);
    }

    /**
     * Getter for next
     * 
     * @see ComplexArray.Fields#next
     */
    public ComplexArray getNext() {
        return obtainWrapped(FIELD_Next, ComplexArray.class, GetMode.STRICT);
    }

    /**
     * Setter for next
     * 
     * @see ComplexArray.Fields#next
     */
    public ComplexArray setNext(ComplexArray value, SetMode mode) {
        putWrapped(FIELD_Next, ComplexArray.class, value, mode);
        return this;
    }

    /**
     * Setter for next
     * 
     * @see ComplexArray.Fields#next
     */
    public ComplexArray setNext(ComplexArray value) {
        putWrapped(FIELD_Next, ComplexArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for array
     * 
     * @see ComplexArray.Fields#array
     */
    public boolean hasArray() {
        return contains(FIELD_Array);
    }

    /**
     * Remover for array
     * 
     * @see ComplexArray.Fields#array
     */
    public void removeArray() {
        remove(FIELD_Array);
    }

    /**
     * Getter for array
     * 
     * @see ComplexArray.Fields#array
     */
    public LongArray getArray(GetMode mode) {
        return obtainWrapped(FIELD_Array, LongArray.class, mode);
    }

    /**
     * Getter for array
     * 
     * @see ComplexArray.Fields#array
     */
    public LongArray getArray() {
        return obtainWrapped(FIELD_Array, LongArray.class, GetMode.STRICT);
    }

    /**
     * Setter for array
     * 
     * @see ComplexArray.Fields#array
     */
    public ComplexArray setArray(LongArray value, SetMode mode) {
        putWrapped(FIELD_Array, LongArray.class, value, mode);
        return this;
    }

    /**
     * Setter for array
     * 
     * @see ComplexArray.Fields#array
     */
    public ComplexArray setArray(LongArray value) {
        putWrapped(FIELD_Array, LongArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public ComplexArray clone()
        throws CloneNotSupportedException
    {
        return ((ComplexArray) super.clone());
    }

    @Override
    public ComplexArray copy()
        throws CloneNotSupportedException
    {
        return ((ComplexArray) super.copy());
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

        public com.linkedin.restli.examples.greetings.api.ComplexArray.Fields next() {
            return new com.linkedin.restli.examples.greetings.api.ComplexArray.Fields(getPathComponents(), "next");
        }

        public PathSpec array() {
            return new PathSpec(getPathComponents(), "array");
        }

    }

}

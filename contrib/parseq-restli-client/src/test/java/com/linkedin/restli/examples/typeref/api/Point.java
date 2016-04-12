
package com.linkedin.restli.examples.typeref.api;

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
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/typeref/api/Point.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Point
    extends RecordTemplate
{

    private final static Point.Fields _fields = new Point.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"Point\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"fields\":[{\"name\":\"x\",\"type\":\"double\"},{\"name\":\"y\",\"type\":\"double\"}]}"));
    private final static RecordDataSchema.Field FIELD_X = SCHEMA.getField("x");
    private final static RecordDataSchema.Field FIELD_Y = SCHEMA.getField("y");

    public Point() {
        super(new DataMap(), SCHEMA);
    }

    public Point(DataMap data) {
        super(data, SCHEMA);
    }

    public static Point.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for x
     * 
     * @see Point.Fields#x
     */
    public boolean hasX() {
        return contains(FIELD_X);
    }

    /**
     * Remover for x
     * 
     * @see Point.Fields#x
     */
    public void removeX() {
        remove(FIELD_X);
    }

    /**
     * Getter for x
     * 
     * @see Point.Fields#x
     */
    public Double getX(GetMode mode) {
        return obtainDirect(FIELD_X, Double.class, mode);
    }

    /**
     * Getter for x
     * 
     * @see Point.Fields#x
     */
    public Double getX() {
        return obtainDirect(FIELD_X, Double.class, GetMode.STRICT);
    }

    /**
     * Setter for x
     * 
     * @see Point.Fields#x
     */
    public Point setX(Double value, SetMode mode) {
        putDirect(FIELD_X, Double.class, Double.class, value, mode);
        return this;
    }

    /**
     * Setter for x
     * 
     * @see Point.Fields#x
     */
    public Point setX(Double value) {
        putDirect(FIELD_X, Double.class, Double.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for x
     * 
     * @see Point.Fields#x
     */
    public Point setX(double value) {
        putDirect(FIELD_X, Double.class, Double.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for y
     * 
     * @see Point.Fields#y
     */
    public boolean hasY() {
        return contains(FIELD_Y);
    }

    /**
     * Remover for y
     * 
     * @see Point.Fields#y
     */
    public void removeY() {
        remove(FIELD_Y);
    }

    /**
     * Getter for y
     * 
     * @see Point.Fields#y
     */
    public Double getY(GetMode mode) {
        return obtainDirect(FIELD_Y, Double.class, mode);
    }

    /**
     * Getter for y
     * 
     * @see Point.Fields#y
     */
    public Double getY() {
        return obtainDirect(FIELD_Y, Double.class, GetMode.STRICT);
    }

    /**
     * Setter for y
     * 
     * @see Point.Fields#y
     */
    public Point setY(Double value, SetMode mode) {
        putDirect(FIELD_Y, Double.class, Double.class, value, mode);
        return this;
    }

    /**
     * Setter for y
     * 
     * @see Point.Fields#y
     */
    public Point setY(Double value) {
        putDirect(FIELD_Y, Double.class, Double.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for y
     * 
     * @see Point.Fields#y
     */
    public Point setY(double value) {
        putDirect(FIELD_Y, Double.class, Double.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public Point clone()
        throws CloneNotSupportedException
    {
        return ((Point) super.clone());
    }

    @Override
    public Point copy()
        throws CloneNotSupportedException
    {
        return ((Point) super.copy());
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

        public PathSpec x() {
            return new PathSpec(getPathComponents(), "x");
        }

        public PathSpec y() {
            return new PathSpec(getPathComponents(), "y");
        }

    }

}

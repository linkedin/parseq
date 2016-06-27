
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
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/TwoPartKey.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class TwoPartKey
    extends RecordTemplate
{

    private final static TwoPartKey.Fields _fields = new TwoPartKey.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"TwoPartKey\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"major\",\"type\":\"string\"},{\"name\":\"minor\",\"type\":\"string\"}]}"));
    private final static RecordDataSchema.Field FIELD_Major = SCHEMA.getField("major");
    private final static RecordDataSchema.Field FIELD_Minor = SCHEMA.getField("minor");

    public TwoPartKey() {
        super(new DataMap(), SCHEMA);
    }

    public TwoPartKey(DataMap data) {
        super(data, SCHEMA);
    }

    public static TwoPartKey.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for major
     * 
     * @see TwoPartKey.Fields#major
     */
    public boolean hasMajor() {
        return contains(FIELD_Major);
    }

    /**
     * Remover for major
     * 
     * @see TwoPartKey.Fields#major
     */
    public void removeMajor() {
        remove(FIELD_Major);
    }

    /**
     * Getter for major
     * 
     * @see TwoPartKey.Fields#major
     */
    public String getMajor(GetMode mode) {
        return obtainDirect(FIELD_Major, String.class, mode);
    }

    /**
     * Getter for major
     * 
     * @see TwoPartKey.Fields#major
     */
    public String getMajor() {
        return obtainDirect(FIELD_Major, String.class, GetMode.STRICT);
    }

    /**
     * Setter for major
     * 
     * @see TwoPartKey.Fields#major
     */
    public TwoPartKey setMajor(String value, SetMode mode) {
        putDirect(FIELD_Major, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for major
     * 
     * @see TwoPartKey.Fields#major
     */
    public TwoPartKey setMajor(String value) {
        putDirect(FIELD_Major, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for minor
     * 
     * @see TwoPartKey.Fields#minor
     */
    public boolean hasMinor() {
        return contains(FIELD_Minor);
    }

    /**
     * Remover for minor
     * 
     * @see TwoPartKey.Fields#minor
     */
    public void removeMinor() {
        remove(FIELD_Minor);
    }

    /**
     * Getter for minor
     * 
     * @see TwoPartKey.Fields#minor
     */
    public String getMinor(GetMode mode) {
        return obtainDirect(FIELD_Minor, String.class, mode);
    }

    /**
     * Getter for minor
     * 
     * @see TwoPartKey.Fields#minor
     */
    public String getMinor() {
        return obtainDirect(FIELD_Minor, String.class, GetMode.STRICT);
    }

    /**
     * Setter for minor
     * 
     * @see TwoPartKey.Fields#minor
     */
    public TwoPartKey setMinor(String value, SetMode mode) {
        putDirect(FIELD_Minor, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for minor
     * 
     * @see TwoPartKey.Fields#minor
     */
    public TwoPartKey setMinor(String value) {
        putDirect(FIELD_Minor, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public TwoPartKey clone()
        throws CloneNotSupportedException
    {
        return ((TwoPartKey) super.clone());
    }

    @Override
    public TwoPartKey copy()
        throws CloneNotSupportedException
    {
        return ((TwoPartKey) super.copy());
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

        public PathSpec major() {
            return new PathSpec(getPathComponents(), "major");
        }

        public PathSpec minor() {
            return new PathSpec(getPathComponents(), "minor");
        }

    }

}

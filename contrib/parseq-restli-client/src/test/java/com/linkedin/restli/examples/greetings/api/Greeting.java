
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
 * A greeting
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/Greeting.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Greeting
    extends RecordTemplate
{

    private final static Greeting.Fields _fields = new Greeting.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"Greeting\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"doc\":\"A greeting\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]},\"doc\":\"tone\"}]}"));
    private final static RecordDataSchema.Field FIELD_Id = SCHEMA.getField("id");
    private final static RecordDataSchema.Field FIELD_Message = SCHEMA.getField("message");
    private final static RecordDataSchema.Field FIELD_Tone = SCHEMA.getField("tone");

    public Greeting() {
        super(new DataMap(), SCHEMA);
    }

    public Greeting(DataMap data) {
        super(data, SCHEMA);
    }

    public static Greeting.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for id
     * 
     * @see Greeting.Fields#id
     */
    public boolean hasId() {
        return contains(FIELD_Id);
    }

    /**
     * Remover for id
     * 
     * @see Greeting.Fields#id
     */
    public void removeId() {
        remove(FIELD_Id);
    }

    /**
     * Getter for id
     * 
     * @see Greeting.Fields#id
     */
    public Long getId(GetMode mode) {
        return obtainDirect(FIELD_Id, Long.class, mode);
    }

    /**
     * Getter for id
     * 
     * @see Greeting.Fields#id
     */
    public Long getId() {
        return obtainDirect(FIELD_Id, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for id
     * 
     * @see Greeting.Fields#id
     */
    public Greeting setId(Long value, SetMode mode) {
        putDirect(FIELD_Id, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for id
     * 
     * @see Greeting.Fields#id
     */
    public Greeting setId(Long value) {
        putDirect(FIELD_Id, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for id
     * 
     * @see Greeting.Fields#id
     */
    public Greeting setId(long value) {
        putDirect(FIELD_Id, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for message
     * 
     * @see Greeting.Fields#message
     */
    public boolean hasMessage() {
        return contains(FIELD_Message);
    }

    /**
     * Remover for message
     * 
     * @see Greeting.Fields#message
     */
    public void removeMessage() {
        remove(FIELD_Message);
    }

    /**
     * Getter for message
     * 
     * @see Greeting.Fields#message
     */
    public String getMessage(GetMode mode) {
        return obtainDirect(FIELD_Message, String.class, mode);
    }

    /**
     * Getter for message
     * 
     * @see Greeting.Fields#message
     */
    public String getMessage() {
        return obtainDirect(FIELD_Message, String.class, GetMode.STRICT);
    }

    /**
     * Setter for message
     * 
     * @see Greeting.Fields#message
     */
    public Greeting setMessage(String value, SetMode mode) {
        putDirect(FIELD_Message, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for message
     * 
     * @see Greeting.Fields#message
     */
    public Greeting setMessage(String value) {
        putDirect(FIELD_Message, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for tone
     * 
     * @see Greeting.Fields#tone
     */
    public boolean hasTone() {
        return contains(FIELD_Tone);
    }

    /**
     * Remover for tone
     * 
     * @see Greeting.Fields#tone
     */
    public void removeTone() {
        remove(FIELD_Tone);
    }

    /**
     * Getter for tone
     * 
     * @see Greeting.Fields#tone
     */
    public Tone getTone(GetMode mode) {
        return obtainDirect(FIELD_Tone, Tone.class, mode);
    }

    /**
     * Getter for tone
     * 
     * @see Greeting.Fields#tone
     */
    public Tone getTone() {
        return obtainDirect(FIELD_Tone, Tone.class, GetMode.STRICT);
    }

    /**
     * Setter for tone
     * 
     * @see Greeting.Fields#tone
     */
    public Greeting setTone(Tone value, SetMode mode) {
        putDirect(FIELD_Tone, Tone.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for tone
     * 
     * @see Greeting.Fields#tone
     */
    public Greeting setTone(Tone value) {
        putDirect(FIELD_Tone, Tone.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public Greeting clone()
        throws CloneNotSupportedException
    {
        return ((Greeting) super.clone());
    }

    @Override
    public Greeting copy()
        throws CloneNotSupportedException
    {
        return ((Greeting) super.copy());
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

        public PathSpec id() {
            return new PathSpec(getPathComponents(), "id");
        }

        public PathSpec message() {
            return new PathSpec(getPathComponents(), "message");
        }

        /**
         * tone
         * 
         */
        public PathSpec tone() {
            return new PathSpec(getPathComponents(), "tone");
        }

    }

}

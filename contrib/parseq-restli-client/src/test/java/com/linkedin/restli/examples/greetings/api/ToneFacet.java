
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
 * metadata for greetings search results
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ToneFacet.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class ToneFacet
    extends RecordTemplate
{

    private final static ToneFacet.Fields _fields = new ToneFacet.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"ToneFacet\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"doc\":\"metadata for greetings search results\",\"fields\":[{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]}},{\"name\":\"count\",\"type\":\"int\"}]}"));
    private final static RecordDataSchema.Field FIELD_Tone = SCHEMA.getField("tone");
    private final static RecordDataSchema.Field FIELD_Count = SCHEMA.getField("count");

    public ToneFacet() {
        super(new DataMap(), SCHEMA);
    }

    public ToneFacet(DataMap data) {
        super(data, SCHEMA);
    }

    public static ToneFacet.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for tone
     * 
     * @see ToneFacet.Fields#tone
     */
    public boolean hasTone() {
        return contains(FIELD_Tone);
    }

    /**
     * Remover for tone
     * 
     * @see ToneFacet.Fields#tone
     */
    public void removeTone() {
        remove(FIELD_Tone);
    }

    /**
     * Getter for tone
     * 
     * @see ToneFacet.Fields#tone
     */
    public Tone getTone(GetMode mode) {
        return obtainDirect(FIELD_Tone, Tone.class, mode);
    }

    /**
     * Getter for tone
     * 
     * @see ToneFacet.Fields#tone
     */
    public Tone getTone() {
        return obtainDirect(FIELD_Tone, Tone.class, GetMode.STRICT);
    }

    /**
     * Setter for tone
     * 
     * @see ToneFacet.Fields#tone
     */
    public ToneFacet setTone(Tone value, SetMode mode) {
        putDirect(FIELD_Tone, Tone.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for tone
     * 
     * @see ToneFacet.Fields#tone
     */
    public ToneFacet setTone(Tone value) {
        putDirect(FIELD_Tone, Tone.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for count
     * 
     * @see ToneFacet.Fields#count
     */
    public boolean hasCount() {
        return contains(FIELD_Count);
    }

    /**
     * Remover for count
     * 
     * @see ToneFacet.Fields#count
     */
    public void removeCount() {
        remove(FIELD_Count);
    }

    /**
     * Getter for count
     * 
     * @see ToneFacet.Fields#count
     */
    public Integer getCount(GetMode mode) {
        return obtainDirect(FIELD_Count, Integer.class, mode);
    }

    /**
     * Getter for count
     * 
     * @see ToneFacet.Fields#count
     */
    public Integer getCount() {
        return obtainDirect(FIELD_Count, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for count
     * 
     * @see ToneFacet.Fields#count
     */
    public ToneFacet setCount(Integer value, SetMode mode) {
        putDirect(FIELD_Count, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for count
     * 
     * @see ToneFacet.Fields#count
     */
    public ToneFacet setCount(Integer value) {
        putDirect(FIELD_Count, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for count
     * 
     * @see ToneFacet.Fields#count
     */
    public ToneFacet setCount(int value) {
        putDirect(FIELD_Count, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public ToneFacet clone()
        throws CloneNotSupportedException
    {
        return ((ToneFacet) super.clone());
    }

    @Override
    public ToneFacet copy()
        throws CloneNotSupportedException
    {
        return ((ToneFacet) super.copy());
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

        public PathSpec tone() {
            return new PathSpec(getPathComponents(), "tone");
        }

        public PathSpec count() {
            return new PathSpec(getPathComponents(), "count");
        }

    }

}

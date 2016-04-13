
package com.linkedin.restli.examples.groups.api;

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
 * A GroupMembership entity parameters
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/GroupMembershipParam.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GroupMembershipParam
    extends RecordTemplate
{

    private final static GroupMembershipParam.Fields _fields = new GroupMembershipParam.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"GroupMembershipParam\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A GroupMembership entity parameters\",\"fields\":[{\"name\":\"intParameter\",\"type\":\"int\"},{\"name\":\"stringParameter\",\"type\":\"string\"}]}"));
    private final static RecordDataSchema.Field FIELD_IntParameter = SCHEMA.getField("intParameter");
    private final static RecordDataSchema.Field FIELD_StringParameter = SCHEMA.getField("stringParameter");

    public GroupMembershipParam() {
        super(new DataMap(), SCHEMA);
    }

    public GroupMembershipParam(DataMap data) {
        super(data, SCHEMA);
    }

    public static GroupMembershipParam.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public boolean hasIntParameter() {
        return contains(FIELD_IntParameter);
    }

    /**
     * Remover for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public void removeIntParameter() {
        remove(FIELD_IntParameter);
    }

    /**
     * Getter for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public Integer getIntParameter(GetMode mode) {
        return obtainDirect(FIELD_IntParameter, Integer.class, mode);
    }

    /**
     * Getter for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public Integer getIntParameter() {
        return obtainDirect(FIELD_IntParameter, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public GroupMembershipParam setIntParameter(Integer value, SetMode mode) {
        putDirect(FIELD_IntParameter, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public GroupMembershipParam setIntParameter(Integer value) {
        putDirect(FIELD_IntParameter, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for intParameter
     * 
     * @see GroupMembershipParam.Fields#intParameter
     */
    public GroupMembershipParam setIntParameter(int value) {
        putDirect(FIELD_IntParameter, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for stringParameter
     * 
     * @see GroupMembershipParam.Fields#stringParameter
     */
    public boolean hasStringParameter() {
        return contains(FIELD_StringParameter);
    }

    /**
     * Remover for stringParameter
     * 
     * @see GroupMembershipParam.Fields#stringParameter
     */
    public void removeStringParameter() {
        remove(FIELD_StringParameter);
    }

    /**
     * Getter for stringParameter
     * 
     * @see GroupMembershipParam.Fields#stringParameter
     */
    public String getStringParameter(GetMode mode) {
        return obtainDirect(FIELD_StringParameter, String.class, mode);
    }

    /**
     * Getter for stringParameter
     * 
     * @see GroupMembershipParam.Fields#stringParameter
     */
    public String getStringParameter() {
        return obtainDirect(FIELD_StringParameter, String.class, GetMode.STRICT);
    }

    /**
     * Setter for stringParameter
     * 
     * @see GroupMembershipParam.Fields#stringParameter
     */
    public GroupMembershipParam setStringParameter(String value, SetMode mode) {
        putDirect(FIELD_StringParameter, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for stringParameter
     * 
     * @see GroupMembershipParam.Fields#stringParameter
     */
    public GroupMembershipParam setStringParameter(String value) {
        putDirect(FIELD_StringParameter, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public GroupMembershipParam clone()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipParam) super.clone());
    }

    @Override
    public GroupMembershipParam copy()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipParam) super.copy());
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

        public PathSpec intParameter() {
            return new PathSpec(getPathComponents(), "intParameter");
        }

        public PathSpec stringParameter() {
            return new PathSpec(getPathComponents(), "stringParameter");
        }

    }

}

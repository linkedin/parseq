
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
 * A GroupMembership entity key
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/GroupMembershipKey.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GroupMembershipKey
    extends RecordTemplate
{

    private final static GroupMembershipKey.Fields _fields = new GroupMembershipKey.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"GroupMembershipKey\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A GroupMembership entity key\",\"fields\":[{\"name\":\"memberID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"},{\"name\":\"groupID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"}]}"));
    private final static RecordDataSchema.Field FIELD_MemberID = SCHEMA.getField("memberID");
    private final static RecordDataSchema.Field FIELD_GroupID = SCHEMA.getField("groupID");

    public GroupMembershipKey() {
        super(new DataMap(), SCHEMA);
    }

    public GroupMembershipKey(DataMap data) {
        super(data, SCHEMA);
    }

    public static GroupMembershipKey.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public boolean hasMemberID() {
        return contains(FIELD_MemberID);
    }

    /**
     * Remover for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public void removeMemberID() {
        remove(FIELD_MemberID);
    }

    /**
     * Getter for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public Integer getMemberID(GetMode mode) {
        return obtainDirect(FIELD_MemberID, Integer.class, mode);
    }

    /**
     * Getter for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public Integer getMemberID() {
        return obtainDirect(FIELD_MemberID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public GroupMembershipKey setMemberID(Integer value, SetMode mode) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public GroupMembershipKey setMemberID(Integer value) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for memberID
     * 
     * @see GroupMembershipKey.Fields#memberID
     */
    public GroupMembershipKey setMemberID(int value) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public boolean hasGroupID() {
        return contains(FIELD_GroupID);
    }

    /**
     * Remover for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public void removeGroupID() {
        remove(FIELD_GroupID);
    }

    /**
     * Getter for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public Integer getGroupID(GetMode mode) {
        return obtainDirect(FIELD_GroupID, Integer.class, mode);
    }

    /**
     * Getter for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public Integer getGroupID() {
        return obtainDirect(FIELD_GroupID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public GroupMembershipKey setGroupID(Integer value, SetMode mode) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public GroupMembershipKey setGroupID(Integer value) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for groupID
     * 
     * @see GroupMembershipKey.Fields#groupID
     */
    public GroupMembershipKey setGroupID(int value) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public GroupMembershipKey clone()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipKey) super.clone());
    }

    @Override
    public GroupMembershipKey copy()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipKey) super.copy());
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

        /**
         * This field is read-only.
         * 
         */
        public PathSpec memberID() {
            return new PathSpec(getPathComponents(), "memberID");
        }

        /**
         * This field is read-only.
         * 
         */
        public PathSpec groupID() {
            return new PathSpec(getPathComponents(), "groupID");
        }

    }

}

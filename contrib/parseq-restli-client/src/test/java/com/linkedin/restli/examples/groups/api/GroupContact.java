
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
 * A contact associated with this group. Managers upload contact to manage pre-approval/blacklists + invite members
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/GroupContact.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GroupContact
    extends RecordTemplate
{

    private final static GroupContact.Fields _fields = new GroupContact.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"GroupContact\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A contact associated with this group. Managers upload contact to manage pre-approval/blacklists + invite members\",\"fields\":[{\"name\":\"contactID\",\"type\":\"int\",\"doc\":\"Surrogate ID for this contact. This field is read-only.\"},{\"name\":\"groupID\",\"type\":\"int\",\"doc\":\"The group that owns this contact\"},{\"name\":\"memberID\",\"type\":\"int\",\"doc\":\"The member associated with this contact record (null if this is a contact is not a LinkedIn member)\",\"optional\":true},{\"name\":\"firstName\",\"type\":\"string\",\"doc\":\"Contact's first name\"},{\"name\":\"lastName\",\"type\":\"string\",\"doc\":\"Contact's last name\"},{\"name\":\"isPreapproved\",\"type\":\"boolean\",\"doc\":\"True if this contact is pre-approved to join the group\"},{\"name\":\"isInvited\",\"type\":\"boolean\",\"doc\":\"True if this contact has been invited\"},{\"name\":\"createdAt\",\"type\":\"long\"},{\"name\":\"updatedAt\",\"type\":\"long\"}]}"));
    private final static RecordDataSchema.Field FIELD_ContactID = SCHEMA.getField("contactID");
    private final static RecordDataSchema.Field FIELD_GroupID = SCHEMA.getField("groupID");
    private final static RecordDataSchema.Field FIELD_MemberID = SCHEMA.getField("memberID");
    private final static RecordDataSchema.Field FIELD_FirstName = SCHEMA.getField("firstName");
    private final static RecordDataSchema.Field FIELD_LastName = SCHEMA.getField("lastName");
    private final static RecordDataSchema.Field FIELD_IsPreapproved = SCHEMA.getField("isPreapproved");
    private final static RecordDataSchema.Field FIELD_IsInvited = SCHEMA.getField("isInvited");
    private final static RecordDataSchema.Field FIELD_CreatedAt = SCHEMA.getField("createdAt");
    private final static RecordDataSchema.Field FIELD_UpdatedAt = SCHEMA.getField("updatedAt");

    public GroupContact() {
        super(new DataMap(), SCHEMA);
    }

    public GroupContact(DataMap data) {
        super(data, SCHEMA);
    }

    public static GroupContact.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public boolean hasContactID() {
        return contains(FIELD_ContactID);
    }

    /**
     * Remover for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public void removeContactID() {
        remove(FIELD_ContactID);
    }

    /**
     * Getter for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public Integer getContactID(GetMode mode) {
        return obtainDirect(FIELD_ContactID, Integer.class, mode);
    }

    /**
     * Getter for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public Integer getContactID() {
        return obtainDirect(FIELD_ContactID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public GroupContact setContactID(Integer value, SetMode mode) {
        putDirect(FIELD_ContactID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public GroupContact setContactID(Integer value) {
        putDirect(FIELD_ContactID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for contactID
     * 
     * @see GroupContact.Fields#contactID
     */
    public GroupContact setContactID(int value) {
        putDirect(FIELD_ContactID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public boolean hasGroupID() {
        return contains(FIELD_GroupID);
    }

    /**
     * Remover for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public void removeGroupID() {
        remove(FIELD_GroupID);
    }

    /**
     * Getter for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public Integer getGroupID(GetMode mode) {
        return obtainDirect(FIELD_GroupID, Integer.class, mode);
    }

    /**
     * Getter for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public Integer getGroupID() {
        return obtainDirect(FIELD_GroupID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public GroupContact setGroupID(Integer value, SetMode mode) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public GroupContact setGroupID(Integer value) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for groupID
     * 
     * @see GroupContact.Fields#groupID
     */
    public GroupContact setGroupID(int value) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public boolean hasMemberID() {
        return contains(FIELD_MemberID);
    }

    /**
     * Remover for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public void removeMemberID() {
        remove(FIELD_MemberID);
    }

    /**
     * Getter for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public Integer getMemberID(GetMode mode) {
        return obtainDirect(FIELD_MemberID, Integer.class, mode);
    }

    /**
     * Getter for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public Integer getMemberID() {
        return obtainDirect(FIELD_MemberID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public GroupContact setMemberID(Integer value, SetMode mode) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public GroupContact setMemberID(Integer value) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for memberID
     * 
     * @see GroupContact.Fields#memberID
     */
    public GroupContact setMemberID(int value) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for firstName
     * 
     * @see GroupContact.Fields#firstName
     */
    public boolean hasFirstName() {
        return contains(FIELD_FirstName);
    }

    /**
     * Remover for firstName
     * 
     * @see GroupContact.Fields#firstName
     */
    public void removeFirstName() {
        remove(FIELD_FirstName);
    }

    /**
     * Getter for firstName
     * 
     * @see GroupContact.Fields#firstName
     */
    public String getFirstName(GetMode mode) {
        return obtainDirect(FIELD_FirstName, String.class, mode);
    }

    /**
     * Getter for firstName
     * 
     * @see GroupContact.Fields#firstName
     */
    public String getFirstName() {
        return obtainDirect(FIELD_FirstName, String.class, GetMode.STRICT);
    }

    /**
     * Setter for firstName
     * 
     * @see GroupContact.Fields#firstName
     */
    public GroupContact setFirstName(String value, SetMode mode) {
        putDirect(FIELD_FirstName, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for firstName
     * 
     * @see GroupContact.Fields#firstName
     */
    public GroupContact setFirstName(String value) {
        putDirect(FIELD_FirstName, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastName
     * 
     * @see GroupContact.Fields#lastName
     */
    public boolean hasLastName() {
        return contains(FIELD_LastName);
    }

    /**
     * Remover for lastName
     * 
     * @see GroupContact.Fields#lastName
     */
    public void removeLastName() {
        remove(FIELD_LastName);
    }

    /**
     * Getter for lastName
     * 
     * @see GroupContact.Fields#lastName
     */
    public String getLastName(GetMode mode) {
        return obtainDirect(FIELD_LastName, String.class, mode);
    }

    /**
     * Getter for lastName
     * 
     * @see GroupContact.Fields#lastName
     */
    public String getLastName() {
        return obtainDirect(FIELD_LastName, String.class, GetMode.STRICT);
    }

    /**
     * Setter for lastName
     * 
     * @see GroupContact.Fields#lastName
     */
    public GroupContact setLastName(String value, SetMode mode) {
        putDirect(FIELD_LastName, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for lastName
     * 
     * @see GroupContact.Fields#lastName
     */
    public GroupContact setLastName(String value) {
        putDirect(FIELD_LastName, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public boolean hasIsPreapproved() {
        return contains(FIELD_IsPreapproved);
    }

    /**
     * Remover for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public void removeIsPreapproved() {
        remove(FIELD_IsPreapproved);
    }

    /**
     * Getter for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public Boolean isIsPreapproved(GetMode mode) {
        return obtainDirect(FIELD_IsPreapproved, Boolean.class, mode);
    }

    /**
     * Getter for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public Boolean isIsPreapproved() {
        return obtainDirect(FIELD_IsPreapproved, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public GroupContact setIsPreapproved(Boolean value, SetMode mode) {
        putDirect(FIELD_IsPreapproved, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public GroupContact setIsPreapproved(Boolean value) {
        putDirect(FIELD_IsPreapproved, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for isPreapproved
     * 
     * @see GroupContact.Fields#isPreapproved
     */
    public GroupContact setIsPreapproved(boolean value) {
        putDirect(FIELD_IsPreapproved, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public boolean hasIsInvited() {
        return contains(FIELD_IsInvited);
    }

    /**
     * Remover for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public void removeIsInvited() {
        remove(FIELD_IsInvited);
    }

    /**
     * Getter for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public Boolean isIsInvited(GetMode mode) {
        return obtainDirect(FIELD_IsInvited, Boolean.class, mode);
    }

    /**
     * Getter for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public Boolean isIsInvited() {
        return obtainDirect(FIELD_IsInvited, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public GroupContact setIsInvited(Boolean value, SetMode mode) {
        putDirect(FIELD_IsInvited, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public GroupContact setIsInvited(Boolean value) {
        putDirect(FIELD_IsInvited, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for isInvited
     * 
     * @see GroupContact.Fields#isInvited
     */
    public GroupContact setIsInvited(boolean value) {
        putDirect(FIELD_IsInvited, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public boolean hasCreatedAt() {
        return contains(FIELD_CreatedAt);
    }

    /**
     * Remover for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public void removeCreatedAt() {
        remove(FIELD_CreatedAt);
    }

    /**
     * Getter for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public Long getCreatedAt(GetMode mode) {
        return obtainDirect(FIELD_CreatedAt, Long.class, mode);
    }

    /**
     * Getter for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public Long getCreatedAt() {
        return obtainDirect(FIELD_CreatedAt, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public GroupContact setCreatedAt(Long value, SetMode mode) {
        putDirect(FIELD_CreatedAt, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public GroupContact setCreatedAt(Long value) {
        putDirect(FIELD_CreatedAt, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for createdAt
     * 
     * @see GroupContact.Fields#createdAt
     */
    public GroupContact setCreatedAt(long value) {
        putDirect(FIELD_CreatedAt, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public boolean hasUpdatedAt() {
        return contains(FIELD_UpdatedAt);
    }

    /**
     * Remover for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public void removeUpdatedAt() {
        remove(FIELD_UpdatedAt);
    }

    /**
     * Getter for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public Long getUpdatedAt(GetMode mode) {
        return obtainDirect(FIELD_UpdatedAt, Long.class, mode);
    }

    /**
     * Getter for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public Long getUpdatedAt() {
        return obtainDirect(FIELD_UpdatedAt, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public GroupContact setUpdatedAt(Long value, SetMode mode) {
        putDirect(FIELD_UpdatedAt, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public GroupContact setUpdatedAt(Long value) {
        putDirect(FIELD_UpdatedAt, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for updatedAt
     * 
     * @see GroupContact.Fields#updatedAt
     */
    public GroupContact setUpdatedAt(long value) {
        putDirect(FIELD_UpdatedAt, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public GroupContact clone()
        throws CloneNotSupportedException
    {
        return ((GroupContact) super.clone());
    }

    @Override
    public GroupContact copy()
        throws CloneNotSupportedException
    {
        return ((GroupContact) super.copy());
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
         * Surrogate ID for this contact. This field is read-only.
         * 
         */
        public PathSpec contactID() {
            return new PathSpec(getPathComponents(), "contactID");
        }

        /**
         * The group that owns this contact
         * 
         */
        public PathSpec groupID() {
            return new PathSpec(getPathComponents(), "groupID");
        }

        /**
         * The member associated with this contact record (null if this is a contact is not a LinkedIn member)
         * 
         */
        public PathSpec memberID() {
            return new PathSpec(getPathComponents(), "memberID");
        }

        /**
         * Contact's first name
         * 
         */
        public PathSpec firstName() {
            return new PathSpec(getPathComponents(), "firstName");
        }

        /**
         * Contact's last name
         * 
         */
        public PathSpec lastName() {
            return new PathSpec(getPathComponents(), "lastName");
        }

        /**
         * True if this contact is pre-approved to join the group
         * 
         */
        public PathSpec isPreapproved() {
            return new PathSpec(getPathComponents(), "isPreapproved");
        }

        /**
         * True if this contact has been invited
         * 
         */
        public PathSpec isInvited() {
            return new PathSpec(getPathComponents(), "isInvited");
        }

        public PathSpec createdAt() {
            return new PathSpec(getPathComponents(), "createdAt");
        }

        public PathSpec updatedAt() {
            return new PathSpec(getPathComponents(), "updatedAt");
        }

    }

}

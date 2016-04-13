
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
 * A GroupMembership entity
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/GroupMembership.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GroupMembership
    extends RecordTemplate
{

    private final static GroupMembership.Fields _fields = new GroupMembership.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"GroupMembership\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A GroupMembership entity\",\"fields\":[{\"name\":\"id\",\"type\":\"string\",\"doc\":\"Compound key of groupID and memberID\"},{\"name\":\"memberID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"},{\"name\":\"groupID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"},{\"name\":\"membershipLevel\",\"type\":{\"type\":\"enum\",\"name\":\"MembershipLevel\",\"symbols\":[\"BLOCKED\",\"NON_MEMBER\",\"REQUESTING_TO_JOIN\",\"MEMBER\",\"MODERATOR\",\"MANAGER\",\"OWNER\"]}},{\"name\":\"contactEmail\",\"type\":\"string\"},{\"name\":\"isPublicized\",\"type\":\"boolean\"},{\"name\":\"allowMessagesFromMembers\",\"type\":\"boolean\"},{\"name\":\"joinedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"resignedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"lastModifiedStateTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"emailDigestFrequency\",\"type\":{\"type\":\"enum\",\"name\":\"EmailDigestFrequency\",\"symbols\":[\"NONE\",\"DAILY\",\"WEEKLY\"]}},{\"name\":\"creationTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"lastModifiedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"emailAnnouncementsFromManagers\",\"type\":\"boolean\"},{\"name\":\"emailForEveryNewPost\",\"type\":\"boolean\"},{\"name\":\"writeLevel\",\"type\":{\"type\":\"enum\",\"name\":\"WriteLevel\",\"symbols\":[\"NONE\",\"PREMODERATED\",\"DEFAULT\",\"FULL\"]},\"doc\":\"This field can only be accessed by moderators of the group\"},{\"name\":\"firstName\",\"type\":\"string\",\"doc\":\"Denormalized from members\"},{\"name\":\"lastName\",\"type\":\"string\",\"doc\":\"Denormalized from members\"}]}"));
    private final static RecordDataSchema.Field FIELD_Id = SCHEMA.getField("id");
    private final static RecordDataSchema.Field FIELD_MemberID = SCHEMA.getField("memberID");
    private final static RecordDataSchema.Field FIELD_GroupID = SCHEMA.getField("groupID");
    private final static RecordDataSchema.Field FIELD_MembershipLevel = SCHEMA.getField("membershipLevel");
    private final static RecordDataSchema.Field FIELD_ContactEmail = SCHEMA.getField("contactEmail");
    private final static RecordDataSchema.Field FIELD_IsPublicized = SCHEMA.getField("isPublicized");
    private final static RecordDataSchema.Field FIELD_AllowMessagesFromMembers = SCHEMA.getField("allowMessagesFromMembers");
    private final static RecordDataSchema.Field FIELD_JoinedTime = SCHEMA.getField("joinedTime");
    private final static RecordDataSchema.Field FIELD_ResignedTime = SCHEMA.getField("resignedTime");
    private final static RecordDataSchema.Field FIELD_LastModifiedStateTime = SCHEMA.getField("lastModifiedStateTime");
    private final static RecordDataSchema.Field FIELD_EmailDigestFrequency = SCHEMA.getField("emailDigestFrequency");
    private final static RecordDataSchema.Field FIELD_CreationTime = SCHEMA.getField("creationTime");
    private final static RecordDataSchema.Field FIELD_LastModifiedTime = SCHEMA.getField("lastModifiedTime");
    private final static RecordDataSchema.Field FIELD_EmailAnnouncementsFromManagers = SCHEMA.getField("emailAnnouncementsFromManagers");
    private final static RecordDataSchema.Field FIELD_EmailForEveryNewPost = SCHEMA.getField("emailForEveryNewPost");
    private final static RecordDataSchema.Field FIELD_WriteLevel = SCHEMA.getField("writeLevel");
    private final static RecordDataSchema.Field FIELD_FirstName = SCHEMA.getField("firstName");
    private final static RecordDataSchema.Field FIELD_LastName = SCHEMA.getField("lastName");

    public GroupMembership() {
        super(new DataMap(), SCHEMA);
    }

    public GroupMembership(DataMap data) {
        super(data, SCHEMA);
    }

    public static GroupMembership.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for id
     * 
     * @see GroupMembership.Fields#id
     */
    public boolean hasId() {
        return contains(FIELD_Id);
    }

    /**
     * Remover for id
     * 
     * @see GroupMembership.Fields#id
     */
    public void removeId() {
        remove(FIELD_Id);
    }

    /**
     * Getter for id
     * 
     * @see GroupMembership.Fields#id
     */
    public String getId(GetMode mode) {
        return obtainDirect(FIELD_Id, String.class, mode);
    }

    /**
     * Getter for id
     * 
     * @see GroupMembership.Fields#id
     */
    public String getId() {
        return obtainDirect(FIELD_Id, String.class, GetMode.STRICT);
    }

    /**
     * Setter for id
     * 
     * @see GroupMembership.Fields#id
     */
    public GroupMembership setId(String value, SetMode mode) {
        putDirect(FIELD_Id, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for id
     * 
     * @see GroupMembership.Fields#id
     */
    public GroupMembership setId(String value) {
        putDirect(FIELD_Id, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public boolean hasMemberID() {
        return contains(FIELD_MemberID);
    }

    /**
     * Remover for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public void removeMemberID() {
        remove(FIELD_MemberID);
    }

    /**
     * Getter for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public Integer getMemberID(GetMode mode) {
        return obtainDirect(FIELD_MemberID, Integer.class, mode);
    }

    /**
     * Getter for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public Integer getMemberID() {
        return obtainDirect(FIELD_MemberID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public GroupMembership setMemberID(Integer value, SetMode mode) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public GroupMembership setMemberID(Integer value) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for memberID
     * 
     * @see GroupMembership.Fields#memberID
     */
    public GroupMembership setMemberID(int value) {
        putDirect(FIELD_MemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public boolean hasGroupID() {
        return contains(FIELD_GroupID);
    }

    /**
     * Remover for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public void removeGroupID() {
        remove(FIELD_GroupID);
    }

    /**
     * Getter for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public Integer getGroupID(GetMode mode) {
        return obtainDirect(FIELD_GroupID, Integer.class, mode);
    }

    /**
     * Getter for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public Integer getGroupID() {
        return obtainDirect(FIELD_GroupID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public GroupMembership setGroupID(Integer value, SetMode mode) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public GroupMembership setGroupID(Integer value) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for groupID
     * 
     * @see GroupMembership.Fields#groupID
     */
    public GroupMembership setGroupID(int value) {
        putDirect(FIELD_GroupID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for membershipLevel
     * 
     * @see GroupMembership.Fields#membershipLevel
     */
    public boolean hasMembershipLevel() {
        return contains(FIELD_MembershipLevel);
    }

    /**
     * Remover for membershipLevel
     * 
     * @see GroupMembership.Fields#membershipLevel
     */
    public void removeMembershipLevel() {
        remove(FIELD_MembershipLevel);
    }

    /**
     * Getter for membershipLevel
     * 
     * @see GroupMembership.Fields#membershipLevel
     */
    public MembershipLevel getMembershipLevel(GetMode mode) {
        return obtainDirect(FIELD_MembershipLevel, MembershipLevel.class, mode);
    }

    /**
     * Getter for membershipLevel
     * 
     * @see GroupMembership.Fields#membershipLevel
     */
    public MembershipLevel getMembershipLevel() {
        return obtainDirect(FIELD_MembershipLevel, MembershipLevel.class, GetMode.STRICT);
    }

    /**
     * Setter for membershipLevel
     * 
     * @see GroupMembership.Fields#membershipLevel
     */
    public GroupMembership setMembershipLevel(MembershipLevel value, SetMode mode) {
        putDirect(FIELD_MembershipLevel, MembershipLevel.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for membershipLevel
     * 
     * @see GroupMembership.Fields#membershipLevel
     */
    public GroupMembership setMembershipLevel(MembershipLevel value) {
        putDirect(FIELD_MembershipLevel, MembershipLevel.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for contactEmail
     * 
     * @see GroupMembership.Fields#contactEmail
     */
    public boolean hasContactEmail() {
        return contains(FIELD_ContactEmail);
    }

    /**
     * Remover for contactEmail
     * 
     * @see GroupMembership.Fields#contactEmail
     */
    public void removeContactEmail() {
        remove(FIELD_ContactEmail);
    }

    /**
     * Getter for contactEmail
     * 
     * @see GroupMembership.Fields#contactEmail
     */
    public String getContactEmail(GetMode mode) {
        return obtainDirect(FIELD_ContactEmail, String.class, mode);
    }

    /**
     * Getter for contactEmail
     * 
     * @see GroupMembership.Fields#contactEmail
     */
    public String getContactEmail() {
        return obtainDirect(FIELD_ContactEmail, String.class, GetMode.STRICT);
    }

    /**
     * Setter for contactEmail
     * 
     * @see GroupMembership.Fields#contactEmail
     */
    public GroupMembership setContactEmail(String value, SetMode mode) {
        putDirect(FIELD_ContactEmail, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for contactEmail
     * 
     * @see GroupMembership.Fields#contactEmail
     */
    public GroupMembership setContactEmail(String value) {
        putDirect(FIELD_ContactEmail, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public boolean hasIsPublicized() {
        return contains(FIELD_IsPublicized);
    }

    /**
     * Remover for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public void removeIsPublicized() {
        remove(FIELD_IsPublicized);
    }

    /**
     * Getter for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public Boolean isIsPublicized(GetMode mode) {
        return obtainDirect(FIELD_IsPublicized, Boolean.class, mode);
    }

    /**
     * Getter for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public Boolean isIsPublicized() {
        return obtainDirect(FIELD_IsPublicized, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public GroupMembership setIsPublicized(Boolean value, SetMode mode) {
        putDirect(FIELD_IsPublicized, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public GroupMembership setIsPublicized(Boolean value) {
        putDirect(FIELD_IsPublicized, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for isPublicized
     * 
     * @see GroupMembership.Fields#isPublicized
     */
    public GroupMembership setIsPublicized(boolean value) {
        putDirect(FIELD_IsPublicized, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public boolean hasAllowMessagesFromMembers() {
        return contains(FIELD_AllowMessagesFromMembers);
    }

    /**
     * Remover for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public void removeAllowMessagesFromMembers() {
        remove(FIELD_AllowMessagesFromMembers);
    }

    /**
     * Getter for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public Boolean isAllowMessagesFromMembers(GetMode mode) {
        return obtainDirect(FIELD_AllowMessagesFromMembers, Boolean.class, mode);
    }

    /**
     * Getter for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public Boolean isAllowMessagesFromMembers() {
        return obtainDirect(FIELD_AllowMessagesFromMembers, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public GroupMembership setAllowMessagesFromMembers(Boolean value, SetMode mode) {
        putDirect(FIELD_AllowMessagesFromMembers, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public GroupMembership setAllowMessagesFromMembers(Boolean value) {
        putDirect(FIELD_AllowMessagesFromMembers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for allowMessagesFromMembers
     * 
     * @see GroupMembership.Fields#allowMessagesFromMembers
     */
    public GroupMembership setAllowMessagesFromMembers(boolean value) {
        putDirect(FIELD_AllowMessagesFromMembers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public boolean hasJoinedTime() {
        return contains(FIELD_JoinedTime);
    }

    /**
     * Remover for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public void removeJoinedTime() {
        remove(FIELD_JoinedTime);
    }

    /**
     * Getter for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public Long getJoinedTime(GetMode mode) {
        return obtainDirect(FIELD_JoinedTime, Long.class, mode);
    }

    /**
     * Getter for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public Long getJoinedTime() {
        return obtainDirect(FIELD_JoinedTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public GroupMembership setJoinedTime(Long value, SetMode mode) {
        putDirect(FIELD_JoinedTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public GroupMembership setJoinedTime(Long value) {
        putDirect(FIELD_JoinedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for joinedTime
     * 
     * @see GroupMembership.Fields#joinedTime
     */
    public GroupMembership setJoinedTime(long value) {
        putDirect(FIELD_JoinedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public boolean hasResignedTime() {
        return contains(FIELD_ResignedTime);
    }

    /**
     * Remover for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public void removeResignedTime() {
        remove(FIELD_ResignedTime);
    }

    /**
     * Getter for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public Long getResignedTime(GetMode mode) {
        return obtainDirect(FIELD_ResignedTime, Long.class, mode);
    }

    /**
     * Getter for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public Long getResignedTime() {
        return obtainDirect(FIELD_ResignedTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public GroupMembership setResignedTime(Long value, SetMode mode) {
        putDirect(FIELD_ResignedTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public GroupMembership setResignedTime(Long value) {
        putDirect(FIELD_ResignedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for resignedTime
     * 
     * @see GroupMembership.Fields#resignedTime
     */
    public GroupMembership setResignedTime(long value) {
        putDirect(FIELD_ResignedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public boolean hasLastModifiedStateTime() {
        return contains(FIELD_LastModifiedStateTime);
    }

    /**
     * Remover for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public void removeLastModifiedStateTime() {
        remove(FIELD_LastModifiedStateTime);
    }

    /**
     * Getter for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public Long getLastModifiedStateTime(GetMode mode) {
        return obtainDirect(FIELD_LastModifiedStateTime, Long.class, mode);
    }

    /**
     * Getter for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public Long getLastModifiedStateTime() {
        return obtainDirect(FIELD_LastModifiedStateTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public GroupMembership setLastModifiedStateTime(Long value, SetMode mode) {
        putDirect(FIELD_LastModifiedStateTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public GroupMembership setLastModifiedStateTime(Long value) {
        putDirect(FIELD_LastModifiedStateTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for lastModifiedStateTime
     * 
     * @see GroupMembership.Fields#lastModifiedStateTime
     */
    public GroupMembership setLastModifiedStateTime(long value) {
        putDirect(FIELD_LastModifiedStateTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for emailDigestFrequency
     * 
     * @see GroupMembership.Fields#emailDigestFrequency
     */
    public boolean hasEmailDigestFrequency() {
        return contains(FIELD_EmailDigestFrequency);
    }

    /**
     * Remover for emailDigestFrequency
     * 
     * @see GroupMembership.Fields#emailDigestFrequency
     */
    public void removeEmailDigestFrequency() {
        remove(FIELD_EmailDigestFrequency);
    }

    /**
     * Getter for emailDigestFrequency
     * 
     * @see GroupMembership.Fields#emailDigestFrequency
     */
    public EmailDigestFrequency getEmailDigestFrequency(GetMode mode) {
        return obtainDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, mode);
    }

    /**
     * Getter for emailDigestFrequency
     * 
     * @see GroupMembership.Fields#emailDigestFrequency
     */
    public EmailDigestFrequency getEmailDigestFrequency() {
        return obtainDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, GetMode.STRICT);
    }

    /**
     * Setter for emailDigestFrequency
     * 
     * @see GroupMembership.Fields#emailDigestFrequency
     */
    public GroupMembership setEmailDigestFrequency(EmailDigestFrequency value, SetMode mode) {
        putDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for emailDigestFrequency
     * 
     * @see GroupMembership.Fields#emailDigestFrequency
     */
    public GroupMembership setEmailDigestFrequency(EmailDigestFrequency value) {
        putDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public boolean hasCreationTime() {
        return contains(FIELD_CreationTime);
    }

    /**
     * Remover for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public void removeCreationTime() {
        remove(FIELD_CreationTime);
    }

    /**
     * Getter for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public Long getCreationTime(GetMode mode) {
        return obtainDirect(FIELD_CreationTime, Long.class, mode);
    }

    /**
     * Getter for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public Long getCreationTime() {
        return obtainDirect(FIELD_CreationTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public GroupMembership setCreationTime(Long value, SetMode mode) {
        putDirect(FIELD_CreationTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public GroupMembership setCreationTime(Long value) {
        putDirect(FIELD_CreationTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for creationTime
     * 
     * @see GroupMembership.Fields#creationTime
     */
    public GroupMembership setCreationTime(long value) {
        putDirect(FIELD_CreationTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public boolean hasLastModifiedTime() {
        return contains(FIELD_LastModifiedTime);
    }

    /**
     * Remover for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public void removeLastModifiedTime() {
        remove(FIELD_LastModifiedTime);
    }

    /**
     * Getter for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public Long getLastModifiedTime(GetMode mode) {
        return obtainDirect(FIELD_LastModifiedTime, Long.class, mode);
    }

    /**
     * Getter for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public Long getLastModifiedTime() {
        return obtainDirect(FIELD_LastModifiedTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public GroupMembership setLastModifiedTime(Long value, SetMode mode) {
        putDirect(FIELD_LastModifiedTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public GroupMembership setLastModifiedTime(Long value) {
        putDirect(FIELD_LastModifiedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for lastModifiedTime
     * 
     * @see GroupMembership.Fields#lastModifiedTime
     */
    public GroupMembership setLastModifiedTime(long value) {
        putDirect(FIELD_LastModifiedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public boolean hasEmailAnnouncementsFromManagers() {
        return contains(FIELD_EmailAnnouncementsFromManagers);
    }

    /**
     * Remover for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public void removeEmailAnnouncementsFromManagers() {
        remove(FIELD_EmailAnnouncementsFromManagers);
    }

    /**
     * Getter for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public Boolean isEmailAnnouncementsFromManagers(GetMode mode) {
        return obtainDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, mode);
    }

    /**
     * Getter for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public Boolean isEmailAnnouncementsFromManagers() {
        return obtainDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public GroupMembership setEmailAnnouncementsFromManagers(Boolean value, SetMode mode) {
        putDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public GroupMembership setEmailAnnouncementsFromManagers(Boolean value) {
        putDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for emailAnnouncementsFromManagers
     * 
     * @see GroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public GroupMembership setEmailAnnouncementsFromManagers(boolean value) {
        putDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public boolean hasEmailForEveryNewPost() {
        return contains(FIELD_EmailForEveryNewPost);
    }

    /**
     * Remover for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public void removeEmailForEveryNewPost() {
        remove(FIELD_EmailForEveryNewPost);
    }

    /**
     * Getter for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public Boolean isEmailForEveryNewPost(GetMode mode) {
        return obtainDirect(FIELD_EmailForEveryNewPost, Boolean.class, mode);
    }

    /**
     * Getter for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public Boolean isEmailForEveryNewPost() {
        return obtainDirect(FIELD_EmailForEveryNewPost, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public GroupMembership setEmailForEveryNewPost(Boolean value, SetMode mode) {
        putDirect(FIELD_EmailForEveryNewPost, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public GroupMembership setEmailForEveryNewPost(Boolean value) {
        putDirect(FIELD_EmailForEveryNewPost, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for emailForEveryNewPost
     * 
     * @see GroupMembership.Fields#emailForEveryNewPost
     */
    public GroupMembership setEmailForEveryNewPost(boolean value) {
        putDirect(FIELD_EmailForEveryNewPost, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for writeLevel
     * 
     * @see GroupMembership.Fields#writeLevel
     */
    public boolean hasWriteLevel() {
        return contains(FIELD_WriteLevel);
    }

    /**
     * Remover for writeLevel
     * 
     * @see GroupMembership.Fields#writeLevel
     */
    public void removeWriteLevel() {
        remove(FIELD_WriteLevel);
    }

    /**
     * Getter for writeLevel
     * 
     * @see GroupMembership.Fields#writeLevel
     */
    public WriteLevel getWriteLevel(GetMode mode) {
        return obtainDirect(FIELD_WriteLevel, WriteLevel.class, mode);
    }

    /**
     * Getter for writeLevel
     * 
     * @see GroupMembership.Fields#writeLevel
     */
    public WriteLevel getWriteLevel() {
        return obtainDirect(FIELD_WriteLevel, WriteLevel.class, GetMode.STRICT);
    }

    /**
     * Setter for writeLevel
     * 
     * @see GroupMembership.Fields#writeLevel
     */
    public GroupMembership setWriteLevel(WriteLevel value, SetMode mode) {
        putDirect(FIELD_WriteLevel, WriteLevel.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for writeLevel
     * 
     * @see GroupMembership.Fields#writeLevel
     */
    public GroupMembership setWriteLevel(WriteLevel value) {
        putDirect(FIELD_WriteLevel, WriteLevel.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for firstName
     * 
     * @see GroupMembership.Fields#firstName
     */
    public boolean hasFirstName() {
        return contains(FIELD_FirstName);
    }

    /**
     * Remover for firstName
     * 
     * @see GroupMembership.Fields#firstName
     */
    public void removeFirstName() {
        remove(FIELD_FirstName);
    }

    /**
     * Getter for firstName
     * 
     * @see GroupMembership.Fields#firstName
     */
    public String getFirstName(GetMode mode) {
        return obtainDirect(FIELD_FirstName, String.class, mode);
    }

    /**
     * Getter for firstName
     * 
     * @see GroupMembership.Fields#firstName
     */
    public String getFirstName() {
        return obtainDirect(FIELD_FirstName, String.class, GetMode.STRICT);
    }

    /**
     * Setter for firstName
     * 
     * @see GroupMembership.Fields#firstName
     */
    public GroupMembership setFirstName(String value, SetMode mode) {
        putDirect(FIELD_FirstName, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for firstName
     * 
     * @see GroupMembership.Fields#firstName
     */
    public GroupMembership setFirstName(String value) {
        putDirect(FIELD_FirstName, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastName
     * 
     * @see GroupMembership.Fields#lastName
     */
    public boolean hasLastName() {
        return contains(FIELD_LastName);
    }

    /**
     * Remover for lastName
     * 
     * @see GroupMembership.Fields#lastName
     */
    public void removeLastName() {
        remove(FIELD_LastName);
    }

    /**
     * Getter for lastName
     * 
     * @see GroupMembership.Fields#lastName
     */
    public String getLastName(GetMode mode) {
        return obtainDirect(FIELD_LastName, String.class, mode);
    }

    /**
     * Getter for lastName
     * 
     * @see GroupMembership.Fields#lastName
     */
    public String getLastName() {
        return obtainDirect(FIELD_LastName, String.class, GetMode.STRICT);
    }

    /**
     * Setter for lastName
     * 
     * @see GroupMembership.Fields#lastName
     */
    public GroupMembership setLastName(String value, SetMode mode) {
        putDirect(FIELD_LastName, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for lastName
     * 
     * @see GroupMembership.Fields#lastName
     */
    public GroupMembership setLastName(String value) {
        putDirect(FIELD_LastName, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public GroupMembership clone()
        throws CloneNotSupportedException
    {
        return ((GroupMembership) super.clone());
    }

    @Override
    public GroupMembership copy()
        throws CloneNotSupportedException
    {
        return ((GroupMembership) super.copy());
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
         * Compound key of groupID and memberID
         * 
         */
        public PathSpec id() {
            return new PathSpec(getPathComponents(), "id");
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

        public PathSpec membershipLevel() {
            return new PathSpec(getPathComponents(), "membershipLevel");
        }

        public PathSpec contactEmail() {
            return new PathSpec(getPathComponents(), "contactEmail");
        }

        public PathSpec isPublicized() {
            return new PathSpec(getPathComponents(), "isPublicized");
        }

        public PathSpec allowMessagesFromMembers() {
            return new PathSpec(getPathComponents(), "allowMessagesFromMembers");
        }

        /**
         * This field is read-only.
         * 
         */
        public PathSpec joinedTime() {
            return new PathSpec(getPathComponents(), "joinedTime");
        }

        /**
         * This field is read-only.
         * 
         */
        public PathSpec resignedTime() {
            return new PathSpec(getPathComponents(), "resignedTime");
        }

        /**
         * This field is read-only.
         * 
         */
        public PathSpec lastModifiedStateTime() {
            return new PathSpec(getPathComponents(), "lastModifiedStateTime");
        }

        public PathSpec emailDigestFrequency() {
            return new PathSpec(getPathComponents(), "emailDigestFrequency");
        }

        /**
         * This field is read-only.
         * 
         */
        public PathSpec creationTime() {
            return new PathSpec(getPathComponents(), "creationTime");
        }

        /**
         * This field is read-only.
         * 
         */
        public PathSpec lastModifiedTime() {
            return new PathSpec(getPathComponents(), "lastModifiedTime");
        }

        public PathSpec emailAnnouncementsFromManagers() {
            return new PathSpec(getPathComponents(), "emailAnnouncementsFromManagers");
        }

        public PathSpec emailForEveryNewPost() {
            return new PathSpec(getPathComponents(), "emailForEveryNewPost");
        }

        /**
         * This field can only be accessed by moderators of the group
         * 
         */
        public PathSpec writeLevel() {
            return new PathSpec(getPathComponents(), "writeLevel");
        }

        /**
         * Denormalized from members
         * 
         */
        public PathSpec firstName() {
            return new PathSpec(getPathComponents(), "firstName");
        }

        /**
         * Denormalized from members
         * 
         */
        public PathSpec lastName() {
            return new PathSpec(getPathComponents(), "lastName");
        }

    }

}

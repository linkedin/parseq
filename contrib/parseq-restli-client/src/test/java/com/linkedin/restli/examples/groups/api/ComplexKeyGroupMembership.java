
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
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/ComplexKeyGroupMembership.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class ComplexKeyGroupMembership
    extends RecordTemplate
{

    private final static ComplexKeyGroupMembership.Fields _fields = new ComplexKeyGroupMembership.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"ComplexKeyGroupMembership\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A GroupMembership entity\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"record\",\"name\":\"GroupMembershipKey\",\"doc\":\"A GroupMembership entity key\",\"fields\":[{\"name\":\"memberID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"},{\"name\":\"groupID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"}]},\"doc\":\"Complex key consisting of groupID and memberID\"},{\"name\":\"membershipLevel\",\"type\":{\"type\":\"enum\",\"name\":\"MembershipLevel\",\"symbols\":[\"BLOCKED\",\"NON_MEMBER\",\"REQUESTING_TO_JOIN\",\"MEMBER\",\"MODERATOR\",\"MANAGER\",\"OWNER\"]}},{\"name\":\"contactEmail\",\"type\":\"string\"},{\"name\":\"isPublicized\",\"type\":\"boolean\"},{\"name\":\"allowMessagesFromMembers\",\"type\":\"boolean\"},{\"name\":\"joinedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"resignedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"lastModifiedStateTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"emailDigestFrequency\",\"type\":{\"type\":\"enum\",\"name\":\"EmailDigestFrequency\",\"symbols\":[\"NONE\",\"DAILY\",\"WEEKLY\"]}},{\"name\":\"creationTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"lastModifiedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"emailAnnouncementsFromManagers\",\"type\":\"boolean\"},{\"name\":\"emailForEveryNewPost\",\"type\":\"boolean\"},{\"name\":\"writeLevel\",\"type\":{\"type\":\"enum\",\"name\":\"WriteLevel\",\"symbols\":[\"NONE\",\"PREMODERATED\",\"DEFAULT\",\"FULL\"]},\"doc\":\"This field can only be accessed by moderators of the group\"},{\"name\":\"firstName\",\"type\":\"string\",\"doc\":\"Denormalized from members\"},{\"name\":\"lastName\",\"type\":\"string\",\"doc\":\"Denormalized from members\"}]}"));
    private final static RecordDataSchema.Field FIELD_Id = SCHEMA.getField("id");
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

    public ComplexKeyGroupMembership() {
        super(new DataMap(), SCHEMA);
    }

    public ComplexKeyGroupMembership(DataMap data) {
        super(data, SCHEMA);
    }

    public static ComplexKeyGroupMembership.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for id
     * 
     * @see ComplexKeyGroupMembership.Fields#id
     */
    public boolean hasId() {
        return contains(FIELD_Id);
    }

    /**
     * Remover for id
     * 
     * @see ComplexKeyGroupMembership.Fields#id
     */
    public void removeId() {
        remove(FIELD_Id);
    }

    /**
     * Getter for id
     * 
     * @see ComplexKeyGroupMembership.Fields#id
     */
    public GroupMembershipKey getId(GetMode mode) {
        return obtainWrapped(FIELD_Id, GroupMembershipKey.class, mode);
    }

    /**
     * Getter for id
     * 
     * @see ComplexKeyGroupMembership.Fields#id
     */
    public GroupMembershipKey getId() {
        return obtainWrapped(FIELD_Id, GroupMembershipKey.class, GetMode.STRICT);
    }

    /**
     * Setter for id
     * 
     * @see ComplexKeyGroupMembership.Fields#id
     */
    public ComplexKeyGroupMembership setId(GroupMembershipKey value, SetMode mode) {
        putWrapped(FIELD_Id, GroupMembershipKey.class, value, mode);
        return this;
    }

    /**
     * Setter for id
     * 
     * @see ComplexKeyGroupMembership.Fields#id
     */
    public ComplexKeyGroupMembership setId(GroupMembershipKey value) {
        putWrapped(FIELD_Id, GroupMembershipKey.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for membershipLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#membershipLevel
     */
    public boolean hasMembershipLevel() {
        return contains(FIELD_MembershipLevel);
    }

    /**
     * Remover for membershipLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#membershipLevel
     */
    public void removeMembershipLevel() {
        remove(FIELD_MembershipLevel);
    }

    /**
     * Getter for membershipLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#membershipLevel
     */
    public MembershipLevel getMembershipLevel(GetMode mode) {
        return obtainDirect(FIELD_MembershipLevel, MembershipLevel.class, mode);
    }

    /**
     * Getter for membershipLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#membershipLevel
     */
    public MembershipLevel getMembershipLevel() {
        return obtainDirect(FIELD_MembershipLevel, MembershipLevel.class, GetMode.STRICT);
    }

    /**
     * Setter for membershipLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#membershipLevel
     */
    public ComplexKeyGroupMembership setMembershipLevel(MembershipLevel value, SetMode mode) {
        putDirect(FIELD_MembershipLevel, MembershipLevel.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for membershipLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#membershipLevel
     */
    public ComplexKeyGroupMembership setMembershipLevel(MembershipLevel value) {
        putDirect(FIELD_MembershipLevel, MembershipLevel.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for contactEmail
     * 
     * @see ComplexKeyGroupMembership.Fields#contactEmail
     */
    public boolean hasContactEmail() {
        return contains(FIELD_ContactEmail);
    }

    /**
     * Remover for contactEmail
     * 
     * @see ComplexKeyGroupMembership.Fields#contactEmail
     */
    public void removeContactEmail() {
        remove(FIELD_ContactEmail);
    }

    /**
     * Getter for contactEmail
     * 
     * @see ComplexKeyGroupMembership.Fields#contactEmail
     */
    public String getContactEmail(GetMode mode) {
        return obtainDirect(FIELD_ContactEmail, String.class, mode);
    }

    /**
     * Getter for contactEmail
     * 
     * @see ComplexKeyGroupMembership.Fields#contactEmail
     */
    public String getContactEmail() {
        return obtainDirect(FIELD_ContactEmail, String.class, GetMode.STRICT);
    }

    /**
     * Setter for contactEmail
     * 
     * @see ComplexKeyGroupMembership.Fields#contactEmail
     */
    public ComplexKeyGroupMembership setContactEmail(String value, SetMode mode) {
        putDirect(FIELD_ContactEmail, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for contactEmail
     * 
     * @see ComplexKeyGroupMembership.Fields#contactEmail
     */
    public ComplexKeyGroupMembership setContactEmail(String value) {
        putDirect(FIELD_ContactEmail, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public boolean hasIsPublicized() {
        return contains(FIELD_IsPublicized);
    }

    /**
     * Remover for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public void removeIsPublicized() {
        remove(FIELD_IsPublicized);
    }

    /**
     * Getter for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public Boolean isIsPublicized(GetMode mode) {
        return obtainDirect(FIELD_IsPublicized, Boolean.class, mode);
    }

    /**
     * Getter for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public Boolean isIsPublicized() {
        return obtainDirect(FIELD_IsPublicized, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public ComplexKeyGroupMembership setIsPublicized(Boolean value, SetMode mode) {
        putDirect(FIELD_IsPublicized, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public ComplexKeyGroupMembership setIsPublicized(Boolean value) {
        putDirect(FIELD_IsPublicized, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for isPublicized
     * 
     * @see ComplexKeyGroupMembership.Fields#isPublicized
     */
    public ComplexKeyGroupMembership setIsPublicized(boolean value) {
        putDirect(FIELD_IsPublicized, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public boolean hasAllowMessagesFromMembers() {
        return contains(FIELD_AllowMessagesFromMembers);
    }

    /**
     * Remover for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public void removeAllowMessagesFromMembers() {
        remove(FIELD_AllowMessagesFromMembers);
    }

    /**
     * Getter for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public Boolean isAllowMessagesFromMembers(GetMode mode) {
        return obtainDirect(FIELD_AllowMessagesFromMembers, Boolean.class, mode);
    }

    /**
     * Getter for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public Boolean isAllowMessagesFromMembers() {
        return obtainDirect(FIELD_AllowMessagesFromMembers, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public ComplexKeyGroupMembership setAllowMessagesFromMembers(Boolean value, SetMode mode) {
        putDirect(FIELD_AllowMessagesFromMembers, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public ComplexKeyGroupMembership setAllowMessagesFromMembers(Boolean value) {
        putDirect(FIELD_AllowMessagesFromMembers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for allowMessagesFromMembers
     * 
     * @see ComplexKeyGroupMembership.Fields#allowMessagesFromMembers
     */
    public ComplexKeyGroupMembership setAllowMessagesFromMembers(boolean value) {
        putDirect(FIELD_AllowMessagesFromMembers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public boolean hasJoinedTime() {
        return contains(FIELD_JoinedTime);
    }

    /**
     * Remover for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public void removeJoinedTime() {
        remove(FIELD_JoinedTime);
    }

    /**
     * Getter for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public Long getJoinedTime(GetMode mode) {
        return obtainDirect(FIELD_JoinedTime, Long.class, mode);
    }

    /**
     * Getter for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public Long getJoinedTime() {
        return obtainDirect(FIELD_JoinedTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public ComplexKeyGroupMembership setJoinedTime(Long value, SetMode mode) {
        putDirect(FIELD_JoinedTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public ComplexKeyGroupMembership setJoinedTime(Long value) {
        putDirect(FIELD_JoinedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for joinedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#joinedTime
     */
    public ComplexKeyGroupMembership setJoinedTime(long value) {
        putDirect(FIELD_JoinedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public boolean hasResignedTime() {
        return contains(FIELD_ResignedTime);
    }

    /**
     * Remover for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public void removeResignedTime() {
        remove(FIELD_ResignedTime);
    }

    /**
     * Getter for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public Long getResignedTime(GetMode mode) {
        return obtainDirect(FIELD_ResignedTime, Long.class, mode);
    }

    /**
     * Getter for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public Long getResignedTime() {
        return obtainDirect(FIELD_ResignedTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public ComplexKeyGroupMembership setResignedTime(Long value, SetMode mode) {
        putDirect(FIELD_ResignedTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public ComplexKeyGroupMembership setResignedTime(Long value) {
        putDirect(FIELD_ResignedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for resignedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#resignedTime
     */
    public ComplexKeyGroupMembership setResignedTime(long value) {
        putDirect(FIELD_ResignedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public boolean hasLastModifiedStateTime() {
        return contains(FIELD_LastModifiedStateTime);
    }

    /**
     * Remover for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public void removeLastModifiedStateTime() {
        remove(FIELD_LastModifiedStateTime);
    }

    /**
     * Getter for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public Long getLastModifiedStateTime(GetMode mode) {
        return obtainDirect(FIELD_LastModifiedStateTime, Long.class, mode);
    }

    /**
     * Getter for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public Long getLastModifiedStateTime() {
        return obtainDirect(FIELD_LastModifiedStateTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public ComplexKeyGroupMembership setLastModifiedStateTime(Long value, SetMode mode) {
        putDirect(FIELD_LastModifiedStateTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public ComplexKeyGroupMembership setLastModifiedStateTime(Long value) {
        putDirect(FIELD_LastModifiedStateTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for lastModifiedStateTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedStateTime
     */
    public ComplexKeyGroupMembership setLastModifiedStateTime(long value) {
        putDirect(FIELD_LastModifiedStateTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for emailDigestFrequency
     * 
     * @see ComplexKeyGroupMembership.Fields#emailDigestFrequency
     */
    public boolean hasEmailDigestFrequency() {
        return contains(FIELD_EmailDigestFrequency);
    }

    /**
     * Remover for emailDigestFrequency
     * 
     * @see ComplexKeyGroupMembership.Fields#emailDigestFrequency
     */
    public void removeEmailDigestFrequency() {
        remove(FIELD_EmailDigestFrequency);
    }

    /**
     * Getter for emailDigestFrequency
     * 
     * @see ComplexKeyGroupMembership.Fields#emailDigestFrequency
     */
    public EmailDigestFrequency getEmailDigestFrequency(GetMode mode) {
        return obtainDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, mode);
    }

    /**
     * Getter for emailDigestFrequency
     * 
     * @see ComplexKeyGroupMembership.Fields#emailDigestFrequency
     */
    public EmailDigestFrequency getEmailDigestFrequency() {
        return obtainDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, GetMode.STRICT);
    }

    /**
     * Setter for emailDigestFrequency
     * 
     * @see ComplexKeyGroupMembership.Fields#emailDigestFrequency
     */
    public ComplexKeyGroupMembership setEmailDigestFrequency(EmailDigestFrequency value, SetMode mode) {
        putDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for emailDigestFrequency
     * 
     * @see ComplexKeyGroupMembership.Fields#emailDigestFrequency
     */
    public ComplexKeyGroupMembership setEmailDigestFrequency(EmailDigestFrequency value) {
        putDirect(FIELD_EmailDigestFrequency, EmailDigestFrequency.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public boolean hasCreationTime() {
        return contains(FIELD_CreationTime);
    }

    /**
     * Remover for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public void removeCreationTime() {
        remove(FIELD_CreationTime);
    }

    /**
     * Getter for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public Long getCreationTime(GetMode mode) {
        return obtainDirect(FIELD_CreationTime, Long.class, mode);
    }

    /**
     * Getter for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public Long getCreationTime() {
        return obtainDirect(FIELD_CreationTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public ComplexKeyGroupMembership setCreationTime(Long value, SetMode mode) {
        putDirect(FIELD_CreationTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public ComplexKeyGroupMembership setCreationTime(Long value) {
        putDirect(FIELD_CreationTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for creationTime
     * 
     * @see ComplexKeyGroupMembership.Fields#creationTime
     */
    public ComplexKeyGroupMembership setCreationTime(long value) {
        putDirect(FIELD_CreationTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public boolean hasLastModifiedTime() {
        return contains(FIELD_LastModifiedTime);
    }

    /**
     * Remover for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public void removeLastModifiedTime() {
        remove(FIELD_LastModifiedTime);
    }

    /**
     * Getter for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public Long getLastModifiedTime(GetMode mode) {
        return obtainDirect(FIELD_LastModifiedTime, Long.class, mode);
    }

    /**
     * Getter for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public Long getLastModifiedTime() {
        return obtainDirect(FIELD_LastModifiedTime, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public ComplexKeyGroupMembership setLastModifiedTime(Long value, SetMode mode) {
        putDirect(FIELD_LastModifiedTime, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public ComplexKeyGroupMembership setLastModifiedTime(Long value) {
        putDirect(FIELD_LastModifiedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for lastModifiedTime
     * 
     * @see ComplexKeyGroupMembership.Fields#lastModifiedTime
     */
    public ComplexKeyGroupMembership setLastModifiedTime(long value) {
        putDirect(FIELD_LastModifiedTime, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public boolean hasEmailAnnouncementsFromManagers() {
        return contains(FIELD_EmailAnnouncementsFromManagers);
    }

    /**
     * Remover for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public void removeEmailAnnouncementsFromManagers() {
        remove(FIELD_EmailAnnouncementsFromManagers);
    }

    /**
     * Getter for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public Boolean isEmailAnnouncementsFromManagers(GetMode mode) {
        return obtainDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, mode);
    }

    /**
     * Getter for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public Boolean isEmailAnnouncementsFromManagers() {
        return obtainDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public ComplexKeyGroupMembership setEmailAnnouncementsFromManagers(Boolean value, SetMode mode) {
        putDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public ComplexKeyGroupMembership setEmailAnnouncementsFromManagers(Boolean value) {
        putDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for emailAnnouncementsFromManagers
     * 
     * @see ComplexKeyGroupMembership.Fields#emailAnnouncementsFromManagers
     */
    public ComplexKeyGroupMembership setEmailAnnouncementsFromManagers(boolean value) {
        putDirect(FIELD_EmailAnnouncementsFromManagers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public boolean hasEmailForEveryNewPost() {
        return contains(FIELD_EmailForEveryNewPost);
    }

    /**
     * Remover for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public void removeEmailForEveryNewPost() {
        remove(FIELD_EmailForEveryNewPost);
    }

    /**
     * Getter for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public Boolean isEmailForEveryNewPost(GetMode mode) {
        return obtainDirect(FIELD_EmailForEveryNewPost, Boolean.class, mode);
    }

    /**
     * Getter for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public Boolean isEmailForEveryNewPost() {
        return obtainDirect(FIELD_EmailForEveryNewPost, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public ComplexKeyGroupMembership setEmailForEveryNewPost(Boolean value, SetMode mode) {
        putDirect(FIELD_EmailForEveryNewPost, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public ComplexKeyGroupMembership setEmailForEveryNewPost(Boolean value) {
        putDirect(FIELD_EmailForEveryNewPost, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for emailForEveryNewPost
     * 
     * @see ComplexKeyGroupMembership.Fields#emailForEveryNewPost
     */
    public ComplexKeyGroupMembership setEmailForEveryNewPost(boolean value) {
        putDirect(FIELD_EmailForEveryNewPost, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for writeLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#writeLevel
     */
    public boolean hasWriteLevel() {
        return contains(FIELD_WriteLevel);
    }

    /**
     * Remover for writeLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#writeLevel
     */
    public void removeWriteLevel() {
        remove(FIELD_WriteLevel);
    }

    /**
     * Getter for writeLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#writeLevel
     */
    public WriteLevel getWriteLevel(GetMode mode) {
        return obtainDirect(FIELD_WriteLevel, WriteLevel.class, mode);
    }

    /**
     * Getter for writeLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#writeLevel
     */
    public WriteLevel getWriteLevel() {
        return obtainDirect(FIELD_WriteLevel, WriteLevel.class, GetMode.STRICT);
    }

    /**
     * Setter for writeLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#writeLevel
     */
    public ComplexKeyGroupMembership setWriteLevel(WriteLevel value, SetMode mode) {
        putDirect(FIELD_WriteLevel, WriteLevel.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for writeLevel
     * 
     * @see ComplexKeyGroupMembership.Fields#writeLevel
     */
    public ComplexKeyGroupMembership setWriteLevel(WriteLevel value) {
        putDirect(FIELD_WriteLevel, WriteLevel.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for firstName
     * 
     * @see ComplexKeyGroupMembership.Fields#firstName
     */
    public boolean hasFirstName() {
        return contains(FIELD_FirstName);
    }

    /**
     * Remover for firstName
     * 
     * @see ComplexKeyGroupMembership.Fields#firstName
     */
    public void removeFirstName() {
        remove(FIELD_FirstName);
    }

    /**
     * Getter for firstName
     * 
     * @see ComplexKeyGroupMembership.Fields#firstName
     */
    public String getFirstName(GetMode mode) {
        return obtainDirect(FIELD_FirstName, String.class, mode);
    }

    /**
     * Getter for firstName
     * 
     * @see ComplexKeyGroupMembership.Fields#firstName
     */
    public String getFirstName() {
        return obtainDirect(FIELD_FirstName, String.class, GetMode.STRICT);
    }

    /**
     * Setter for firstName
     * 
     * @see ComplexKeyGroupMembership.Fields#firstName
     */
    public ComplexKeyGroupMembership setFirstName(String value, SetMode mode) {
        putDirect(FIELD_FirstName, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for firstName
     * 
     * @see ComplexKeyGroupMembership.Fields#firstName
     */
    public ComplexKeyGroupMembership setFirstName(String value) {
        putDirect(FIELD_FirstName, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastName
     * 
     * @see ComplexKeyGroupMembership.Fields#lastName
     */
    public boolean hasLastName() {
        return contains(FIELD_LastName);
    }

    /**
     * Remover for lastName
     * 
     * @see ComplexKeyGroupMembership.Fields#lastName
     */
    public void removeLastName() {
        remove(FIELD_LastName);
    }

    /**
     * Getter for lastName
     * 
     * @see ComplexKeyGroupMembership.Fields#lastName
     */
    public String getLastName(GetMode mode) {
        return obtainDirect(FIELD_LastName, String.class, mode);
    }

    /**
     * Getter for lastName
     * 
     * @see ComplexKeyGroupMembership.Fields#lastName
     */
    public String getLastName() {
        return obtainDirect(FIELD_LastName, String.class, GetMode.STRICT);
    }

    /**
     * Setter for lastName
     * 
     * @see ComplexKeyGroupMembership.Fields#lastName
     */
    public ComplexKeyGroupMembership setLastName(String value, SetMode mode) {
        putDirect(FIELD_LastName, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for lastName
     * 
     * @see ComplexKeyGroupMembership.Fields#lastName
     */
    public ComplexKeyGroupMembership setLastName(String value) {
        putDirect(FIELD_LastName, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public ComplexKeyGroupMembership clone()
        throws CloneNotSupportedException
    {
        return ((ComplexKeyGroupMembership) super.clone());
    }

    @Override
    public ComplexKeyGroupMembership copy()
        throws CloneNotSupportedException
    {
        return ((ComplexKeyGroupMembership) super.copy());
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
         * Complex key consisting of groupID and memberID
         * 
         */
        public com.linkedin.restli.examples.groups.api.GroupMembershipKey.Fields id() {
            return new com.linkedin.restli.examples.groups.api.GroupMembershipKey.Fields(getPathComponents(), "id");
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

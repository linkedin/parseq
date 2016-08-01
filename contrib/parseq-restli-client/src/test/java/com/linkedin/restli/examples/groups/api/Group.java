
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
import com.linkedin.data.template.StringArray;


/**
 * A Group record
 *
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/Group.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Group
    extends RecordTemplate
{

    private final static Group.Fields _fields = new Group.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"Group\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A Group record\",\"fields\":[{\"name\":\"id\",\"type\":\"int\",\"doc\":\"This field is read-only, and will be automatically assigned when the group is POSTed.\"},{\"name\":\"vanityUrl\",\"type\":\"string\"},{\"name\":\"parentGroupId\",\"type\":\"int\",\"doc\":\"Parent group references\"},{\"name\":\"name\",\"type\":\"string\",\"doc\":\"Cannot be changed by owner/managers once set (only CS can change it)\"},{\"name\":\"shortDescription\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"rules\",\"type\":\"string\"},{\"name\":\"contactEmail\",\"type\":\"string\"},{\"name\":\"category\",\"type\":\"int\"},{\"name\":\"otherCategory\",\"type\":\"int\"},{\"name\":\"badge\",\"type\":{\"type\":\"enum\",\"name\":\"Badge\",\"symbols\":[\"OFFICIAL\",\"SPONSORED\",\"FEATURED\",\"FOR_GOOD\",\"NONE\"]}},{\"name\":\"homeSiteUrl\",\"type\":\"string\"},{\"name\":\"smallLogoMediaUrl\",\"type\":\"string\"},{\"name\":\"largeLogoMediaUrl\",\"type\":\"string\"},{\"name\":\"location\",\"type\":{\"type\":\"record\",\"name\":\"Location\",\"doc\":\"A Location record. TODO HIGH This should be in common.linkedin\",\"fields\":[{\"name\":\"countryCode\",\"type\":\"string\"},{\"name\":\"postalCode\",\"type\":\"string\"},{\"name\":\"geoPostalCode\",\"type\":\"string\"},{\"name\":\"regionCode\",\"type\":\"int\"},{\"name\":\"latitude\",\"type\":\"float\"},{\"name\":\"longitude\",\"type\":\"float\"},{\"name\":\"geoPlaceCodes\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"gmtOffset\",\"type\":\"float\"},{\"name\":\"usesDaylightSavings\",\"type\":\"boolean\"}]},\"doc\":\"An inlined Location struct\"},{\"name\":\"locale\",\"type\":\"string\"},{\"name\":\"sharingKey\",\"type\":\"string\",\"doc\":\"System-generated, read-only\"},{\"name\":\"visibility\",\"type\":{\"type\":\"enum\",\"name\":\"Visibility\",\"symbols\":[\"PUBLIC\",\"PRIVATE\",\"HIDDEN\"]}},{\"name\":\"state\",\"type\":{\"type\":\"enum\",\"name\":\"State\",\"symbols\":[\"ACTIVE\",\"LOCKED\",\"INACTIVE\",\"PROPOSED\"]},\"default\":\"ACTIVE\"},{\"name\":\"createdTimestamp\",\"type\":\"long\",\"doc\":\"This field is read-only. TODO Timestamp representation\"},{\"name\":\"lastModifiedTimestamp\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"isOpenToNonMembers\",\"type\":\"boolean\"},{\"name\":\"approvalModes\",\"type\":\"int\",\"doc\":\"TODO This is really a bitset with each bit mapped to a setting enum. See ANetApprovalMode\"},{\"name\":\"contactability\",\"type\":{\"type\":\"enum\",\"name\":\"Contactability\",\"symbols\":[\"CONTACTABLE\",\"NON_CONTACTABLE\"]}},{\"name\":\"directoryPresence\",\"type\":{\"type\":\"enum\",\"name\":\"DirectoryPresence\",\"symbols\":[\"NONE\",\"LINKEDIN\",\"PUBLIC\"]}},{\"name\":\"hasMemberInvites\",\"type\":\"boolean\"},{\"name\":\"numIdentityChanges\",\"type\":\"int\",\"doc\":\"System-maintained, read-only\"},{\"name\":\"maxIdentityChanges\",\"type\":\"int\",\"doc\":\"CS-editable only\"},{\"name\":\"maxMembers\",\"type\":\"int\",\"doc\":\"CS-editable only\"},{\"name\":\"maxModerators\",\"type\":\"int\",\"doc\":\"CS-editable only\"},{\"name\":\"maxSubgroups\",\"type\":\"int\",\"doc\":\"CS-editable only\"},{\"name\":\"maxFeeds\",\"type\":\"int\",\"doc\":\"CS-editable only\"},{\"name\":\"hasEmailExport\",\"type\":\"boolean\"},{\"name\":\"categoriesEnabled\",\"type\":{\"type\":\"enum\",\"name\":\"PostCategory\",\"symbols\":[\"DISCUSSION\",\"JOB\",\"PROMOTION\"]}},{\"name\":\"hasNetworkUpdates\",\"type\":\"boolean\"},{\"name\":\"hasMemberRoster\",\"type\":\"boolean\"},{\"name\":\"hasSettings\",\"type\":\"boolean\"},{\"name\":\"hideSubgroups\",\"type\":\"boolean\"},{\"name\":\"categoriesForModeratorsOnly\",\"type\":\"PostCategory\"},{\"name\":\"numMemberFlagsToDelete\",\"type\":\"int\"},{\"name\":\"newsFormat\",\"type\":{\"type\":\"enum\",\"name\":\"NewsFormat\",\"symbols\":[\"RECENT\",\"CLUSTERED\"]}},{\"name\":\"preModeration\",\"type\":{\"type\":\"enum\",\"name\":\"PreModerationType\",\"symbols\":[\"NONE\",\"COMMENTS\",\"ALL\"]}},{\"name\":\"preModerationCategories\",\"type\":\"PostCategory\"},{\"name\":\"nonMemberPermissions\",\"type\":{\"type\":\"enum\",\"name\":\"NonMemberPermissions\",\"symbols\":[\"NONE\",\"READ_ONLY\",\"COMMENT_WITH_MODERATION\",\"COMMENT_AND_POST_WITH_MODERATION\",\"COMMENT_NO_MODERATION_POST_MODERATION\"]}},{\"name\":\"openedToNonMembersTimestamp\",\"type\":\"long\"},{\"name\":\"preModerateNewMembersPeriodInDays\",\"type\":\"int\"},{\"name\":\"preModerateMembersWithLowConnections\",\"type\":\"boolean\"},{\"name\":\"preApprovedEmailDomains\",\"type\":{\"type\":\"array\",\"items\":\"string\"}},{\"name\":\"owner\",\"type\":{\"type\":\"record\",\"name\":\"GroupMembership\",\"doc\":\"A GroupMembership entity\",\"fields\":[{\"name\":\"id\",\"type\":\"string\",\"doc\":\"Compound key of groupID and memberID\"},{\"name\":\"memberID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"},{\"name\":\"groupID\",\"type\":\"int\",\"doc\":\"This field is read-only.\"},{\"name\":\"membershipLevel\",\"type\":{\"type\":\"enum\",\"name\":\"MembershipLevel\",\"symbols\":[\"BLOCKED\",\"NON_MEMBER\",\"REQUESTING_TO_JOIN\",\"MEMBER\",\"MODERATOR\",\"MANAGER\",\"OWNER\"]}},{\"name\":\"contactEmail\",\"type\":\"string\"},{\"name\":\"isPublicized\",\"type\":\"boolean\"},{\"name\":\"allowMessagesFromMembers\",\"type\":\"boolean\"},{\"name\":\"joinedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"resignedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"lastModifiedStateTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"emailDigestFrequency\",\"type\":{\"type\":\"enum\",\"name\":\"EmailDigestFrequency\",\"symbols\":[\"NONE\",\"DAILY\",\"WEEKLY\"]}},{\"name\":\"creationTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"lastModifiedTime\",\"type\":\"long\",\"doc\":\"This field is read-only.\"},{\"name\":\"emailAnnouncementsFromManagers\",\"type\":\"boolean\"},{\"name\":\"emailForEveryNewPost\",\"type\":\"boolean\"},{\"name\":\"writeLevel\",\"type\":{\"type\":\"enum\",\"name\":\"WriteLevel\",\"symbols\":[\"NONE\",\"PREMODERATED\",\"DEFAULT\",\"FULL\"]},\"doc\":\"This field can only be accessed by moderators of the group\"},{\"name\":\"firstName\",\"type\":\"string\",\"doc\":\"Denormalized from members\"},{\"name\":\"lastName\",\"type\":\"string\",\"doc\":\"Denormalized from members\"}]},\"doc\":\"Required when creating a group, not returned as part of the default representation (must be explicitly requested via 'fields'\"}]}"));
    private final static RecordDataSchema.Field FIELD_Id = SCHEMA.getField("id");
    private final static RecordDataSchema.Field FIELD_VanityUrl = SCHEMA.getField("vanityUrl");
    private final static RecordDataSchema.Field FIELD_ParentGroupId = SCHEMA.getField("parentGroupId");
    private final static RecordDataSchema.Field FIELD_Name = SCHEMA.getField("name");
    private final static RecordDataSchema.Field FIELD_ShortDescription = SCHEMA.getField("shortDescription");
    private final static RecordDataSchema.Field FIELD_Description = SCHEMA.getField("description");
    private final static RecordDataSchema.Field FIELD_Rules = SCHEMA.getField("rules");
    private final static RecordDataSchema.Field FIELD_ContactEmail = SCHEMA.getField("contactEmail");
    private final static RecordDataSchema.Field FIELD_Category = SCHEMA.getField("category");
    private final static RecordDataSchema.Field FIELD_OtherCategory = SCHEMA.getField("otherCategory");
    private final static RecordDataSchema.Field FIELD_Badge = SCHEMA.getField("badge");
    private final static RecordDataSchema.Field FIELD_HomeSiteUrl = SCHEMA.getField("homeSiteUrl");
    private final static RecordDataSchema.Field FIELD_SmallLogoMediaUrl = SCHEMA.getField("smallLogoMediaUrl");
    private final static RecordDataSchema.Field FIELD_LargeLogoMediaUrl = SCHEMA.getField("largeLogoMediaUrl");
    private final static RecordDataSchema.Field FIELD_Location = SCHEMA.getField("location");
    private final static RecordDataSchema.Field FIELD_Locale = SCHEMA.getField("locale");
    private final static RecordDataSchema.Field FIELD_SharingKey = SCHEMA.getField("sharingKey");
    private final static RecordDataSchema.Field FIELD_Visibility = SCHEMA.getField("visibility");
    private final static RecordDataSchema.Field FIELD_State = SCHEMA.getField("state");
    private final static RecordDataSchema.Field FIELD_CreatedTimestamp = SCHEMA.getField("createdTimestamp");
    private final static RecordDataSchema.Field FIELD_LastModifiedTimestamp = SCHEMA.getField("lastModifiedTimestamp");
    private final static RecordDataSchema.Field FIELD_IsOpenToNonMembers = SCHEMA.getField("isOpenToNonMembers");
    private final static RecordDataSchema.Field FIELD_ApprovalModes = SCHEMA.getField("approvalModes");
    private final static RecordDataSchema.Field FIELD_Contactability = SCHEMA.getField("contactability");
    private final static RecordDataSchema.Field FIELD_DirectoryPresence = SCHEMA.getField("directoryPresence");
    private final static RecordDataSchema.Field FIELD_HasMemberInvites = SCHEMA.getField("hasMemberInvites");
    private final static RecordDataSchema.Field FIELD_NumIdentityChanges = SCHEMA.getField("numIdentityChanges");
    private final static RecordDataSchema.Field FIELD_MaxIdentityChanges = SCHEMA.getField("maxIdentityChanges");
    private final static RecordDataSchema.Field FIELD_MaxMembers = SCHEMA.getField("maxMembers");
    private final static RecordDataSchema.Field FIELD_MaxModerators = SCHEMA.getField("maxModerators");
    private final static RecordDataSchema.Field FIELD_MaxSubgroups = SCHEMA.getField("maxSubgroups");
    private final static RecordDataSchema.Field FIELD_MaxFeeds = SCHEMA.getField("maxFeeds");
    private final static RecordDataSchema.Field FIELD_HasEmailExport = SCHEMA.getField("hasEmailExport");
    private final static RecordDataSchema.Field FIELD_CategoriesEnabled = SCHEMA.getField("categoriesEnabled");
    private final static RecordDataSchema.Field FIELD_HasNetworkUpdates = SCHEMA.getField("hasNetworkUpdates");
    private final static RecordDataSchema.Field FIELD_HasMemberRoster = SCHEMA.getField("hasMemberRoster");
    private final static RecordDataSchema.Field FIELD_HasSettings = SCHEMA.getField("hasSettings");
    private final static RecordDataSchema.Field FIELD_HideSubgroups = SCHEMA.getField("hideSubgroups");
    private final static RecordDataSchema.Field FIELD_CategoriesForModeratorsOnly = SCHEMA.getField("categoriesForModeratorsOnly");
    private final static RecordDataSchema.Field FIELD_NumMemberFlagsToDelete = SCHEMA.getField("numMemberFlagsToDelete");
    private final static RecordDataSchema.Field FIELD_NewsFormat = SCHEMA.getField("newsFormat");
    private final static RecordDataSchema.Field FIELD_PreModeration = SCHEMA.getField("preModeration");
    private final static RecordDataSchema.Field FIELD_PreModerationCategories = SCHEMA.getField("preModerationCategories");
    private final static RecordDataSchema.Field FIELD_NonMemberPermissions = SCHEMA.getField("nonMemberPermissions");
    private final static RecordDataSchema.Field FIELD_OpenedToNonMembersTimestamp = SCHEMA.getField("openedToNonMembersTimestamp");
    private final static RecordDataSchema.Field FIELD_PreModerateNewMembersPeriodInDays = SCHEMA.getField("preModerateNewMembersPeriodInDays");
    private final static RecordDataSchema.Field FIELD_PreModerateMembersWithLowConnections = SCHEMA.getField("preModerateMembersWithLowConnections");
    private final static RecordDataSchema.Field FIELD_PreApprovedEmailDomains = SCHEMA.getField("preApprovedEmailDomains");
    private final static RecordDataSchema.Field FIELD_Owner = SCHEMA.getField("owner");

    public Group() {
        super(new DataMap(), SCHEMA);
    }

    public Group(DataMap data) {
        super(data, SCHEMA);
    }

    public static Group.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for id
     *
     * @see Group.Fields#id
     */
    public boolean hasId() {
        return contains(FIELD_Id);
    }

    /**
     * Remover for id
     *
     * @see Group.Fields#id
     */
    public void removeId() {
        remove(FIELD_Id);
    }

    /**
     * Getter for id
     *
     * @see Group.Fields#id
     */
    public Integer getId(GetMode mode) {
        return obtainDirect(FIELD_Id, Integer.class, mode);
    }

    /**
     * Getter for id
     *
     * @see Group.Fields#id
     */
    public Integer getId() {
        return obtainDirect(FIELD_Id, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for id
     *
     * @see Group.Fields#id
     */
    public Group setId(Integer value, SetMode mode) {
        putDirect(FIELD_Id, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for id
     *
     * @see Group.Fields#id
     */
    public Group setId(Integer value) {
        putDirect(FIELD_Id, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for id
     *
     * @see Group.Fields#id
     */
    public Group setId(int value) {
        putDirect(FIELD_Id, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for vanityUrl
     *
     * @see Group.Fields#vanityUrl
     */
    public boolean hasVanityUrl() {
        return contains(FIELD_VanityUrl);
    }

    /**
     * Remover for vanityUrl
     *
     * @see Group.Fields#vanityUrl
     */
    public void removeVanityUrl() {
        remove(FIELD_VanityUrl);
    }

    /**
     * Getter for vanityUrl
     *
     * @see Group.Fields#vanityUrl
     */
    public String getVanityUrl(GetMode mode) {
        return obtainDirect(FIELD_VanityUrl, String.class, mode);
    }

    /**
     * Getter for vanityUrl
     *
     * @see Group.Fields#vanityUrl
     */
    public String getVanityUrl() {
        return obtainDirect(FIELD_VanityUrl, String.class, GetMode.STRICT);
    }

    /**
     * Setter for vanityUrl
     *
     * @see Group.Fields#vanityUrl
     */
    public Group setVanityUrl(String value, SetMode mode) {
        putDirect(FIELD_VanityUrl, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for vanityUrl
     *
     * @see Group.Fields#vanityUrl
     */
    public Group setVanityUrl(String value) {
        putDirect(FIELD_VanityUrl, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public boolean hasParentGroupId() {
        return contains(FIELD_ParentGroupId);
    }

    /**
     * Remover for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public void removeParentGroupId() {
        remove(FIELD_ParentGroupId);
    }

    /**
     * Getter for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public Integer getParentGroupId(GetMode mode) {
        return obtainDirect(FIELD_ParentGroupId, Integer.class, mode);
    }

    /**
     * Getter for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public Integer getParentGroupId() {
        return obtainDirect(FIELD_ParentGroupId, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public Group setParentGroupId(Integer value, SetMode mode) {
        putDirect(FIELD_ParentGroupId, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public Group setParentGroupId(Integer value) {
        putDirect(FIELD_ParentGroupId, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for parentGroupId
     *
     * @see Group.Fields#parentGroupId
     */
    public Group setParentGroupId(int value) {
        putDirect(FIELD_ParentGroupId, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for name
     *
     * @see Group.Fields#name
     */
    public boolean hasName() {
        return contains(FIELD_Name);
    }

    /**
     * Remover for name
     *
     * @see Group.Fields#name
     */
    public void removeName() {
        remove(FIELD_Name);
    }

    /**
     * Getter for name
     *
     * @see Group.Fields#name
     */
    public String getName(GetMode mode) {
        return obtainDirect(FIELD_Name, String.class, mode);
    }

    /**
     * Getter for name
     *
     * @see Group.Fields#name
     */
    public String getName() {
        return obtainDirect(FIELD_Name, String.class, GetMode.STRICT);
    }

    /**
     * Setter for name
     *
     * @see Group.Fields#name
     */
    public Group setName(String value, SetMode mode) {
        putDirect(FIELD_Name, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for name
     *
     * @see Group.Fields#name
     */
    public Group setName(String value) {
        putDirect(FIELD_Name, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for shortDescription
     *
     * @see Group.Fields#shortDescription
     */
    public boolean hasShortDescription() {
        return contains(FIELD_ShortDescription);
    }

    /**
     * Remover for shortDescription
     *
     * @see Group.Fields#shortDescription
     */
    public void removeShortDescription() {
        remove(FIELD_ShortDescription);
    }

    /**
     * Getter for shortDescription
     *
     * @see Group.Fields#shortDescription
     */
    public String getShortDescription(GetMode mode) {
        return obtainDirect(FIELD_ShortDescription, String.class, mode);
    }

    /**
     * Getter for shortDescription
     *
     * @see Group.Fields#shortDescription
     */
    public String getShortDescription() {
        return obtainDirect(FIELD_ShortDescription, String.class, GetMode.STRICT);
    }

    /**
     * Setter for shortDescription
     *
     * @see Group.Fields#shortDescription
     */
    public Group setShortDescription(String value, SetMode mode) {
        putDirect(FIELD_ShortDescription, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for shortDescription
     *
     * @see Group.Fields#shortDescription
     */
    public Group setShortDescription(String value) {
        putDirect(FIELD_ShortDescription, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for description
     *
     * @see Group.Fields#description
     */
    public boolean hasDescription() {
        return contains(FIELD_Description);
    }

    /**
     * Remover for description
     *
     * @see Group.Fields#description
     */
    public void removeDescription() {
        remove(FIELD_Description);
    }

    /**
     * Getter for description
     *
     * @see Group.Fields#description
     */
    public String getDescription(GetMode mode) {
        return obtainDirect(FIELD_Description, String.class, mode);
    }

    /**
     * Getter for description
     *
     * @see Group.Fields#description
     */
    public String getDescription() {
        return obtainDirect(FIELD_Description, String.class, GetMode.STRICT);
    }

    /**
     * Setter for description
     *
     * @see Group.Fields#description
     */
    public Group setDescription(String value, SetMode mode) {
        putDirect(FIELD_Description, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for description
     *
     * @see Group.Fields#description
     */
    public Group setDescription(String value) {
        putDirect(FIELD_Description, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for rules
     *
     * @see Group.Fields#rules
     */
    public boolean hasRules() {
        return contains(FIELD_Rules);
    }

    /**
     * Remover for rules
     *
     * @see Group.Fields#rules
     */
    public void removeRules() {
        remove(FIELD_Rules);
    }

    /**
     * Getter for rules
     *
     * @see Group.Fields#rules
     */
    public String getRules(GetMode mode) {
        return obtainDirect(FIELD_Rules, String.class, mode);
    }

    /**
     * Getter for rules
     *
     * @see Group.Fields#rules
     */
    public String getRules() {
        return obtainDirect(FIELD_Rules, String.class, GetMode.STRICT);
    }

    /**
     * Setter for rules
     *
     * @see Group.Fields#rules
     */
    public Group setRules(String value, SetMode mode) {
        putDirect(FIELD_Rules, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for rules
     *
     * @see Group.Fields#rules
     */
    public Group setRules(String value) {
        putDirect(FIELD_Rules, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for contactEmail
     *
     * @see Group.Fields#contactEmail
     */
    public boolean hasContactEmail() {
        return contains(FIELD_ContactEmail);
    }

    /**
     * Remover for contactEmail
     *
     * @see Group.Fields#contactEmail
     */
    public void removeContactEmail() {
        remove(FIELD_ContactEmail);
    }

    /**
     * Getter for contactEmail
     *
     * @see Group.Fields#contactEmail
     */
    public String getContactEmail(GetMode mode) {
        return obtainDirect(FIELD_ContactEmail, String.class, mode);
    }

    /**
     * Getter for contactEmail
     *
     * @see Group.Fields#contactEmail
     */
    public String getContactEmail() {
        return obtainDirect(FIELD_ContactEmail, String.class, GetMode.STRICT);
    }

    /**
     * Setter for contactEmail
     *
     * @see Group.Fields#contactEmail
     */
    public Group setContactEmail(String value, SetMode mode) {
        putDirect(FIELD_ContactEmail, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for contactEmail
     *
     * @see Group.Fields#contactEmail
     */
    public Group setContactEmail(String value) {
        putDirect(FIELD_ContactEmail, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for category
     *
     * @see Group.Fields#category
     */
    public boolean hasCategory() {
        return contains(FIELD_Category);
    }

    /**
     * Remover for category
     *
     * @see Group.Fields#category
     */
    public void removeCategory() {
        remove(FIELD_Category);
    }

    /**
     * Getter for category
     *
     * @see Group.Fields#category
     */
    public Integer getCategory(GetMode mode) {
        return obtainDirect(FIELD_Category, Integer.class, mode);
    }

    /**
     * Getter for category
     *
     * @see Group.Fields#category
     */
    public Integer getCategory() {
        return obtainDirect(FIELD_Category, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for category
     *
     * @see Group.Fields#category
     */
    public Group setCategory(Integer value, SetMode mode) {
        putDirect(FIELD_Category, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for category
     *
     * @see Group.Fields#category
     */
    public Group setCategory(Integer value) {
        putDirect(FIELD_Category, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for category
     *
     * @see Group.Fields#category
     */
    public Group setCategory(int value) {
        putDirect(FIELD_Category, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public boolean hasOtherCategory() {
        return contains(FIELD_OtherCategory);
    }

    /**
     * Remover for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public void removeOtherCategory() {
        remove(FIELD_OtherCategory);
    }

    /**
     * Getter for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public Integer getOtherCategory(GetMode mode) {
        return obtainDirect(FIELD_OtherCategory, Integer.class, mode);
    }

    /**
     * Getter for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public Integer getOtherCategory() {
        return obtainDirect(FIELD_OtherCategory, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public Group setOtherCategory(Integer value, SetMode mode) {
        putDirect(FIELD_OtherCategory, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public Group setOtherCategory(Integer value) {
        putDirect(FIELD_OtherCategory, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for otherCategory
     *
     * @see Group.Fields#otherCategory
     */
    public Group setOtherCategory(int value) {
        putDirect(FIELD_OtherCategory, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for badge
     *
     * @see Group.Fields#badge
     */
    public boolean hasBadge() {
        return contains(FIELD_Badge);
    }

    /**
     * Remover for badge
     *
     * @see Group.Fields#badge
     */
    public void removeBadge() {
        remove(FIELD_Badge);
    }

    /**
     * Getter for badge
     *
     * @see Group.Fields#badge
     */
    public Badge getBadge(GetMode mode) {
        return obtainDirect(FIELD_Badge, Badge.class, mode);
    }

    /**
     * Getter for badge
     *
     * @see Group.Fields#badge
     */
    public Badge getBadge() {
        return obtainDirect(FIELD_Badge, Badge.class, GetMode.STRICT);
    }

    /**
     * Setter for badge
     *
     * @see Group.Fields#badge
     */
    public Group setBadge(Badge value, SetMode mode) {
        putDirect(FIELD_Badge, Badge.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for badge
     *
     * @see Group.Fields#badge
     */
    public Group setBadge(Badge value) {
        putDirect(FIELD_Badge, Badge.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for homeSiteUrl
     *
     * @see Group.Fields#homeSiteUrl
     */
    public boolean hasHomeSiteUrl() {
        return contains(FIELD_HomeSiteUrl);
    }

    /**
     * Remover for homeSiteUrl
     *
     * @see Group.Fields#homeSiteUrl
     */
    public void removeHomeSiteUrl() {
        remove(FIELD_HomeSiteUrl);
    }

    /**
     * Getter for homeSiteUrl
     *
     * @see Group.Fields#homeSiteUrl
     */
    public String getHomeSiteUrl(GetMode mode) {
        return obtainDirect(FIELD_HomeSiteUrl, String.class, mode);
    }

    /**
     * Getter for homeSiteUrl
     *
     * @see Group.Fields#homeSiteUrl
     */
    public String getHomeSiteUrl() {
        return obtainDirect(FIELD_HomeSiteUrl, String.class, GetMode.STRICT);
    }

    /**
     * Setter for homeSiteUrl
     *
     * @see Group.Fields#homeSiteUrl
     */
    public Group setHomeSiteUrl(String value, SetMode mode) {
        putDirect(FIELD_HomeSiteUrl, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for homeSiteUrl
     *
     * @see Group.Fields#homeSiteUrl
     */
    public Group setHomeSiteUrl(String value) {
        putDirect(FIELD_HomeSiteUrl, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for smallLogoMediaUrl
     *
     * @see Group.Fields#smallLogoMediaUrl
     */
    public boolean hasSmallLogoMediaUrl() {
        return contains(FIELD_SmallLogoMediaUrl);
    }

    /**
     * Remover for smallLogoMediaUrl
     *
     * @see Group.Fields#smallLogoMediaUrl
     */
    public void removeSmallLogoMediaUrl() {
        remove(FIELD_SmallLogoMediaUrl);
    }

    /**
     * Getter for smallLogoMediaUrl
     *
     * @see Group.Fields#smallLogoMediaUrl
     */
    public String getSmallLogoMediaUrl(GetMode mode) {
        return obtainDirect(FIELD_SmallLogoMediaUrl, String.class, mode);
    }

    /**
     * Getter for smallLogoMediaUrl
     *
     * @see Group.Fields#smallLogoMediaUrl
     */
    public String getSmallLogoMediaUrl() {
        return obtainDirect(FIELD_SmallLogoMediaUrl, String.class, GetMode.STRICT);
    }

    /**
     * Setter for smallLogoMediaUrl
     *
     * @see Group.Fields#smallLogoMediaUrl
     */
    public Group setSmallLogoMediaUrl(String value, SetMode mode) {
        putDirect(FIELD_SmallLogoMediaUrl, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for smallLogoMediaUrl
     *
     * @see Group.Fields#smallLogoMediaUrl
     */
    public Group setSmallLogoMediaUrl(String value) {
        putDirect(FIELD_SmallLogoMediaUrl, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for largeLogoMediaUrl
     *
     * @see Group.Fields#largeLogoMediaUrl
     */
    public boolean hasLargeLogoMediaUrl() {
        return contains(FIELD_LargeLogoMediaUrl);
    }

    /**
     * Remover for largeLogoMediaUrl
     *
     * @see Group.Fields#largeLogoMediaUrl
     */
    public void removeLargeLogoMediaUrl() {
        remove(FIELD_LargeLogoMediaUrl);
    }

    /**
     * Getter for largeLogoMediaUrl
     *
     * @see Group.Fields#largeLogoMediaUrl
     */
    public String getLargeLogoMediaUrl(GetMode mode) {
        return obtainDirect(FIELD_LargeLogoMediaUrl, String.class, mode);
    }

    /**
     * Getter for largeLogoMediaUrl
     *
     * @see Group.Fields#largeLogoMediaUrl
     */
    public String getLargeLogoMediaUrl() {
        return obtainDirect(FIELD_LargeLogoMediaUrl, String.class, GetMode.STRICT);
    }

    /**
     * Setter for largeLogoMediaUrl
     *
     * @see Group.Fields#largeLogoMediaUrl
     */
    public Group setLargeLogoMediaUrl(String value, SetMode mode) {
        putDirect(FIELD_LargeLogoMediaUrl, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for largeLogoMediaUrl
     *
     * @see Group.Fields#largeLogoMediaUrl
     */
    public Group setLargeLogoMediaUrl(String value) {
        putDirect(FIELD_LargeLogoMediaUrl, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for location
     *
     * @see Group.Fields#location
     */
    public boolean hasLocation() {
        return contains(FIELD_Location);
    }

    /**
     * Remover for location
     *
     * @see Group.Fields#location
     */
    public void removeLocation() {
        remove(FIELD_Location);
    }

    /**
     * Getter for location
     *
     * @see Group.Fields#location
     */
    public Location getLocation(GetMode mode) {
        return obtainWrapped(FIELD_Location, Location.class, mode);
    }

    /**
     * Getter for location
     *
     * @see Group.Fields#location
     */
    public Location getLocation() {
        return obtainWrapped(FIELD_Location, Location.class, GetMode.STRICT);
    }

    /**
     * Setter for location
     *
     * @see Group.Fields#location
     */
    public Group setLocation(Location value, SetMode mode) {
        putWrapped(FIELD_Location, Location.class, value, mode);
        return this;
    }

    /**
     * Setter for location
     *
     * @see Group.Fields#location
     */
    public Group setLocation(Location value) {
        putWrapped(FIELD_Location, Location.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for locale
     *
     * @see Group.Fields#locale
     */
    public boolean hasLocale() {
        return contains(FIELD_Locale);
    }

    /**
     * Remover for locale
     *
     * @see Group.Fields#locale
     */
    public void removeLocale() {
        remove(FIELD_Locale);
    }

    /**
     * Getter for locale
     *
     * @see Group.Fields#locale
     */
    public String getLocale(GetMode mode) {
        return obtainDirect(FIELD_Locale, String.class, mode);
    }

    /**
     * Getter for locale
     *
     * @see Group.Fields#locale
     */
    public String getLocale() {
        return obtainDirect(FIELD_Locale, String.class, GetMode.STRICT);
    }

    /**
     * Setter for locale
     *
     * @see Group.Fields#locale
     */
    public Group setLocale(String value, SetMode mode) {
        putDirect(FIELD_Locale, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for locale
     *
     * @see Group.Fields#locale
     */
    public Group setLocale(String value) {
        putDirect(FIELD_Locale, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for sharingKey
     *
     * @see Group.Fields#sharingKey
     */
    public boolean hasSharingKey() {
        return contains(FIELD_SharingKey);
    }

    /**
     * Remover for sharingKey
     *
     * @see Group.Fields#sharingKey
     */
    public void removeSharingKey() {
        remove(FIELD_SharingKey);
    }

    /**
     * Getter for sharingKey
     *
     * @see Group.Fields#sharingKey
     */
    public String getSharingKey(GetMode mode) {
        return obtainDirect(FIELD_SharingKey, String.class, mode);
    }

    /**
     * Getter for sharingKey
     *
     * @see Group.Fields#sharingKey
     */
    public String getSharingKey() {
        return obtainDirect(FIELD_SharingKey, String.class, GetMode.STRICT);
    }

    /**
     * Setter for sharingKey
     *
     * @see Group.Fields#sharingKey
     */
    public Group setSharingKey(String value, SetMode mode) {
        putDirect(FIELD_SharingKey, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for sharingKey
     *
     * @see Group.Fields#sharingKey
     */
    public Group setSharingKey(String value) {
        putDirect(FIELD_SharingKey, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for visibility
     *
     * @see Group.Fields#visibility
     */
    public boolean hasVisibility() {
        return contains(FIELD_Visibility);
    }

    /**
     * Remover for visibility
     *
     * @see Group.Fields#visibility
     */
    public void removeVisibility() {
        remove(FIELD_Visibility);
    }

    /**
     * Getter for visibility
     *
     * @see Group.Fields#visibility
     */
    public Visibility getVisibility(GetMode mode) {
        return obtainDirect(FIELD_Visibility, Visibility.class, mode);
    }

    /**
     * Getter for visibility
     *
     * @see Group.Fields#visibility
     */
    public Visibility getVisibility() {
        return obtainDirect(FIELD_Visibility, Visibility.class, GetMode.STRICT);
    }

    /**
     * Setter for visibility
     *
     * @see Group.Fields#visibility
     */
    public Group setVisibility(Visibility value, SetMode mode) {
        putDirect(FIELD_Visibility, Visibility.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for visibility
     *
     * @see Group.Fields#visibility
     */
    public Group setVisibility(Visibility value) {
        putDirect(FIELD_Visibility, Visibility.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for state
     *
     * @see Group.Fields#state
     */
    public boolean hasState() {
        return contains(FIELD_State);
    }

    /**
     * Remover for state
     *
     * @see Group.Fields#state
     */
    public void removeState() {
        remove(FIELD_State);
    }

    /**
     * Getter for state
     *
     * @see Group.Fields#state
     */
    public State getState(GetMode mode) {
        return obtainDirect(FIELD_State, State.class, mode);
    }

    /**
     * Getter for state
     *
     * @see Group.Fields#state
     */
    public State getState() {
        return obtainDirect(FIELD_State, State.class, GetMode.STRICT);
    }

    /**
     * Setter for state
     *
     * @see Group.Fields#state
     */
    public Group setState(State value, SetMode mode) {
        putDirect(FIELD_State, State.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for state
     *
     * @see Group.Fields#state
     */
    public Group setState(State value) {
        putDirect(FIELD_State, State.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public boolean hasCreatedTimestamp() {
        return contains(FIELD_CreatedTimestamp);
    }

    /**
     * Remover for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public void removeCreatedTimestamp() {
        remove(FIELD_CreatedTimestamp);
    }

    /**
     * Getter for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public Long getCreatedTimestamp(GetMode mode) {
        return obtainDirect(FIELD_CreatedTimestamp, Long.class, mode);
    }

    /**
     * Getter for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public Long getCreatedTimestamp() {
        return obtainDirect(FIELD_CreatedTimestamp, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public Group setCreatedTimestamp(Long value, SetMode mode) {
        putDirect(FIELD_CreatedTimestamp, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public Group setCreatedTimestamp(Long value) {
        putDirect(FIELD_CreatedTimestamp, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for createdTimestamp
     *
     * @see Group.Fields#createdTimestamp
     */
    public Group setCreatedTimestamp(long value) {
        putDirect(FIELD_CreatedTimestamp, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public boolean hasLastModifiedTimestamp() {
        return contains(FIELD_LastModifiedTimestamp);
    }

    /**
     * Remover for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public void removeLastModifiedTimestamp() {
        remove(FIELD_LastModifiedTimestamp);
    }

    /**
     * Getter for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public Long getLastModifiedTimestamp(GetMode mode) {
        return obtainDirect(FIELD_LastModifiedTimestamp, Long.class, mode);
    }

    /**
     * Getter for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public Long getLastModifiedTimestamp() {
        return obtainDirect(FIELD_LastModifiedTimestamp, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public Group setLastModifiedTimestamp(Long value, SetMode mode) {
        putDirect(FIELD_LastModifiedTimestamp, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public Group setLastModifiedTimestamp(Long value) {
        putDirect(FIELD_LastModifiedTimestamp, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for lastModifiedTimestamp
     *
     * @see Group.Fields#lastModifiedTimestamp
     */
    public Group setLastModifiedTimestamp(long value) {
        putDirect(FIELD_LastModifiedTimestamp, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public boolean hasIsOpenToNonMembers() {
        return contains(FIELD_IsOpenToNonMembers);
    }

    /**
     * Remover for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public void removeIsOpenToNonMembers() {
        remove(FIELD_IsOpenToNonMembers);
    }

    /**
     * Getter for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public Boolean isIsOpenToNonMembers(GetMode mode) {
        return obtainDirect(FIELD_IsOpenToNonMembers, Boolean.class, mode);
    }

    /**
     * Getter for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public Boolean isIsOpenToNonMembers() {
        return obtainDirect(FIELD_IsOpenToNonMembers, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public Group setIsOpenToNonMembers(Boolean value, SetMode mode) {
        putDirect(FIELD_IsOpenToNonMembers, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public Group setIsOpenToNonMembers(Boolean value) {
        putDirect(FIELD_IsOpenToNonMembers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for isOpenToNonMembers
     *
     * @see Group.Fields#isOpenToNonMembers
     */
    public Group setIsOpenToNonMembers(boolean value) {
        putDirect(FIELD_IsOpenToNonMembers, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public boolean hasApprovalModes() {
        return contains(FIELD_ApprovalModes);
    }

    /**
     * Remover for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public void removeApprovalModes() {
        remove(FIELD_ApprovalModes);
    }

    /**
     * Getter for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public Integer getApprovalModes(GetMode mode) {
        return obtainDirect(FIELD_ApprovalModes, Integer.class, mode);
    }

    /**
     * Getter for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public Integer getApprovalModes() {
        return obtainDirect(FIELD_ApprovalModes, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public Group setApprovalModes(Integer value, SetMode mode) {
        putDirect(FIELD_ApprovalModes, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public Group setApprovalModes(Integer value) {
        putDirect(FIELD_ApprovalModes, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for approvalModes
     *
     * @see Group.Fields#approvalModes
     */
    public Group setApprovalModes(int value) {
        putDirect(FIELD_ApprovalModes, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for contactability
     *
     * @see Group.Fields#contactability
     */
    public boolean hasContactability() {
        return contains(FIELD_Contactability);
    }

    /**
     * Remover for contactability
     *
     * @see Group.Fields#contactability
     */
    public void removeContactability() {
        remove(FIELD_Contactability);
    }

    /**
     * Getter for contactability
     *
     * @see Group.Fields#contactability
     */
    public Contactability getContactability(GetMode mode) {
        return obtainDirect(FIELD_Contactability, Contactability.class, mode);
    }

    /**
     * Getter for contactability
     *
     * @see Group.Fields#contactability
     */
    public Contactability getContactability() {
        return obtainDirect(FIELD_Contactability, Contactability.class, GetMode.STRICT);
    }

    /**
     * Setter for contactability
     *
     * @see Group.Fields#contactability
     */
    public Group setContactability(Contactability value, SetMode mode) {
        putDirect(FIELD_Contactability, Contactability.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for contactability
     *
     * @see Group.Fields#contactability
     */
    public Group setContactability(Contactability value) {
        putDirect(FIELD_Contactability, Contactability.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for directoryPresence
     *
     * @see Group.Fields#directoryPresence
     */
    public boolean hasDirectoryPresence() {
        return contains(FIELD_DirectoryPresence);
    }

    /**
     * Remover for directoryPresence
     *
     * @see Group.Fields#directoryPresence
     */
    public void removeDirectoryPresence() {
        remove(FIELD_DirectoryPresence);
    }

    /**
     * Getter for directoryPresence
     *
     * @see Group.Fields#directoryPresence
     */
    public DirectoryPresence getDirectoryPresence(GetMode mode) {
        return obtainDirect(FIELD_DirectoryPresence, DirectoryPresence.class, mode);
    }

    /**
     * Getter for directoryPresence
     *
     * @see Group.Fields#directoryPresence
     */
    public DirectoryPresence getDirectoryPresence() {
        return obtainDirect(FIELD_DirectoryPresence, DirectoryPresence.class, GetMode.STRICT);
    }

    /**
     * Setter for directoryPresence
     *
     * @see Group.Fields#directoryPresence
     */
    public Group setDirectoryPresence(DirectoryPresence value, SetMode mode) {
        putDirect(FIELD_DirectoryPresence, DirectoryPresence.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for directoryPresence
     *
     * @see Group.Fields#directoryPresence
     */
    public Group setDirectoryPresence(DirectoryPresence value) {
        putDirect(FIELD_DirectoryPresence, DirectoryPresence.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public boolean hasHasMemberInvites() {
        return contains(FIELD_HasMemberInvites);
    }

    /**
     * Remover for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public void removeHasMemberInvites() {
        remove(FIELD_HasMemberInvites);
    }

    /**
     * Getter for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public Boolean isHasMemberInvites(GetMode mode) {
        return obtainDirect(FIELD_HasMemberInvites, Boolean.class, mode);
    }

    /**
     * Getter for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public Boolean isHasMemberInvites() {
        return obtainDirect(FIELD_HasMemberInvites, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public Group setHasMemberInvites(Boolean value, SetMode mode) {
        putDirect(FIELD_HasMemberInvites, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public Group setHasMemberInvites(Boolean value) {
        putDirect(FIELD_HasMemberInvites, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for hasMemberInvites
     *
     * @see Group.Fields#hasMemberInvites
     */
    public Group setHasMemberInvites(boolean value) {
        putDirect(FIELD_HasMemberInvites, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public boolean hasNumIdentityChanges() {
        return contains(FIELD_NumIdentityChanges);
    }

    /**
     * Remover for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public void removeNumIdentityChanges() {
        remove(FIELD_NumIdentityChanges);
    }

    /**
     * Getter for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public Integer getNumIdentityChanges(GetMode mode) {
        return obtainDirect(FIELD_NumIdentityChanges, Integer.class, mode);
    }

    /**
     * Getter for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public Integer getNumIdentityChanges() {
        return obtainDirect(FIELD_NumIdentityChanges, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public Group setNumIdentityChanges(Integer value, SetMode mode) {
        putDirect(FIELD_NumIdentityChanges, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public Group setNumIdentityChanges(Integer value) {
        putDirect(FIELD_NumIdentityChanges, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for numIdentityChanges
     *
     * @see Group.Fields#numIdentityChanges
     */
    public Group setNumIdentityChanges(int value) {
        putDirect(FIELD_NumIdentityChanges, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public boolean hasMaxIdentityChanges() {
        return contains(FIELD_MaxIdentityChanges);
    }

    /**
     * Remover for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public void removeMaxIdentityChanges() {
        remove(FIELD_MaxIdentityChanges);
    }

    /**
     * Getter for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public Integer getMaxIdentityChanges(GetMode mode) {
        return obtainDirect(FIELD_MaxIdentityChanges, Integer.class, mode);
    }

    /**
     * Getter for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public Integer getMaxIdentityChanges() {
        return obtainDirect(FIELD_MaxIdentityChanges, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public Group setMaxIdentityChanges(Integer value, SetMode mode) {
        putDirect(FIELD_MaxIdentityChanges, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public Group setMaxIdentityChanges(Integer value) {
        putDirect(FIELD_MaxIdentityChanges, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for maxIdentityChanges
     *
     * @see Group.Fields#maxIdentityChanges
     */
    public Group setMaxIdentityChanges(int value) {
        putDirect(FIELD_MaxIdentityChanges, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public boolean hasMaxMembers() {
        return contains(FIELD_MaxMembers);
    }

    /**
     * Remover for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public void removeMaxMembers() {
        remove(FIELD_MaxMembers);
    }

    /**
     * Getter for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public Integer getMaxMembers(GetMode mode) {
        return obtainDirect(FIELD_MaxMembers, Integer.class, mode);
    }

    /**
     * Getter for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public Integer getMaxMembers() {
        return obtainDirect(FIELD_MaxMembers, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public Group setMaxMembers(Integer value, SetMode mode) {
        putDirect(FIELD_MaxMembers, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public Group setMaxMembers(Integer value) {
        putDirect(FIELD_MaxMembers, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for maxMembers
     *
     * @see Group.Fields#maxMembers
     */
    public Group setMaxMembers(int value) {
        putDirect(FIELD_MaxMembers, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public boolean hasMaxModerators() {
        return contains(FIELD_MaxModerators);
    }

    /**
     * Remover for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public void removeMaxModerators() {
        remove(FIELD_MaxModerators);
    }

    /**
     * Getter for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public Integer getMaxModerators(GetMode mode) {
        return obtainDirect(FIELD_MaxModerators, Integer.class, mode);
    }

    /**
     * Getter for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public Integer getMaxModerators() {
        return obtainDirect(FIELD_MaxModerators, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public Group setMaxModerators(Integer value, SetMode mode) {
        putDirect(FIELD_MaxModerators, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public Group setMaxModerators(Integer value) {
        putDirect(FIELD_MaxModerators, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for maxModerators
     *
     * @see Group.Fields#maxModerators
     */
    public Group setMaxModerators(int value) {
        putDirect(FIELD_MaxModerators, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public boolean hasMaxSubgroups() {
        return contains(FIELD_MaxSubgroups);
    }

    /**
     * Remover for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public void removeMaxSubgroups() {
        remove(FIELD_MaxSubgroups);
    }

    /**
     * Getter for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public Integer getMaxSubgroups(GetMode mode) {
        return obtainDirect(FIELD_MaxSubgroups, Integer.class, mode);
    }

    /**
     * Getter for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public Integer getMaxSubgroups() {
        return obtainDirect(FIELD_MaxSubgroups, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public Group setMaxSubgroups(Integer value, SetMode mode) {
        putDirect(FIELD_MaxSubgroups, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public Group setMaxSubgroups(Integer value) {
        putDirect(FIELD_MaxSubgroups, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for maxSubgroups
     *
     * @see Group.Fields#maxSubgroups
     */
    public Group setMaxSubgroups(int value) {
        putDirect(FIELD_MaxSubgroups, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public boolean hasMaxFeeds() {
        return contains(FIELD_MaxFeeds);
    }

    /**
     * Remover for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public void removeMaxFeeds() {
        remove(FIELD_MaxFeeds);
    }

    /**
     * Getter for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public Integer getMaxFeeds(GetMode mode) {
        return obtainDirect(FIELD_MaxFeeds, Integer.class, mode);
    }

    /**
     * Getter for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public Integer getMaxFeeds() {
        return obtainDirect(FIELD_MaxFeeds, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public Group setMaxFeeds(Integer value, SetMode mode) {
        putDirect(FIELD_MaxFeeds, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public Group setMaxFeeds(Integer value) {
        putDirect(FIELD_MaxFeeds, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for maxFeeds
     *
     * @see Group.Fields#maxFeeds
     */
    public Group setMaxFeeds(int value) {
        putDirect(FIELD_MaxFeeds, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public boolean hasHasEmailExport() {
        return contains(FIELD_HasEmailExport);
    }

    /**
     * Remover for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public void removeHasEmailExport() {
        remove(FIELD_HasEmailExport);
    }

    /**
     * Getter for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public Boolean isHasEmailExport(GetMode mode) {
        return obtainDirect(FIELD_HasEmailExport, Boolean.class, mode);
    }

    /**
     * Getter for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public Boolean isHasEmailExport() {
        return obtainDirect(FIELD_HasEmailExport, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public Group setHasEmailExport(Boolean value, SetMode mode) {
        putDirect(FIELD_HasEmailExport, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public Group setHasEmailExport(Boolean value) {
        putDirect(FIELD_HasEmailExport, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for hasEmailExport
     *
     * @see Group.Fields#hasEmailExport
     */
    public Group setHasEmailExport(boolean value) {
        putDirect(FIELD_HasEmailExport, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for categoriesEnabled
     *
     * @see Group.Fields#categoriesEnabled
     */
    public boolean hasCategoriesEnabled() {
        return contains(FIELD_CategoriesEnabled);
    }

    /**
     * Remover for categoriesEnabled
     *
     * @see Group.Fields#categoriesEnabled
     */
    public void removeCategoriesEnabled() {
        remove(FIELD_CategoriesEnabled);
    }

    /**
     * Getter for categoriesEnabled
     *
     * @see Group.Fields#categoriesEnabled
     */
    public PostCategory getCategoriesEnabled(GetMode mode) {
        return obtainDirect(FIELD_CategoriesEnabled, PostCategory.class, mode);
    }

    /**
     * Getter for categoriesEnabled
     *
     * @see Group.Fields#categoriesEnabled
     */
    public PostCategory getCategoriesEnabled() {
        return obtainDirect(FIELD_CategoriesEnabled, PostCategory.class, GetMode.STRICT);
    }

    /**
     * Setter for categoriesEnabled
     *
     * @see Group.Fields#categoriesEnabled
     */
    public Group setCategoriesEnabled(PostCategory value, SetMode mode) {
        putDirect(FIELD_CategoriesEnabled, PostCategory.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for categoriesEnabled
     *
     * @see Group.Fields#categoriesEnabled
     */
    public Group setCategoriesEnabled(PostCategory value) {
        putDirect(FIELD_CategoriesEnabled, PostCategory.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public boolean hasHasNetworkUpdates() {
        return contains(FIELD_HasNetworkUpdates);
    }

    /**
     * Remover for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public void removeHasNetworkUpdates() {
        remove(FIELD_HasNetworkUpdates);
    }

    /**
     * Getter for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public Boolean isHasNetworkUpdates(GetMode mode) {
        return obtainDirect(FIELD_HasNetworkUpdates, Boolean.class, mode);
    }

    /**
     * Getter for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public Boolean isHasNetworkUpdates() {
        return obtainDirect(FIELD_HasNetworkUpdates, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public Group setHasNetworkUpdates(Boolean value, SetMode mode) {
        putDirect(FIELD_HasNetworkUpdates, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public Group setHasNetworkUpdates(Boolean value) {
        putDirect(FIELD_HasNetworkUpdates, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for hasNetworkUpdates
     *
     * @see Group.Fields#hasNetworkUpdates
     */
    public Group setHasNetworkUpdates(boolean value) {
        putDirect(FIELD_HasNetworkUpdates, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public boolean hasHasMemberRoster() {
        return contains(FIELD_HasMemberRoster);
    }

    /**
     * Remover for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public void removeHasMemberRoster() {
        remove(FIELD_HasMemberRoster);
    }

    /**
     * Getter for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public Boolean isHasMemberRoster(GetMode mode) {
        return obtainDirect(FIELD_HasMemberRoster, Boolean.class, mode);
    }

    /**
     * Getter for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public Boolean isHasMemberRoster() {
        return obtainDirect(FIELD_HasMemberRoster, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public Group setHasMemberRoster(Boolean value, SetMode mode) {
        putDirect(FIELD_HasMemberRoster, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public Group setHasMemberRoster(Boolean value) {
        putDirect(FIELD_HasMemberRoster, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for hasMemberRoster
     *
     * @see Group.Fields#hasMemberRoster
     */
    public Group setHasMemberRoster(boolean value) {
        putDirect(FIELD_HasMemberRoster, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public boolean hasHasSettings() {
        return contains(FIELD_HasSettings);
    }

    /**
     * Remover for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public void removeHasSettings() {
        remove(FIELD_HasSettings);
    }

    /**
     * Getter for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public Boolean isHasSettings(GetMode mode) {
        return obtainDirect(FIELD_HasSettings, Boolean.class, mode);
    }

    /**
     * Getter for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public Boolean isHasSettings() {
        return obtainDirect(FIELD_HasSettings, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public Group setHasSettings(Boolean value, SetMode mode) {
        putDirect(FIELD_HasSettings, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public Group setHasSettings(Boolean value) {
        putDirect(FIELD_HasSettings, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for hasSettings
     *
     * @see Group.Fields#hasSettings
     */
    public Group setHasSettings(boolean value) {
        putDirect(FIELD_HasSettings, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public boolean hasHideSubgroups() {
        return contains(FIELD_HideSubgroups);
    }

    /**
     * Remover for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public void removeHideSubgroups() {
        remove(FIELD_HideSubgroups);
    }

    /**
     * Getter for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public Boolean isHideSubgroups(GetMode mode) {
        return obtainDirect(FIELD_HideSubgroups, Boolean.class, mode);
    }

    /**
     * Getter for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public Boolean isHideSubgroups() {
        return obtainDirect(FIELD_HideSubgroups, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public Group setHideSubgroups(Boolean value, SetMode mode) {
        putDirect(FIELD_HideSubgroups, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public Group setHideSubgroups(Boolean value) {
        putDirect(FIELD_HideSubgroups, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for hideSubgroups
     *
     * @see Group.Fields#hideSubgroups
     */
    public Group setHideSubgroups(boolean value) {
        putDirect(FIELD_HideSubgroups, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for categoriesForModeratorsOnly
     *
     * @see Group.Fields#categoriesForModeratorsOnly
     */
    public boolean hasCategoriesForModeratorsOnly() {
        return contains(FIELD_CategoriesForModeratorsOnly);
    }

    /**
     * Remover for categoriesForModeratorsOnly
     *
     * @see Group.Fields#categoriesForModeratorsOnly
     */
    public void removeCategoriesForModeratorsOnly() {
        remove(FIELD_CategoriesForModeratorsOnly);
    }

    /**
     * Getter for categoriesForModeratorsOnly
     *
     * @see Group.Fields#categoriesForModeratorsOnly
     */
    public PostCategory getCategoriesForModeratorsOnly(GetMode mode) {
        return obtainDirect(FIELD_CategoriesForModeratorsOnly, PostCategory.class, mode);
    }

    /**
     * Getter for categoriesForModeratorsOnly
     *
     * @see Group.Fields#categoriesForModeratorsOnly
     */
    public PostCategory getCategoriesForModeratorsOnly() {
        return obtainDirect(FIELD_CategoriesForModeratorsOnly, PostCategory.class, GetMode.STRICT);
    }

    /**
     * Setter for categoriesForModeratorsOnly
     *
     * @see Group.Fields#categoriesForModeratorsOnly
     */
    public Group setCategoriesForModeratorsOnly(PostCategory value, SetMode mode) {
        putDirect(FIELD_CategoriesForModeratorsOnly, PostCategory.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for categoriesForModeratorsOnly
     *
     * @see Group.Fields#categoriesForModeratorsOnly
     */
    public Group setCategoriesForModeratorsOnly(PostCategory value) {
        putDirect(FIELD_CategoriesForModeratorsOnly, PostCategory.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public boolean hasNumMemberFlagsToDelete() {
        return contains(FIELD_NumMemberFlagsToDelete);
    }

    /**
     * Remover for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public void removeNumMemberFlagsToDelete() {
        remove(FIELD_NumMemberFlagsToDelete);
    }

    /**
     * Getter for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public Integer getNumMemberFlagsToDelete(GetMode mode) {
        return obtainDirect(FIELD_NumMemberFlagsToDelete, Integer.class, mode);
    }

    /**
     * Getter for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public Integer getNumMemberFlagsToDelete() {
        return obtainDirect(FIELD_NumMemberFlagsToDelete, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public Group setNumMemberFlagsToDelete(Integer value, SetMode mode) {
        putDirect(FIELD_NumMemberFlagsToDelete, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public Group setNumMemberFlagsToDelete(Integer value) {
        putDirect(FIELD_NumMemberFlagsToDelete, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for numMemberFlagsToDelete
     *
     * @see Group.Fields#numMemberFlagsToDelete
     */
    public Group setNumMemberFlagsToDelete(int value) {
        putDirect(FIELD_NumMemberFlagsToDelete, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for newsFormat
     *
     * @see Group.Fields#newsFormat
     */
    public boolean hasNewsFormat() {
        return contains(FIELD_NewsFormat);
    }

    /**
     * Remover for newsFormat
     *
     * @see Group.Fields#newsFormat
     */
    public void removeNewsFormat() {
        remove(FIELD_NewsFormat);
    }

    /**
     * Getter for newsFormat
     *
     * @see Group.Fields#newsFormat
     */
    public NewsFormat getNewsFormat(GetMode mode) {
        return obtainDirect(FIELD_NewsFormat, NewsFormat.class, mode);
    }

    /**
     * Getter for newsFormat
     *
     * @see Group.Fields#newsFormat
     */
    public NewsFormat getNewsFormat() {
        return obtainDirect(FIELD_NewsFormat, NewsFormat.class, GetMode.STRICT);
    }

    /**
     * Setter for newsFormat
     *
     * @see Group.Fields#newsFormat
     */
    public Group setNewsFormat(NewsFormat value, SetMode mode) {
        putDirect(FIELD_NewsFormat, NewsFormat.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for newsFormat
     *
     * @see Group.Fields#newsFormat
     */
    public Group setNewsFormat(NewsFormat value) {
        putDirect(FIELD_NewsFormat, NewsFormat.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for preModeration
     *
     * @see Group.Fields#preModeration
     */
    public boolean hasPreModeration() {
        return contains(FIELD_PreModeration);
    }

    /**
     * Remover for preModeration
     *
     * @see Group.Fields#preModeration
     */
    public void removePreModeration() {
        remove(FIELD_PreModeration);
    }

    /**
     * Getter for preModeration
     *
     * @see Group.Fields#preModeration
     */
    public PreModerationType getPreModeration(GetMode mode) {
        return obtainDirect(FIELD_PreModeration, PreModerationType.class, mode);
    }

    /**
     * Getter for preModeration
     *
     * @see Group.Fields#preModeration
     */
    public PreModerationType getPreModeration() {
        return obtainDirect(FIELD_PreModeration, PreModerationType.class, GetMode.STRICT);
    }

    /**
     * Setter for preModeration
     *
     * @see Group.Fields#preModeration
     */
    public Group setPreModeration(PreModerationType value, SetMode mode) {
        putDirect(FIELD_PreModeration, PreModerationType.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for preModeration
     *
     * @see Group.Fields#preModeration
     */
    public Group setPreModeration(PreModerationType value) {
        putDirect(FIELD_PreModeration, PreModerationType.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for preModerationCategories
     *
     * @see Group.Fields#preModerationCategories
     */
    public boolean hasPreModerationCategories() {
        return contains(FIELD_PreModerationCategories);
    }

    /**
     * Remover for preModerationCategories
     *
     * @see Group.Fields#preModerationCategories
     */
    public void removePreModerationCategories() {
        remove(FIELD_PreModerationCategories);
    }

    /**
     * Getter for preModerationCategories
     *
     * @see Group.Fields#preModerationCategories
     */
    public PostCategory getPreModerationCategories(GetMode mode) {
        return obtainDirect(FIELD_PreModerationCategories, PostCategory.class, mode);
    }

    /**
     * Getter for preModerationCategories
     *
     * @see Group.Fields#preModerationCategories
     */
    public PostCategory getPreModerationCategories() {
        return obtainDirect(FIELD_PreModerationCategories, PostCategory.class, GetMode.STRICT);
    }

    /**
     * Setter for preModerationCategories
     *
     * @see Group.Fields#preModerationCategories
     */
    public Group setPreModerationCategories(PostCategory value, SetMode mode) {
        putDirect(FIELD_PreModerationCategories, PostCategory.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for preModerationCategories
     *
     * @see Group.Fields#preModerationCategories
     */
    public Group setPreModerationCategories(PostCategory value) {
        putDirect(FIELD_PreModerationCategories, PostCategory.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for nonMemberPermissions
     *
     * @see Group.Fields#nonMemberPermissions
     */
    public boolean hasNonMemberPermissions() {
        return contains(FIELD_NonMemberPermissions);
    }

    /**
     * Remover for nonMemberPermissions
     *
     * @see Group.Fields#nonMemberPermissions
     */
    public void removeNonMemberPermissions() {
        remove(FIELD_NonMemberPermissions);
    }

    /**
     * Getter for nonMemberPermissions
     *
     * @see Group.Fields#nonMemberPermissions
     */
    public NonMemberPermissions getNonMemberPermissions(GetMode mode) {
        return obtainDirect(FIELD_NonMemberPermissions, NonMemberPermissions.class, mode);
    }

    /**
     * Getter for nonMemberPermissions
     *
     * @see Group.Fields#nonMemberPermissions
     */
    public NonMemberPermissions getNonMemberPermissions() {
        return obtainDirect(FIELD_NonMemberPermissions, NonMemberPermissions.class, GetMode.STRICT);
    }

    /**
     * Setter for nonMemberPermissions
     *
     * @see Group.Fields#nonMemberPermissions
     */
    public Group setNonMemberPermissions(NonMemberPermissions value, SetMode mode) {
        putDirect(FIELD_NonMemberPermissions, NonMemberPermissions.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for nonMemberPermissions
     *
     * @see Group.Fields#nonMemberPermissions
     */
    public Group setNonMemberPermissions(NonMemberPermissions value) {
        putDirect(FIELD_NonMemberPermissions, NonMemberPermissions.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public boolean hasOpenedToNonMembersTimestamp() {
        return contains(FIELD_OpenedToNonMembersTimestamp);
    }

    /**
     * Remover for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public void removeOpenedToNonMembersTimestamp() {
        remove(FIELD_OpenedToNonMembersTimestamp);
    }

    /**
     * Getter for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public Long getOpenedToNonMembersTimestamp(GetMode mode) {
        return obtainDirect(FIELD_OpenedToNonMembersTimestamp, Long.class, mode);
    }

    /**
     * Getter for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public Long getOpenedToNonMembersTimestamp() {
        return obtainDirect(FIELD_OpenedToNonMembersTimestamp, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public Group setOpenedToNonMembersTimestamp(Long value, SetMode mode) {
        putDirect(FIELD_OpenedToNonMembersTimestamp, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public Group setOpenedToNonMembersTimestamp(Long value) {
        putDirect(FIELD_OpenedToNonMembersTimestamp, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for openedToNonMembersTimestamp
     *
     * @see Group.Fields#openedToNonMembersTimestamp
     */
    public Group setOpenedToNonMembersTimestamp(long value) {
        putDirect(FIELD_OpenedToNonMembersTimestamp, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public boolean hasPreModerateNewMembersPeriodInDays() {
        return contains(FIELD_PreModerateNewMembersPeriodInDays);
    }

    /**
     * Remover for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public void removePreModerateNewMembersPeriodInDays() {
        remove(FIELD_PreModerateNewMembersPeriodInDays);
    }

    /**
     * Getter for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public Integer getPreModerateNewMembersPeriodInDays(GetMode mode) {
        return obtainDirect(FIELD_PreModerateNewMembersPeriodInDays, Integer.class, mode);
    }

    /**
     * Getter for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public Integer getPreModerateNewMembersPeriodInDays() {
        return obtainDirect(FIELD_PreModerateNewMembersPeriodInDays, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public Group setPreModerateNewMembersPeriodInDays(Integer value, SetMode mode) {
        putDirect(FIELD_PreModerateNewMembersPeriodInDays, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public Group setPreModerateNewMembersPeriodInDays(Integer value) {
        putDirect(FIELD_PreModerateNewMembersPeriodInDays, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for preModerateNewMembersPeriodInDays
     *
     * @see Group.Fields#preModerateNewMembersPeriodInDays
     */
    public Group setPreModerateNewMembersPeriodInDays(int value) {
        putDirect(FIELD_PreModerateNewMembersPeriodInDays, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public boolean hasPreModerateMembersWithLowConnections() {
        return contains(FIELD_PreModerateMembersWithLowConnections);
    }

    /**
     * Remover for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public void removePreModerateMembersWithLowConnections() {
        remove(FIELD_PreModerateMembersWithLowConnections);
    }

    /**
     * Getter for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public Boolean isPreModerateMembersWithLowConnections(GetMode mode) {
        return obtainDirect(FIELD_PreModerateMembersWithLowConnections, Boolean.class, mode);
    }

    /**
     * Getter for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public Boolean isPreModerateMembersWithLowConnections() {
        return obtainDirect(FIELD_PreModerateMembersWithLowConnections, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public Group setPreModerateMembersWithLowConnections(Boolean value, SetMode mode) {
        putDirect(FIELD_PreModerateMembersWithLowConnections, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public Group setPreModerateMembersWithLowConnections(Boolean value) {
        putDirect(FIELD_PreModerateMembersWithLowConnections, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for preModerateMembersWithLowConnections
     *
     * @see Group.Fields#preModerateMembersWithLowConnections
     */
    public Group setPreModerateMembersWithLowConnections(boolean value) {
        putDirect(FIELD_PreModerateMembersWithLowConnections, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for preApprovedEmailDomains
     *
     * @see Group.Fields#preApprovedEmailDomains
     */
    public boolean hasPreApprovedEmailDomains() {
        return contains(FIELD_PreApprovedEmailDomains);
    }

    /**
     * Remover for preApprovedEmailDomains
     *
     * @see Group.Fields#preApprovedEmailDomains
     */
    public void removePreApprovedEmailDomains() {
        remove(FIELD_PreApprovedEmailDomains);
    }

    /**
     * Getter for preApprovedEmailDomains
     *
     * @see Group.Fields#preApprovedEmailDomains
     */
    public StringArray getPreApprovedEmailDomains(GetMode mode) {
        return obtainWrapped(FIELD_PreApprovedEmailDomains, StringArray.class, mode);
    }

    /**
     * Getter for preApprovedEmailDomains
     *
     * @see Group.Fields#preApprovedEmailDomains
     */
    public StringArray getPreApprovedEmailDomains() {
        return obtainWrapped(FIELD_PreApprovedEmailDomains, StringArray.class, GetMode.STRICT);
    }

    /**
     * Setter for preApprovedEmailDomains
     *
     * @see Group.Fields#preApprovedEmailDomains
     */
    public Group setPreApprovedEmailDomains(StringArray value, SetMode mode) {
        putWrapped(FIELD_PreApprovedEmailDomains, StringArray.class, value, mode);
        return this;
    }

    /**
     * Setter for preApprovedEmailDomains
     *
     * @see Group.Fields#preApprovedEmailDomains
     */
    public Group setPreApprovedEmailDomains(StringArray value) {
        putWrapped(FIELD_PreApprovedEmailDomains, StringArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for owner
     *
     * @see Group.Fields#owner
     */
    public boolean hasOwner() {
        return contains(FIELD_Owner);
    }

    /**
     * Remover for owner
     *
     * @see Group.Fields#owner
     */
    public void removeOwner() {
        remove(FIELD_Owner);
    }

    /**
     * Getter for owner
     *
     * @see Group.Fields#owner
     */
    public GroupMembership getOwner(GetMode mode) {
        return obtainWrapped(FIELD_Owner, GroupMembership.class, mode);
    }

    /**
     * Getter for owner
     *
     * @see Group.Fields#owner
     */
    public GroupMembership getOwner() {
        return obtainWrapped(FIELD_Owner, GroupMembership.class, GetMode.STRICT);
    }

    /**
     * Setter for owner
     *
     * @see Group.Fields#owner
     */
    public Group setOwner(GroupMembership value, SetMode mode) {
        putWrapped(FIELD_Owner, GroupMembership.class, value, mode);
        return this;
    }

    /**
     * Setter for owner
     *
     * @see Group.Fields#owner
     */
    public Group setOwner(GroupMembership value) {
        putWrapped(FIELD_Owner, GroupMembership.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public Group clone()
        throws CloneNotSupportedException
    {
        return ((Group) super.clone());
    }

    @Override
    public Group copy()
        throws CloneNotSupportedException
    {
        return ((Group) super.copy());
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
         * This field is read-only, and will be automatically assigned when the group is POSTed.
         *
         */
        public PathSpec id() {
            return new PathSpec(getPathComponents(), "id");
        }

        public PathSpec vanityUrl() {
            return new PathSpec(getPathComponents(), "vanityUrl");
        }

        /**
         * Parent group references
         *
         */
        public PathSpec parentGroupId() {
            return new PathSpec(getPathComponents(), "parentGroupId");
        }

        /**
         * Cannot be changed by owner/managers once set (only CS can change it)
         *
         */
        public PathSpec name() {
            return new PathSpec(getPathComponents(), "name");
        }

        public PathSpec shortDescription() {
            return new PathSpec(getPathComponents(), "shortDescription");
        }

        public PathSpec description() {
            return new PathSpec(getPathComponents(), "description");
        }

        public PathSpec rules() {
            return new PathSpec(getPathComponents(), "rules");
        }

        public PathSpec contactEmail() {
            return new PathSpec(getPathComponents(), "contactEmail");
        }

        public PathSpec category() {
            return new PathSpec(getPathComponents(), "category");
        }

        public PathSpec otherCategory() {
            return new PathSpec(getPathComponents(), "otherCategory");
        }

        public PathSpec badge() {
            return new PathSpec(getPathComponents(), "badge");
        }

        public PathSpec homeSiteUrl() {
            return new PathSpec(getPathComponents(), "homeSiteUrl");
        }

        public PathSpec smallLogoMediaUrl() {
            return new PathSpec(getPathComponents(), "smallLogoMediaUrl");
        }

        public PathSpec largeLogoMediaUrl() {
            return new PathSpec(getPathComponents(), "largeLogoMediaUrl");
        }

        /**
         * An inlined Location struct
         *
         */
        public com.linkedin.restli.examples.groups.api.Location.Fields location() {
            return new com.linkedin.restli.examples.groups.api.Location.Fields(getPathComponents(), "location");
        }

        public PathSpec locale() {
            return new PathSpec(getPathComponents(), "locale");
        }

        /**
         * System-generated, read-only
         *
         */
        public PathSpec sharingKey() {
            return new PathSpec(getPathComponents(), "sharingKey");
        }

        public PathSpec visibility() {
            return new PathSpec(getPathComponents(), "visibility");
        }

        public PathSpec state() {
            return new PathSpec(getPathComponents(), "state");
        }

        /**
         * This field is read-only.
         *
         */
        public PathSpec createdTimestamp() {
            return new PathSpec(getPathComponents(), "createdTimestamp");
        }

        /**
         * This field is read-only.
         *
         */
        public PathSpec lastModifiedTimestamp() {
            return new PathSpec(getPathComponents(), "lastModifiedTimestamp");
        }

        public PathSpec isOpenToNonMembers() {
            return new PathSpec(getPathComponents(), "isOpenToNonMembers");
        }

        /**
         *
         *
         */
        public PathSpec approvalModes() {
            return new PathSpec(getPathComponents(), "approvalModes");
        }

        public PathSpec contactability() {
            return new PathSpec(getPathComponents(), "contactability");
        }

        public PathSpec directoryPresence() {
            return new PathSpec(getPathComponents(), "directoryPresence");
        }

        public PathSpec hasMemberInvites() {
            return new PathSpec(getPathComponents(), "hasMemberInvites");
        }

        /**
         * System-maintained, read-only
         *
         */
        public PathSpec numIdentityChanges() {
            return new PathSpec(getPathComponents(), "numIdentityChanges");
        }

        /**
         * CS-editable only
         *
         */
        public PathSpec maxIdentityChanges() {
            return new PathSpec(getPathComponents(), "maxIdentityChanges");
        }

        /**
         * CS-editable only
         *
         */
        public PathSpec maxMembers() {
            return new PathSpec(getPathComponents(), "maxMembers");
        }

        /**
         * CS-editable only
         *
         */
        public PathSpec maxModerators() {
            return new PathSpec(getPathComponents(), "maxModerators");
        }

        /**
         * CS-editable only
         *
         */
        public PathSpec maxSubgroups() {
            return new PathSpec(getPathComponents(), "maxSubgroups");
        }

        /**
         * CS-editable only
         *
         */
        public PathSpec maxFeeds() {
            return new PathSpec(getPathComponents(), "maxFeeds");
        }

        public PathSpec hasEmailExport() {
            return new PathSpec(getPathComponents(), "hasEmailExport");
        }

        public PathSpec categoriesEnabled() {
            return new PathSpec(getPathComponents(), "categoriesEnabled");
        }

        public PathSpec hasNetworkUpdates() {
            return new PathSpec(getPathComponents(), "hasNetworkUpdates");
        }

        public PathSpec hasMemberRoster() {
            return new PathSpec(getPathComponents(), "hasMemberRoster");
        }

        public PathSpec hasSettings() {
            return new PathSpec(getPathComponents(), "hasSettings");
        }

        public PathSpec hideSubgroups() {
            return new PathSpec(getPathComponents(), "hideSubgroups");
        }

        public PathSpec categoriesForModeratorsOnly() {
            return new PathSpec(getPathComponents(), "categoriesForModeratorsOnly");
        }

        public PathSpec numMemberFlagsToDelete() {
            return new PathSpec(getPathComponents(), "numMemberFlagsToDelete");
        }

        public PathSpec newsFormat() {
            return new PathSpec(getPathComponents(), "newsFormat");
        }

        public PathSpec preModeration() {
            return new PathSpec(getPathComponents(), "preModeration");
        }

        public PathSpec preModerationCategories() {
            return new PathSpec(getPathComponents(), "preModerationCategories");
        }

        public PathSpec nonMemberPermissions() {
            return new PathSpec(getPathComponents(), "nonMemberPermissions");
        }

        public PathSpec openedToNonMembersTimestamp() {
            return new PathSpec(getPathComponents(), "openedToNonMembersTimestamp");
        }

        public PathSpec preModerateNewMembersPeriodInDays() {
            return new PathSpec(getPathComponents(), "preModerateNewMembersPeriodInDays");
        }

        public PathSpec preModerateMembersWithLowConnections() {
            return new PathSpec(getPathComponents(), "preModerateMembersWithLowConnections");
        }

        public PathSpec preApprovedEmailDomains() {
            return new PathSpec(getPathComponents(), "preApprovedEmailDomains");
        }

        /**
         * Required when creating a group, not returned as part of the default representation (must be explicitly requested via 'fields'
         *
         */
        public com.linkedin.restli.examples.groups.api.GroupMembership.Fields owner() {
            return new com.linkedin.restli.examples.groups.api.GroupMembership.Fields(getPathComponents(), "owner");
        }

    }

}

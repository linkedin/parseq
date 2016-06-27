
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
 * Request for transferOwnership RPC method
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/TransferOwnershipRequest.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class TransferOwnershipRequest
    extends RecordTemplate
{

    private final static TransferOwnershipRequest.Fields _fields = new TransferOwnershipRequest.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"TransferOwnershipRequest\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"Request for transferOwnership RPC method\",\"fields\":[{\"name\":\"newOwnerMemberID\",\"type\":\"int\",\"doc\":\"The new owner\"},{\"name\":\"newOwnerContactEmail\",\"type\":\"string\",\"doc\":\"The new owner's email\"}]}"));
    private final static RecordDataSchema.Field FIELD_NewOwnerMemberID = SCHEMA.getField("newOwnerMemberID");
    private final static RecordDataSchema.Field FIELD_NewOwnerContactEmail = SCHEMA.getField("newOwnerContactEmail");

    public TransferOwnershipRequest() {
        super(new DataMap(), SCHEMA);
    }

    public TransferOwnershipRequest(DataMap data) {
        super(data, SCHEMA);
    }

    public static TransferOwnershipRequest.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public boolean hasNewOwnerMemberID() {
        return contains(FIELD_NewOwnerMemberID);
    }

    /**
     * Remover for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public void removeNewOwnerMemberID() {
        remove(FIELD_NewOwnerMemberID);
    }

    /**
     * Getter for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public Integer getNewOwnerMemberID(GetMode mode) {
        return obtainDirect(FIELD_NewOwnerMemberID, Integer.class, mode);
    }

    /**
     * Getter for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public Integer getNewOwnerMemberID() {
        return obtainDirect(FIELD_NewOwnerMemberID, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public TransferOwnershipRequest setNewOwnerMemberID(Integer value, SetMode mode) {
        putDirect(FIELD_NewOwnerMemberID, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public TransferOwnershipRequest setNewOwnerMemberID(Integer value) {
        putDirect(FIELD_NewOwnerMemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for newOwnerMemberID
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerMemberID
     */
    public TransferOwnershipRequest setNewOwnerMemberID(int value) {
        putDirect(FIELD_NewOwnerMemberID, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for newOwnerContactEmail
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerContactEmail
     */
    public boolean hasNewOwnerContactEmail() {
        return contains(FIELD_NewOwnerContactEmail);
    }

    /**
     * Remover for newOwnerContactEmail
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerContactEmail
     */
    public void removeNewOwnerContactEmail() {
        remove(FIELD_NewOwnerContactEmail);
    }

    /**
     * Getter for newOwnerContactEmail
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerContactEmail
     */
    public String getNewOwnerContactEmail(GetMode mode) {
        return obtainDirect(FIELD_NewOwnerContactEmail, String.class, mode);
    }

    /**
     * Getter for newOwnerContactEmail
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerContactEmail
     */
    public String getNewOwnerContactEmail() {
        return obtainDirect(FIELD_NewOwnerContactEmail, String.class, GetMode.STRICT);
    }

    /**
     * Setter for newOwnerContactEmail
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerContactEmail
     */
    public TransferOwnershipRequest setNewOwnerContactEmail(String value, SetMode mode) {
        putDirect(FIELD_NewOwnerContactEmail, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for newOwnerContactEmail
     * 
     * @see TransferOwnershipRequest.Fields#newOwnerContactEmail
     */
    public TransferOwnershipRequest setNewOwnerContactEmail(String value) {
        putDirect(FIELD_NewOwnerContactEmail, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public TransferOwnershipRequest clone()
        throws CloneNotSupportedException
    {
        return ((TransferOwnershipRequest) super.clone());
    }

    @Override
    public TransferOwnershipRequest copy()
        throws CloneNotSupportedException
    {
        return ((TransferOwnershipRequest) super.copy());
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
         * The new owner
         * 
         */
        public PathSpec newOwnerMemberID() {
            return new PathSpec(getPathComponents(), "newOwnerMemberID");
        }

        /**
         * The new owner's email
         * 
         */
        public PathSpec newOwnerContactEmail() {
            return new PathSpec(getPathComponents(), "newOwnerContactEmail");
        }

    }

}


package com.linkedin.restli.examples.groups.api;

import javax.annotation.Generated;
import com.linkedin.data.schema.TyperefDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.TyperefInfo;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/GroupMembershipQueryParamArrayRef.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GroupMembershipQueryParamArrayRef
    extends TyperefInfo
{

    private final static TyperefDataSchema SCHEMA = ((TyperefDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"typeref\",\"name\":\"GroupMembershipQueryParamArrayRef\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"ref\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GroupMembershipQueryParam\",\"doc\":\"A GroupMembership resource query parameters\",\"fields\":[{\"name\":\"intParameter\",\"type\":\"int\"},{\"name\":\"stringParameter\",\"type\":\"string\"}]}}}"));

    public GroupMembershipQueryParamArrayRef() {
        super(SCHEMA);
    }

}

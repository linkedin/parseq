
package com.linkedin.restli.examples.groups.api;

import javax.annotation.Generated;
import com.linkedin.data.schema.EnumDataSchema;
import com.linkedin.data.template.DataTemplateUtil;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/MembershipSortOrder.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public enum MembershipSortOrder {

    LAST_NAME_ASC,
    LAST_TRANSITION_ON_DESC,
    $UNKNOWN;
    private final static EnumDataSchema SCHEMA = ((EnumDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"enum\",\"name\":\"MembershipSortOrder\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"symbols\":[\"LAST_NAME_ASC\",\"LAST_TRANSITION_ON_DESC\"]}"));

}

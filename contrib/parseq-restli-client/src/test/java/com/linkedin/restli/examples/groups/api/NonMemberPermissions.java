
package com.linkedin.restli.examples.groups.api;

import javax.annotation.Generated;
import com.linkedin.data.schema.EnumDataSchema;
import com.linkedin.data.template.DataTemplateUtil;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/NonMemberPermissions.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public enum NonMemberPermissions {

    NONE,
    READ_ONLY,
    COMMENT_WITH_MODERATION,
    COMMENT_AND_POST_WITH_MODERATION,
    COMMENT_NO_MODERATION_POST_MODERATION,
    $UNKNOWN;
    private final static EnumDataSchema SCHEMA = ((EnumDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"enum\",\"name\":\"NonMemberPermissions\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"symbols\":[\"NONE\",\"READ_ONLY\",\"COMMENT_WITH_MODERATION\",\"COMMENT_AND_POST_WITH_MODERATION\",\"COMMENT_NO_MODERATION_POST_MODERATION\"]}"));

}

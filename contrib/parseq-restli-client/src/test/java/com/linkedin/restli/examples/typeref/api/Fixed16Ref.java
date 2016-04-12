
package com.linkedin.restli.examples.typeref.api;

import javax.annotation.Generated;
import com.linkedin.data.schema.TyperefDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.TyperefInfo;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/typeref/api/TyperefRecord.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Fixed16Ref
    extends TyperefInfo
{

    private final static TyperefDataSchema SCHEMA = ((TyperefDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"typeref\",\"name\":\"Fixed16Ref\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"ref\":{\"type\":\"fixed\",\"name\":\"Fixed16\",\"size\":16}}"));

    public Fixed16Ref() {
        super(SCHEMA);
    }

}

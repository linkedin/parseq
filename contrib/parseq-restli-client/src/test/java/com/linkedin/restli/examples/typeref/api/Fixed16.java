
package com.linkedin.restli.examples.typeref.api;

import javax.annotation.Generated;
import com.linkedin.data.ByteString;
import com.linkedin.data.schema.FixedDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.FixedTemplate;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/typeref/api/Fixed16.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Fixed16
    extends FixedTemplate
{

    private final static FixedDataSchema SCHEMA = ((FixedDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"fixed\",\"name\":\"Fixed16\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"size\":16}"));

    public Fixed16(ByteString value) {
        super(value, SCHEMA);
    }

    public Fixed16(Object data) {
        super(data, SCHEMA);
    }

    @Override
    public Fixed16 clone()
        throws CloneNotSupportedException
    {
        return ((Fixed16) super.clone());
    }

    @Override
    public Fixed16 copy()
        throws CloneNotSupportedException
    {
        return ((Fixed16) super.copy());
    }

}


package com.linkedin.restli.examples.greetings.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/SearchMetadata.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class ToneFacetArray
    extends WrappingArrayTemplate<ToneFacet>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"ToneFacet\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"doc\":\"metadata for greetings search results\",\"fields\":[{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]}},{\"name\":\"count\",\"type\":\"int\"}]}}"));

    public ToneFacetArray() {
        this(new DataList());
    }

    public ToneFacetArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public ToneFacetArray(Collection<ToneFacet> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public ToneFacetArray(DataList data) {
        super(data, SCHEMA, ToneFacet.class);
    }

    @Override
    public ToneFacetArray clone()
        throws CloneNotSupportedException
    {
        return ((ToneFacetArray) super.clone());
    }

    @Override
    public ToneFacetArray copy()
        throws CloneNotSupportedException
    {
        return ((ToneFacetArray) super.copy());
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

        public com.linkedin.restli.examples.greetings.api.ToneFacet.Fields items() {
            return new com.linkedin.restli.examples.greetings.api.ToneFacet.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

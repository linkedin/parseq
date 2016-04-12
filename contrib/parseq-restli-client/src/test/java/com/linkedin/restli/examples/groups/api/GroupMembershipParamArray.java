
package com.linkedin.restli.examples.groups.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.groups.client.groups.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipParamArray
    extends WrappingArrayTemplate<GroupMembershipParam>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GroupMembershipParam\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A GroupMembership entity parameters\",\"fields\":[{\"name\":\"intParameter\",\"type\":\"int\"},{\"name\":\"stringParameter\",\"type\":\"string\"}]}}"));

    public GroupMembershipParamArray() {
        this(new DataList());
    }

    public GroupMembershipParamArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public GroupMembershipParamArray(Collection<GroupMembershipParam> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public GroupMembershipParamArray(DataList data) {
        super(data, SCHEMA, GroupMembershipParam.class);
    }

    @Override
    public GroupMembershipParamArray clone()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipParamArray) super.clone());
    }

    @Override
    public GroupMembershipParamArray copy()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipParamArray) super.copy());
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

        public com.linkedin.restli.examples.groups.api.GroupMembershipParam.Fields items() {
            return new com.linkedin.restli.examples.groups.api.GroupMembershipParam.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}

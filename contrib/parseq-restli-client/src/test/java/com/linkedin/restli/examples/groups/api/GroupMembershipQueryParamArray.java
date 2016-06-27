
package com.linkedin.restli.examples.groups.api;

import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataList;
import com.linkedin.data.schema.ArrayDataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.WrappingArrayTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/groups/api/GroupMembershipQueryParamArrayRef.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class GroupMembershipQueryParamArray
    extends WrappingArrayTemplate<GroupMembershipQueryParam>
{

    private final static ArrayDataSchema SCHEMA = ((ArrayDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"GroupMembershipQueryParam\",\"namespace\":\"com.linkedin.restli.examples.groups.api\",\"doc\":\"A GroupMembership resource query parameters\",\"fields\":[{\"name\":\"intParameter\",\"type\":\"int\"},{\"name\":\"stringParameter\",\"type\":\"string\"}]}}"));

    public GroupMembershipQueryParamArray() {
        this(new DataList());
    }

    public GroupMembershipQueryParamArray(int initialCapacity) {
        this(new DataList(initialCapacity));
    }

    public GroupMembershipQueryParamArray(Collection<GroupMembershipQueryParam> c) {
        this(new DataList(c.size()));
        addAll(c);
    }

    public GroupMembershipQueryParamArray(DataList data) {
        super(data, SCHEMA, GroupMembershipQueryParam.class);
    }

    @Override
    public GroupMembershipQueryParamArray clone()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipQueryParamArray) super.clone());
    }

    @Override
    public GroupMembershipQueryParamArray copy()
        throws CloneNotSupportedException
    {
        return ((GroupMembershipQueryParamArray) super.copy());
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

        public com.linkedin.restli.examples.groups.api.GroupMembershipQueryParam.Fields items() {
            return new com.linkedin.restli.examples.groups.api.GroupMembershipQueryParam.Fields(getPathComponents(), PathSpec.WILDCARD);
        }

    }

}


package com.linkedin.restli.examples.typeref.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.DataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.TyperefDataSchema;
import com.linkedin.data.schema.UnionDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.HasTyperefInfo;
import com.linkedin.data.template.TyperefInfo;
import com.linkedin.data.template.UnionTemplate;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/typeref/api/Union.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class Union
    extends UnionTemplate
    implements HasTyperefInfo
{

    private final static UnionDataSchema SCHEMA = ((UnionDataSchema) DataTemplateUtil.parseSchema("[\"int\",\"string\"]"));
    private final static DataSchema MEMBER_Int = SCHEMA.getType("int");
    private final static DataSchema MEMBER_String = SCHEMA.getType("string");
    private final static TyperefInfo TYPEREFINFO = new Union.UnionTyperefInfo();

    public Union() {
        super(new DataMap(), SCHEMA);
    }

    public Union(Object data) {
        super(data, SCHEMA);
    }

    public static Union create(Integer value) {
        Union newUnion = new Union();
        newUnion.setInt(value);
        return newUnion;
    }

    public boolean isInt() {
        return memberIs("int");
    }

    public Integer getInt() {
        return obtainDirect(MEMBER_Int, Integer.class, "int");
    }

    public void setInt(Integer value) {
        selectDirect(MEMBER_Int, Integer.class, Integer.class, "int", value);
    }

    public static Union create(java.lang.String value) {
        Union newUnion = new Union();
        newUnion.setString(value);
        return newUnion;
    }

    public boolean isString() {
        return memberIs("string");
    }

    public java.lang.String getString() {
        return obtainDirect(MEMBER_String, java.lang.String.class, "string");
    }

    public void setString(java.lang.String value) {
        selectDirect(MEMBER_String, java.lang.String.class, java.lang.String.class, "string", value);
    }

    @Override
    public Union clone()
        throws CloneNotSupportedException
    {
        return ((Union) super.clone());
    }

    @Override
    public Union copy()
        throws CloneNotSupportedException
    {
        return ((Union) super.copy());
    }

    public TyperefInfo typerefInfo() {
        return TYPEREFINFO;
    }

    public static class Fields
        extends PathSpec
    {


        public Fields(List<java.lang.String> path, java.lang.String name) {
            super(path, name);
        }

        public Fields() {
            super();
        }

        public PathSpec Int() {
            return new PathSpec(getPathComponents(), "int");
        }

        public PathSpec String() {
            return new PathSpec(getPathComponents(), "string");
        }

    }


    /**
     * 
     * 
     */
    private final static class UnionTyperefInfo
        extends TyperefInfo
    {

        private final static TyperefDataSchema SCHEMA = ((TyperefDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"typeref\",\"name\":\"Union\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"ref\":[\"int\",\"string\"]}"));

        public UnionTyperefInfo() {
            super(SCHEMA);
        }

    }

}

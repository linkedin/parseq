
package com.linkedin.restli.examples.greetings.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.DataSchema;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.schema.UnionDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.SetMode;
import com.linkedin.data.template.UnionTemplate;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ValidationDemo.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class ValidationDemo
    extends RecordTemplate
{

    private final static ValidationDemo.Fields _fields = new ValidationDemo.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"ValidationDemo\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"stringA\",\"type\":\"string\",\"validate\":{\"strlen\":{\"min\":1,\"max\":10}}},{\"name\":\"intA\",\"type\":\"int\",\"optional\":true},{\"name\":\"stringB\",\"type\":\"string\"},{\"name\":\"intB\",\"type\":\"int\",\"optional\":true,\"validate\":{\"seven\":{}}},{\"name\":\"UnionFieldWithInlineRecord\",\"type\":[{\"type\":\"record\",\"name\":\"myRecord\",\"fields\":[{\"name\":\"foo1\",\"type\":\"int\"},{\"name\":\"foo2\",\"type\":\"int\",\"optional\":true}]},{\"type\":\"enum\",\"name\":\"myEnum\",\"symbols\":[\"FOOFOO\",\"BARBAR\"]}]},{\"name\":\"ArrayWithInlineRecord\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"myItem\",\"fields\":[{\"name\":\"bar1\",\"type\":\"string\"},{\"name\":\"bar2\",\"type\":\"string\"}]}},\"optional\":true},{\"name\":\"MapWithTyperefs\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"typeref\",\"name\":\"myGreeting\",\"ref\":{\"type\":\"record\",\"name\":\"Greeting\",\"doc\":\"A greeting\",\"fields\":[{\"name\":\"id\",\"type\":\"long\"},{\"name\":\"message\",\"type\":\"string\"},{\"name\":\"tone\",\"type\":{\"type\":\"enum\",\"name\":\"Tone\",\"symbols\":[\"FRIENDLY\",\"SINCERE\",\"INSULTING\"]},\"doc\":\"tone\"}]}}},\"optional\":true},{\"name\":\"validationDemoNext\",\"type\":\"ValidationDemo\",\"optional\":true}]}"));
    private final static RecordDataSchema.Field FIELD_StringA = SCHEMA.getField("stringA");
    private final static RecordDataSchema.Field FIELD_IntA = SCHEMA.getField("intA");
    private final static RecordDataSchema.Field FIELD_StringB = SCHEMA.getField("stringB");
    private final static RecordDataSchema.Field FIELD_IntB = SCHEMA.getField("intB");
    private final static RecordDataSchema.Field FIELD_UnionFieldWithInlineRecord = SCHEMA.getField("UnionFieldWithInlineRecord");
    private final static RecordDataSchema.Field FIELD_ArrayWithInlineRecord = SCHEMA.getField("ArrayWithInlineRecord");
    private final static RecordDataSchema.Field FIELD_MapWithTyperefs = SCHEMA.getField("MapWithTyperefs");
    private final static RecordDataSchema.Field FIELD_ValidationDemoNext = SCHEMA.getField("validationDemoNext");

    public ValidationDemo() {
        super(new DataMap(), SCHEMA);
    }

    public ValidationDemo(DataMap data) {
        super(data, SCHEMA);
    }

    public static ValidationDemo.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for stringA
     * 
     * @see ValidationDemo.Fields#stringA
     */
    public boolean hasStringA() {
        return contains(FIELD_StringA);
    }

    /**
     * Remover for stringA
     * 
     * @see ValidationDemo.Fields#stringA
     */
    public void removeStringA() {
        remove(FIELD_StringA);
    }

    /**
     * Getter for stringA
     * 
     * @see ValidationDemo.Fields#stringA
     */
    public String getStringA(GetMode mode) {
        return obtainDirect(FIELD_StringA, String.class, mode);
    }

    /**
     * Getter for stringA
     * 
     * @see ValidationDemo.Fields#stringA
     */
    public String getStringA() {
        return obtainDirect(FIELD_StringA, String.class, GetMode.STRICT);
    }

    /**
     * Setter for stringA
     * 
     * @see ValidationDemo.Fields#stringA
     */
    public ValidationDemo setStringA(String value, SetMode mode) {
        putDirect(FIELD_StringA, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for stringA
     * 
     * @see ValidationDemo.Fields#stringA
     */
    public ValidationDemo setStringA(String value) {
        putDirect(FIELD_StringA, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public boolean hasIntA() {
        return contains(FIELD_IntA);
    }

    /**
     * Remover for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public void removeIntA() {
        remove(FIELD_IntA);
    }

    /**
     * Getter for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public Integer getIntA(GetMode mode) {
        return obtainDirect(FIELD_IntA, Integer.class, mode);
    }

    /**
     * Getter for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public Integer getIntA() {
        return obtainDirect(FIELD_IntA, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public ValidationDemo setIntA(Integer value, SetMode mode) {
        putDirect(FIELD_IntA, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public ValidationDemo setIntA(Integer value) {
        putDirect(FIELD_IntA, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for intA
     * 
     * @see ValidationDemo.Fields#intA
     */
    public ValidationDemo setIntA(int value) {
        putDirect(FIELD_IntA, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for stringB
     * 
     * @see ValidationDemo.Fields#stringB
     */
    public boolean hasStringB() {
        return contains(FIELD_StringB);
    }

    /**
     * Remover for stringB
     * 
     * @see ValidationDemo.Fields#stringB
     */
    public void removeStringB() {
        remove(FIELD_StringB);
    }

    /**
     * Getter for stringB
     * 
     * @see ValidationDemo.Fields#stringB
     */
    public String getStringB(GetMode mode) {
        return obtainDirect(FIELD_StringB, String.class, mode);
    }

    /**
     * Getter for stringB
     * 
     * @see ValidationDemo.Fields#stringB
     */
    public String getStringB() {
        return obtainDirect(FIELD_StringB, String.class, GetMode.STRICT);
    }

    /**
     * Setter for stringB
     * 
     * @see ValidationDemo.Fields#stringB
     */
    public ValidationDemo setStringB(String value, SetMode mode) {
        putDirect(FIELD_StringB, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for stringB
     * 
     * @see ValidationDemo.Fields#stringB
     */
    public ValidationDemo setStringB(String value) {
        putDirect(FIELD_StringB, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public boolean hasIntB() {
        return contains(FIELD_IntB);
    }

    /**
     * Remover for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public void removeIntB() {
        remove(FIELD_IntB);
    }

    /**
     * Getter for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public Integer getIntB(GetMode mode) {
        return obtainDirect(FIELD_IntB, Integer.class, mode);
    }

    /**
     * Getter for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public Integer getIntB() {
        return obtainDirect(FIELD_IntB, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public ValidationDemo setIntB(Integer value, SetMode mode) {
        putDirect(FIELD_IntB, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public ValidationDemo setIntB(Integer value) {
        putDirect(FIELD_IntB, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for intB
     * 
     * @see ValidationDemo.Fields#intB
     */
    public ValidationDemo setIntB(int value) {
        putDirect(FIELD_IntB, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for UnionFieldWithInlineRecord
     * 
     * @see ValidationDemo.Fields#UnionFieldWithInlineRecord
     */
    public boolean hasUnionFieldWithInlineRecord() {
        return contains(FIELD_UnionFieldWithInlineRecord);
    }

    /**
     * Remover for UnionFieldWithInlineRecord
     * 
     * @see ValidationDemo.Fields#UnionFieldWithInlineRecord
     */
    public void removeUnionFieldWithInlineRecord() {
        remove(FIELD_UnionFieldWithInlineRecord);
    }

    /**
     * Getter for UnionFieldWithInlineRecord
     * 
     * @see ValidationDemo.Fields#UnionFieldWithInlineRecord
     */
    public ValidationDemo.UnionFieldWithInlineRecord getUnionFieldWithInlineRecord(GetMode mode) {
        return obtainWrapped(FIELD_UnionFieldWithInlineRecord, ValidationDemo.UnionFieldWithInlineRecord.class, mode);
    }

    /**
     * Getter for UnionFieldWithInlineRecord
     * 
     * @see ValidationDemo.Fields#UnionFieldWithInlineRecord
     */
    public ValidationDemo.UnionFieldWithInlineRecord getUnionFieldWithInlineRecord() {
        return obtainWrapped(FIELD_UnionFieldWithInlineRecord, ValidationDemo.UnionFieldWithInlineRecord.class, GetMode.STRICT);
    }

    /**
     * Setter for UnionFieldWithInlineRecord
     * 
     * @see ValidationDemo.Fields#UnionFieldWithInlineRecord
     */
    public ValidationDemo setUnionFieldWithInlineRecord(ValidationDemo.UnionFieldWithInlineRecord value, SetMode mode) {
        putWrapped(FIELD_UnionFieldWithInlineRecord, ValidationDemo.UnionFieldWithInlineRecord.class, value, mode);
        return this;
    }

    /**
     * Setter for UnionFieldWithInlineRecord
     * 
     * @see ValidationDemo.Fields#UnionFieldWithInlineRecord
     */
    public ValidationDemo setUnionFieldWithInlineRecord(ValidationDemo.UnionFieldWithInlineRecord value) {
        putWrapped(FIELD_UnionFieldWithInlineRecord, ValidationDemo.UnionFieldWithInlineRecord.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for ArrayWithInlineRecord
     * 
     * @see ValidationDemo.Fields#ArrayWithInlineRecord
     */
    public boolean hasArrayWithInlineRecord() {
        return contains(FIELD_ArrayWithInlineRecord);
    }

    /**
     * Remover for ArrayWithInlineRecord
     * 
     * @see ValidationDemo.Fields#ArrayWithInlineRecord
     */
    public void removeArrayWithInlineRecord() {
        remove(FIELD_ArrayWithInlineRecord);
    }

    /**
     * Getter for ArrayWithInlineRecord
     * 
     * @see ValidationDemo.Fields#ArrayWithInlineRecord
     */
    public MyItemArray getArrayWithInlineRecord(GetMode mode) {
        return obtainWrapped(FIELD_ArrayWithInlineRecord, MyItemArray.class, mode);
    }

    /**
     * Getter for ArrayWithInlineRecord
     * 
     * @see ValidationDemo.Fields#ArrayWithInlineRecord
     */
    public MyItemArray getArrayWithInlineRecord() {
        return obtainWrapped(FIELD_ArrayWithInlineRecord, MyItemArray.class, GetMode.STRICT);
    }

    /**
     * Setter for ArrayWithInlineRecord
     * 
     * @see ValidationDemo.Fields#ArrayWithInlineRecord
     */
    public ValidationDemo setArrayWithInlineRecord(MyItemArray value, SetMode mode) {
        putWrapped(FIELD_ArrayWithInlineRecord, MyItemArray.class, value, mode);
        return this;
    }

    /**
     * Setter for ArrayWithInlineRecord
     * 
     * @see ValidationDemo.Fields#ArrayWithInlineRecord
     */
    public ValidationDemo setArrayWithInlineRecord(MyItemArray value) {
        putWrapped(FIELD_ArrayWithInlineRecord, MyItemArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for MapWithTyperefs
     * 
     * @see ValidationDemo.Fields#MapWithTyperefs
     */
    public boolean hasMapWithTyperefs() {
        return contains(FIELD_MapWithTyperefs);
    }

    /**
     * Remover for MapWithTyperefs
     * 
     * @see ValidationDemo.Fields#MapWithTyperefs
     */
    public void removeMapWithTyperefs() {
        remove(FIELD_MapWithTyperefs);
    }

    /**
     * Getter for MapWithTyperefs
     * 
     * @see ValidationDemo.Fields#MapWithTyperefs
     */
    public GreetingMap getMapWithTyperefs(GetMode mode) {
        return obtainWrapped(FIELD_MapWithTyperefs, GreetingMap.class, mode);
    }

    /**
     * Getter for MapWithTyperefs
     * 
     * @see ValidationDemo.Fields#MapWithTyperefs
     */
    public GreetingMap getMapWithTyperefs() {
        return obtainWrapped(FIELD_MapWithTyperefs, GreetingMap.class, GetMode.STRICT);
    }

    /**
     * Setter for MapWithTyperefs
     * 
     * @see ValidationDemo.Fields#MapWithTyperefs
     */
    public ValidationDemo setMapWithTyperefs(GreetingMap value, SetMode mode) {
        putWrapped(FIELD_MapWithTyperefs, GreetingMap.class, value, mode);
        return this;
    }

    /**
     * Setter for MapWithTyperefs
     * 
     * @see ValidationDemo.Fields#MapWithTyperefs
     */
    public ValidationDemo setMapWithTyperefs(GreetingMap value) {
        putWrapped(FIELD_MapWithTyperefs, GreetingMap.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for validationDemoNext
     * 
     * @see ValidationDemo.Fields#validationDemoNext
     */
    public boolean hasValidationDemoNext() {
        return contains(FIELD_ValidationDemoNext);
    }

    /**
     * Remover for validationDemoNext
     * 
     * @see ValidationDemo.Fields#validationDemoNext
     */
    public void removeValidationDemoNext() {
        remove(FIELD_ValidationDemoNext);
    }

    /**
     * Getter for validationDemoNext
     * 
     * @see ValidationDemo.Fields#validationDemoNext
     */
    public ValidationDemo getValidationDemoNext(GetMode mode) {
        return obtainWrapped(FIELD_ValidationDemoNext, ValidationDemo.class, mode);
    }

    /**
     * Getter for validationDemoNext
     * 
     * @see ValidationDemo.Fields#validationDemoNext
     */
    public ValidationDemo getValidationDemoNext() {
        return obtainWrapped(FIELD_ValidationDemoNext, ValidationDemo.class, GetMode.STRICT);
    }

    /**
     * Setter for validationDemoNext
     * 
     * @see ValidationDemo.Fields#validationDemoNext
     */
    public ValidationDemo setValidationDemoNext(ValidationDemo value, SetMode mode) {
        putWrapped(FIELD_ValidationDemoNext, ValidationDemo.class, value, mode);
        return this;
    }

    /**
     * Setter for validationDemoNext
     * 
     * @see ValidationDemo.Fields#validationDemoNext
     */
    public ValidationDemo setValidationDemoNext(ValidationDemo value) {
        putWrapped(FIELD_ValidationDemoNext, ValidationDemo.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public ValidationDemo clone()
        throws CloneNotSupportedException
    {
        return ((ValidationDemo) super.clone());
    }

    @Override
    public ValidationDemo copy()
        throws CloneNotSupportedException
    {
        return ((ValidationDemo) super.copy());
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

        public PathSpec stringA() {
            return new PathSpec(getPathComponents(), "stringA");
        }

        public PathSpec intA() {
            return new PathSpec(getPathComponents(), "intA");
        }

        public PathSpec stringB() {
            return new PathSpec(getPathComponents(), "stringB");
        }

        public PathSpec intB() {
            return new PathSpec(getPathComponents(), "intB");
        }

        public com.linkedin.restli.examples.greetings.api.ValidationDemo.UnionFieldWithInlineRecord.Fields UnionFieldWithInlineRecord() {
            return new com.linkedin.restli.examples.greetings.api.ValidationDemo.UnionFieldWithInlineRecord.Fields(getPathComponents(), "UnionFieldWithInlineRecord");
        }

        public com.linkedin.restli.examples.greetings.api.MyItemArray.Fields ArrayWithInlineRecord() {
            return new com.linkedin.restli.examples.greetings.api.MyItemArray.Fields(getPathComponents(), "ArrayWithInlineRecord");
        }

        public com.linkedin.restli.examples.greetings.api.GreetingMap.Fields MapWithTyperefs() {
            return new com.linkedin.restli.examples.greetings.api.GreetingMap.Fields(getPathComponents(), "MapWithTyperefs");
        }

        public com.linkedin.restli.examples.greetings.api.ValidationDemo.Fields validationDemoNext() {
            return new com.linkedin.restli.examples.greetings.api.ValidationDemo.Fields(getPathComponents(), "validationDemoNext");
        }

    }

    @Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/greetings/api/ValidationDemo.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
    public final static class UnionFieldWithInlineRecord
        extends UnionTemplate
    {

        private final static UnionDataSchema SCHEMA = ((UnionDataSchema) DataTemplateUtil.parseSchema("[{\"type\":\"record\",\"name\":\"myRecord\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"fields\":[{\"name\":\"foo1\",\"type\":\"int\"},{\"name\":\"foo2\",\"type\":\"int\",\"optional\":true}]},{\"type\":\"enum\",\"name\":\"myEnum\",\"namespace\":\"com.linkedin.restli.examples.greetings.api\",\"symbols\":[\"FOOFOO\",\"BARBAR\"]}]"));
        private final static DataSchema MEMBER_MyRecord = SCHEMA.getType("com.linkedin.restli.examples.greetings.api.myRecord");
        private final static DataSchema MEMBER_MyEnum = SCHEMA.getType("com.linkedin.restli.examples.greetings.api.myEnum");

        public UnionFieldWithInlineRecord() {
            super(new DataMap(), SCHEMA);
        }

        public UnionFieldWithInlineRecord(Object data) {
            super(data, SCHEMA);
        }

        public static ValidationDemo.UnionFieldWithInlineRecord create(myRecord value) {
            ValidationDemo.UnionFieldWithInlineRecord newUnion = new ValidationDemo.UnionFieldWithInlineRecord();
            newUnion.setMyRecord(value);
            return newUnion;
        }

        public boolean isMyRecord() {
            return memberIs("com.linkedin.restli.examples.greetings.api.myRecord");
        }

        public myRecord getMyRecord() {
            return obtainWrapped(MEMBER_MyRecord, myRecord.class, "com.linkedin.restli.examples.greetings.api.myRecord");
        }

        public void setMyRecord(myRecord value) {
            selectWrapped(MEMBER_MyRecord, myRecord.class, "com.linkedin.restli.examples.greetings.api.myRecord", value);
        }

        public static ValidationDemo.UnionFieldWithInlineRecord create(myEnum value) {
            ValidationDemo.UnionFieldWithInlineRecord newUnion = new ValidationDemo.UnionFieldWithInlineRecord();
            newUnion.setMyEnum(value);
            return newUnion;
        }

        public boolean isMyEnum() {
            return memberIs("com.linkedin.restli.examples.greetings.api.myEnum");
        }

        public myEnum getMyEnum() {
            return obtainDirect(MEMBER_MyEnum, myEnum.class, "com.linkedin.restli.examples.greetings.api.myEnum");
        }

        public void setMyEnum(myEnum value) {
            selectDirect(MEMBER_MyEnum, myEnum.class, String.class, "com.linkedin.restli.examples.greetings.api.myEnum", value);
        }

        @Override
        public ValidationDemo.UnionFieldWithInlineRecord clone()
            throws CloneNotSupportedException
        {
            return ((ValidationDemo.UnionFieldWithInlineRecord) super.clone());
        }

        @Override
        public ValidationDemo.UnionFieldWithInlineRecord copy()
            throws CloneNotSupportedException
        {
            return ((ValidationDemo.UnionFieldWithInlineRecord) super.copy());
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

            public com.linkedin.restli.examples.greetings.api.myRecord.Fields MyRecord() {
                return new com.linkedin.restli.examples.greetings.api.myRecord.Fields(getPathComponents(), "com.linkedin.restli.examples.greetings.api.myRecord");
            }

            public PathSpec MyEnum() {
                return new PathSpec(getPathComponents(), "com.linkedin.restli.examples.greetings.api.myEnum");
            }

        }

    }

}

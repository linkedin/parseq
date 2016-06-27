
package com.linkedin.restli.examples.typeref.api;

import java.util.List;
import javax.annotation.Generated;
import com.linkedin.data.ByteString;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.GetMode;
import com.linkedin.data.template.IntegerArray;
import com.linkedin.data.template.IntegerMap;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.data.template.SetMode;


/**
 * 
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Data Template. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/pegasus/com/linkedin/restli/examples/typeref/api/TyperefRecord.pdsc.", date = "Thu Mar 31 14:04:52 PDT 2016")
public class TyperefRecord
    extends RecordTemplate
{

    private final static TyperefRecord.Fields _fields = new TyperefRecord.Fields();
    private final static RecordDataSchema SCHEMA = ((RecordDataSchema) DataTemplateUtil.parseSchema("{\"type\":\"record\",\"name\":\"TyperefRecord\",\"namespace\":\"com.linkedin.restli.examples.typeref.api\",\"fields\":[{\"name\":\"int\",\"type\":{\"type\":\"typeref\",\"name\":\"IntRef\",\"ref\":\"int\"},\"optional\":true},{\"name\":\"long\",\"type\":{\"type\":\"typeref\",\"name\":\"LongRef\",\"ref\":\"long\"},\"optional\":true},{\"name\":\"float\",\"type\":{\"type\":\"typeref\",\"name\":\"FloatRef\",\"ref\":\"float\"},\"optional\":true},{\"name\":\"double\",\"type\":{\"type\":\"typeref\",\"name\":\"DoubleRef\",\"ref\":\"double\"},\"optional\":true},{\"name\":\"boolean\",\"type\":{\"type\":\"typeref\",\"name\":\"BooleanRef\",\"ref\":\"boolean\"},\"optional\":true},{\"name\":\"string\",\"type\":{\"type\":\"typeref\",\"name\":\"StringRef\",\"ref\":\"string\"},\"optional\":true},{\"name\":\"bytes\",\"type\":{\"type\":\"typeref\",\"name\":\"BytesRef\",\"ref\":\"bytes\"},\"optional\":true},{\"name\":\"intArray\",\"type\":{\"type\":\"typeref\",\"name\":\"IntArrayRef\",\"ref\":{\"type\":\"array\",\"items\":\"int\"}},\"optional\":true},{\"name\":\"intMap\",\"type\":{\"type\":\"typeref\",\"name\":\"IntMapRef\",\"ref\":{\"type\":\"map\",\"values\":\"int\"}},\"optional\":true},{\"name\":\"fixed16\",\"type\":{\"type\":\"typeref\",\"name\":\"Fixed16Ref\",\"ref\":{\"type\":\"fixed\",\"name\":\"Fixed16\",\"size\":16}},\"optional\":true},{\"name\":\"fruits\",\"type\":{\"type\":\"typeref\",\"name\":\"FruitsRef\",\"ref\":{\"type\":\"enum\",\"name\":\"Fruits\",\"symbols\":[\"APPLE\",\"ORANGE\"]}},\"optional\":true},{\"name\":\"union\",\"type\":{\"type\":\"typeref\",\"name\":\"Union\",\"ref\":[\"int\",\"string\"]},\"optional\":true},{\"name\":\"union2\",\"type\":{\"type\":\"typeref\",\"name\":\"UnionRef\",\"ref\":\"Union\"},\"optional\":true},{\"name\":\"point\",\"type\":{\"type\":\"typeref\",\"name\":\"PointRef\",\"ref\":{\"type\":\"record\",\"name\":\"Point\",\"fields\":[{\"name\":\"x\",\"type\":\"double\"},{\"name\":\"y\",\"type\":\"double\"}]}},\"optional\":true}]}"));
    private final static RecordDataSchema.Field FIELD_Int = SCHEMA.getField("int");
    private final static RecordDataSchema.Field FIELD_Long = SCHEMA.getField("long");
    private final static RecordDataSchema.Field FIELD_Float = SCHEMA.getField("float");
    private final static RecordDataSchema.Field FIELD_Double = SCHEMA.getField("double");
    private final static RecordDataSchema.Field FIELD_Boolean = SCHEMA.getField("boolean");
    private final static RecordDataSchema.Field FIELD_String = SCHEMA.getField("string");
    private final static RecordDataSchema.Field FIELD_Bytes = SCHEMA.getField("bytes");
    private final static RecordDataSchema.Field FIELD_IntArray = SCHEMA.getField("intArray");
    private final static RecordDataSchema.Field FIELD_IntMap = SCHEMA.getField("intMap");
    private final static RecordDataSchema.Field FIELD_Fixed16 = SCHEMA.getField("fixed16");
    private final static RecordDataSchema.Field FIELD_Fruits = SCHEMA.getField("fruits");
    private final static RecordDataSchema.Field FIELD_Union = SCHEMA.getField("union");
    private final static RecordDataSchema.Field FIELD_Union2 = SCHEMA.getField("union2");
    private final static RecordDataSchema.Field FIELD_Point = SCHEMA.getField("point");

    public TyperefRecord() {
        super(new DataMap(), SCHEMA);
    }

    public TyperefRecord(DataMap data) {
        super(data, SCHEMA);
    }

    public static TyperefRecord.Fields fields() {
        return _fields;
    }

    /**
     * Existence checker for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public boolean hasInt() {
        return contains(FIELD_Int);
    }

    /**
     * Remover for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public void removeInt() {
        remove(FIELD_Int);
    }

    /**
     * Getter for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public Integer getInt(GetMode mode) {
        return obtainDirect(FIELD_Int, Integer.class, mode);
    }

    /**
     * Getter for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public Integer getInt() {
        return obtainDirect(FIELD_Int, Integer.class, GetMode.STRICT);
    }

    /**
     * Setter for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public TyperefRecord setInt(Integer value, SetMode mode) {
        putDirect(FIELD_Int, Integer.class, Integer.class, value, mode);
        return this;
    }

    /**
     * Setter for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public TyperefRecord setInt(Integer value) {
        putDirect(FIELD_Int, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for int
     * 
     * @see TyperefRecord.Fields#int_
     */
    public TyperefRecord setInt(int value) {
        putDirect(FIELD_Int, Integer.class, Integer.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public boolean hasLong() {
        return contains(FIELD_Long);
    }

    /**
     * Remover for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public void removeLong() {
        remove(FIELD_Long);
    }

    /**
     * Getter for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public Long getLong(GetMode mode) {
        return obtainDirect(FIELD_Long, Long.class, mode);
    }

    /**
     * Getter for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public Long getLong() {
        return obtainDirect(FIELD_Long, Long.class, GetMode.STRICT);
    }

    /**
     * Setter for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public TyperefRecord setLong(Long value, SetMode mode) {
        putDirect(FIELD_Long, Long.class, Long.class, value, mode);
        return this;
    }

    /**
     * Setter for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public TyperefRecord setLong(Long value) {
        putDirect(FIELD_Long, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for long
     * 
     * @see TyperefRecord.Fields#long_
     */
    public TyperefRecord setLong(long value) {
        putDirect(FIELD_Long, Long.class, Long.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public boolean hasFloat() {
        return contains(FIELD_Float);
    }

    /**
     * Remover for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public void removeFloat() {
        remove(FIELD_Float);
    }

    /**
     * Getter for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public Float getFloat(GetMode mode) {
        return obtainDirect(FIELD_Float, Float.class, mode);
    }

    /**
     * Getter for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public Float getFloat() {
        return obtainDirect(FIELD_Float, Float.class, GetMode.STRICT);
    }

    /**
     * Setter for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public TyperefRecord setFloat(Float value, SetMode mode) {
        putDirect(FIELD_Float, Float.class, Float.class, value, mode);
        return this;
    }

    /**
     * Setter for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public TyperefRecord setFloat(Float value) {
        putDirect(FIELD_Float, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for float
     * 
     * @see TyperefRecord.Fields#float_
     */
    public TyperefRecord setFloat(float value) {
        putDirect(FIELD_Float, Float.class, Float.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public boolean hasDouble() {
        return contains(FIELD_Double);
    }

    /**
     * Remover for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public void removeDouble() {
        remove(FIELD_Double);
    }

    /**
     * Getter for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public Double getDouble(GetMode mode) {
        return obtainDirect(FIELD_Double, Double.class, mode);
    }

    /**
     * Getter for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public Double getDouble() {
        return obtainDirect(FIELD_Double, Double.class, GetMode.STRICT);
    }

    /**
     * Setter for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public TyperefRecord setDouble(Double value, SetMode mode) {
        putDirect(FIELD_Double, Double.class, Double.class, value, mode);
        return this;
    }

    /**
     * Setter for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public TyperefRecord setDouble(Double value) {
        putDirect(FIELD_Double, Double.class, Double.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for double
     * 
     * @see TyperefRecord.Fields#double_
     */
    public TyperefRecord setDouble(double value) {
        putDirect(FIELD_Double, Double.class, Double.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public boolean hasBoolean() {
        return contains(FIELD_Boolean);
    }

    /**
     * Remover for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public void removeBoolean() {
        remove(FIELD_Boolean);
    }

    /**
     * Getter for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public Boolean isBoolean(GetMode mode) {
        return obtainDirect(FIELD_Boolean, Boolean.class, mode);
    }

    /**
     * Getter for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public Boolean isBoolean() {
        return obtainDirect(FIELD_Boolean, Boolean.class, GetMode.STRICT);
    }

    /**
     * Setter for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public TyperefRecord setBoolean(Boolean value, SetMode mode) {
        putDirect(FIELD_Boolean, Boolean.class, Boolean.class, value, mode);
        return this;
    }

    /**
     * Setter for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public TyperefRecord setBoolean(Boolean value) {
        putDirect(FIELD_Boolean, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Setter for boolean
     * 
     * @see TyperefRecord.Fields#boolean_
     */
    public TyperefRecord setBoolean(boolean value) {
        putDirect(FIELD_Boolean, Boolean.class, Boolean.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for string
     * 
     * @see TyperefRecord.Fields#string
     */
    public boolean hasString() {
        return contains(FIELD_String);
    }

    /**
     * Remover for string
     * 
     * @see TyperefRecord.Fields#string
     */
    public void removeString() {
        remove(FIELD_String);
    }

    /**
     * Getter for string
     * 
     * @see TyperefRecord.Fields#string
     */
    public String getString(GetMode mode) {
        return obtainDirect(FIELD_String, String.class, mode);
    }

    /**
     * Getter for string
     * 
     * @see TyperefRecord.Fields#string
     */
    public String getString() {
        return obtainDirect(FIELD_String, String.class, GetMode.STRICT);
    }

    /**
     * Setter for string
     * 
     * @see TyperefRecord.Fields#string
     */
    public TyperefRecord setString(String value, SetMode mode) {
        putDirect(FIELD_String, String.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for string
     * 
     * @see TyperefRecord.Fields#string
     */
    public TyperefRecord setString(String value) {
        putDirect(FIELD_String, String.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for bytes
     * 
     * @see TyperefRecord.Fields#bytes
     */
    public boolean hasBytes() {
        return contains(FIELD_Bytes);
    }

    /**
     * Remover for bytes
     * 
     * @see TyperefRecord.Fields#bytes
     */
    public void removeBytes() {
        remove(FIELD_Bytes);
    }

    /**
     * Getter for bytes
     * 
     * @see TyperefRecord.Fields#bytes
     */
    public ByteString getBytes(GetMode mode) {
        return obtainDirect(FIELD_Bytes, ByteString.class, mode);
    }

    /**
     * Getter for bytes
     * 
     * @see TyperefRecord.Fields#bytes
     */
    public ByteString getBytes() {
        return obtainDirect(FIELD_Bytes, ByteString.class, GetMode.STRICT);
    }

    /**
     * Setter for bytes
     * 
     * @see TyperefRecord.Fields#bytes
     */
    public TyperefRecord setBytes(ByteString value, SetMode mode) {
        putDirect(FIELD_Bytes, ByteString.class, ByteString.class, value, mode);
        return this;
    }

    /**
     * Setter for bytes
     * 
     * @see TyperefRecord.Fields#bytes
     */
    public TyperefRecord setBytes(ByteString value) {
        putDirect(FIELD_Bytes, ByteString.class, ByteString.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for intArray
     * 
     * @see TyperefRecord.Fields#intArray
     */
    public boolean hasIntArray() {
        return contains(FIELD_IntArray);
    }

    /**
     * Remover for intArray
     * 
     * @see TyperefRecord.Fields#intArray
     */
    public void removeIntArray() {
        remove(FIELD_IntArray);
    }

    /**
     * Getter for intArray
     * 
     * @see TyperefRecord.Fields#intArray
     */
    public IntegerArray getIntArray(GetMode mode) {
        return obtainWrapped(FIELD_IntArray, IntegerArray.class, mode);
    }

    /**
     * Getter for intArray
     * 
     * @see TyperefRecord.Fields#intArray
     */
    public IntegerArray getIntArray() {
        return obtainWrapped(FIELD_IntArray, IntegerArray.class, GetMode.STRICT);
    }

    /**
     * Setter for intArray
     * 
     * @see TyperefRecord.Fields#intArray
     */
    public TyperefRecord setIntArray(IntegerArray value, SetMode mode) {
        putWrapped(FIELD_IntArray, IntegerArray.class, value, mode);
        return this;
    }

    /**
     * Setter for intArray
     * 
     * @see TyperefRecord.Fields#intArray
     */
    public TyperefRecord setIntArray(IntegerArray value) {
        putWrapped(FIELD_IntArray, IntegerArray.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for intMap
     * 
     * @see TyperefRecord.Fields#intMap
     */
    public boolean hasIntMap() {
        return contains(FIELD_IntMap);
    }

    /**
     * Remover for intMap
     * 
     * @see TyperefRecord.Fields#intMap
     */
    public void removeIntMap() {
        remove(FIELD_IntMap);
    }

    /**
     * Getter for intMap
     * 
     * @see TyperefRecord.Fields#intMap
     */
    public IntegerMap getIntMap(GetMode mode) {
        return obtainWrapped(FIELD_IntMap, IntegerMap.class, mode);
    }

    /**
     * Getter for intMap
     * 
     * @see TyperefRecord.Fields#intMap
     */
    public IntegerMap getIntMap() {
        return obtainWrapped(FIELD_IntMap, IntegerMap.class, GetMode.STRICT);
    }

    /**
     * Setter for intMap
     * 
     * @see TyperefRecord.Fields#intMap
     */
    public TyperefRecord setIntMap(IntegerMap value, SetMode mode) {
        putWrapped(FIELD_IntMap, IntegerMap.class, value, mode);
        return this;
    }

    /**
     * Setter for intMap
     * 
     * @see TyperefRecord.Fields#intMap
     */
    public TyperefRecord setIntMap(IntegerMap value) {
        putWrapped(FIELD_IntMap, IntegerMap.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for fixed16
     * 
     * @see TyperefRecord.Fields#fixed16
     */
    public boolean hasFixed16() {
        return contains(FIELD_Fixed16);
    }

    /**
     * Remover for fixed16
     * 
     * @see TyperefRecord.Fields#fixed16
     */
    public void removeFixed16() {
        remove(FIELD_Fixed16);
    }

    /**
     * Getter for fixed16
     * 
     * @see TyperefRecord.Fields#fixed16
     */
    public Fixed16 getFixed16(GetMode mode) {
        return obtainWrapped(FIELD_Fixed16, Fixed16 .class, mode);
    }

    /**
     * Getter for fixed16
     * 
     * @see TyperefRecord.Fields#fixed16
     */
    public Fixed16 getFixed16() {
        return obtainWrapped(FIELD_Fixed16, Fixed16 .class, GetMode.STRICT);
    }

    /**
     * Setter for fixed16
     * 
     * @see TyperefRecord.Fields#fixed16
     */
    public TyperefRecord setFixed16(Fixed16 value, SetMode mode) {
        putWrapped(FIELD_Fixed16, Fixed16 .class, value, mode);
        return this;
    }

    /**
     * Setter for fixed16
     * 
     * @see TyperefRecord.Fields#fixed16
     */
    public TyperefRecord setFixed16(Fixed16 value) {
        putWrapped(FIELD_Fixed16, Fixed16 .class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for fruits
     * 
     * @see TyperefRecord.Fields#fruits
     */
    public boolean hasFruits() {
        return contains(FIELD_Fruits);
    }

    /**
     * Remover for fruits
     * 
     * @see TyperefRecord.Fields#fruits
     */
    public void removeFruits() {
        remove(FIELD_Fruits);
    }

    /**
     * Getter for fruits
     * 
     * @see TyperefRecord.Fields#fruits
     */
    public Fruits getFruits(GetMode mode) {
        return obtainDirect(FIELD_Fruits, Fruits.class, mode);
    }

    /**
     * Getter for fruits
     * 
     * @see TyperefRecord.Fields#fruits
     */
    public Fruits getFruits() {
        return obtainDirect(FIELD_Fruits, Fruits.class, GetMode.STRICT);
    }

    /**
     * Setter for fruits
     * 
     * @see TyperefRecord.Fields#fruits
     */
    public TyperefRecord setFruits(Fruits value, SetMode mode) {
        putDirect(FIELD_Fruits, Fruits.class, String.class, value, mode);
        return this;
    }

    /**
     * Setter for fruits
     * 
     * @see TyperefRecord.Fields#fruits
     */
    public TyperefRecord setFruits(Fruits value) {
        putDirect(FIELD_Fruits, Fruits.class, String.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for union
     * 
     * @see TyperefRecord.Fields#union
     */
    public boolean hasUnion() {
        return contains(FIELD_Union);
    }

    /**
     * Remover for union
     * 
     * @see TyperefRecord.Fields#union
     */
    public void removeUnion() {
        remove(FIELD_Union);
    }

    /**
     * Getter for union
     * 
     * @see TyperefRecord.Fields#union
     */
    public Union getUnion(GetMode mode) {
        return obtainWrapped(FIELD_Union, Union.class, mode);
    }

    /**
     * Getter for union
     * 
     * @see TyperefRecord.Fields#union
     */
    public Union getUnion() {
        return obtainWrapped(FIELD_Union, Union.class, GetMode.STRICT);
    }

    /**
     * Setter for union
     * 
     * @see TyperefRecord.Fields#union
     */
    public TyperefRecord setUnion(Union value, SetMode mode) {
        putWrapped(FIELD_Union, Union.class, value, mode);
        return this;
    }

    /**
     * Setter for union
     * 
     * @see TyperefRecord.Fields#union
     */
    public TyperefRecord setUnion(Union value) {
        putWrapped(FIELD_Union, Union.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for union2
     * 
     * @see TyperefRecord.Fields#union2
     */
    public boolean hasUnion2() {
        return contains(FIELD_Union2);
    }

    /**
     * Remover for union2
     * 
     * @see TyperefRecord.Fields#union2
     */
    public void removeUnion2() {
        remove(FIELD_Union2);
    }

    /**
     * Getter for union2
     * 
     * @see TyperefRecord.Fields#union2
     */
    public Union getUnion2(GetMode mode) {
        return obtainWrapped(FIELD_Union2, Union.class, mode);
    }

    /**
     * Getter for union2
     * 
     * @see TyperefRecord.Fields#union2
     */
    public Union getUnion2() {
        return obtainWrapped(FIELD_Union2, Union.class, GetMode.STRICT);
    }

    /**
     * Setter for union2
     * 
     * @see TyperefRecord.Fields#union2
     */
    public TyperefRecord setUnion2(Union value, SetMode mode) {
        putWrapped(FIELD_Union2, Union.class, value, mode);
        return this;
    }

    /**
     * Setter for union2
     * 
     * @see TyperefRecord.Fields#union2
     */
    public TyperefRecord setUnion2(Union value) {
        putWrapped(FIELD_Union2, Union.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    /**
     * Existence checker for point
     * 
     * @see TyperefRecord.Fields#point
     */
    public boolean hasPoint() {
        return contains(FIELD_Point);
    }

    /**
     * Remover for point
     * 
     * @see TyperefRecord.Fields#point
     */
    public void removePoint() {
        remove(FIELD_Point);
    }

    /**
     * Getter for point
     * 
     * @see TyperefRecord.Fields#point
     */
    public Point getPoint(GetMode mode) {
        return obtainWrapped(FIELD_Point, Point.class, mode);
    }

    /**
     * Getter for point
     * 
     * @see TyperefRecord.Fields#point
     */
    public Point getPoint() {
        return obtainWrapped(FIELD_Point, Point.class, GetMode.STRICT);
    }

    /**
     * Setter for point
     * 
     * @see TyperefRecord.Fields#point
     */
    public TyperefRecord setPoint(Point value, SetMode mode) {
        putWrapped(FIELD_Point, Point.class, value, mode);
        return this;
    }

    /**
     * Setter for point
     * 
     * @see TyperefRecord.Fields#point
     */
    public TyperefRecord setPoint(Point value) {
        putWrapped(FIELD_Point, Point.class, value, SetMode.DISALLOW_NULL);
        return this;
    }

    @Override
    public TyperefRecord clone()
        throws CloneNotSupportedException
    {
        return ((TyperefRecord) super.clone());
    }

    @Override
    public TyperefRecord copy()
        throws CloneNotSupportedException
    {
        return ((TyperefRecord) super.copy());
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

        public PathSpec int_() {
            return new PathSpec(getPathComponents(), "int");
        }

        public PathSpec long_() {
            return new PathSpec(getPathComponents(), "long");
        }

        public PathSpec float_() {
            return new PathSpec(getPathComponents(), "float");
        }

        public PathSpec double_() {
            return new PathSpec(getPathComponents(), "double");
        }

        public PathSpec boolean_() {
            return new PathSpec(getPathComponents(), "boolean");
        }

        public PathSpec string() {
            return new PathSpec(getPathComponents(), "string");
        }

        public PathSpec bytes() {
            return new PathSpec(getPathComponents(), "bytes");
        }

        public PathSpec intArray() {
            return new PathSpec(getPathComponents(), "intArray");
        }

        public PathSpec intMap() {
            return new PathSpec(getPathComponents(), "intMap");
        }

        public PathSpec fixed16() {
            return new PathSpec(getPathComponents(), "fixed16");
        }

        public PathSpec fruits() {
            return new PathSpec(getPathComponents(), "fruits");
        }

        public com.linkedin.restli.examples.typeref.api.Union.Fields union() {
            return new com.linkedin.restli.examples.typeref.api.Union.Fields(getPathComponents(), "union");
        }

        public com.linkedin.restli.examples.typeref.api.Union.Fields union2() {
            return new com.linkedin.restli.examples.typeref.api.Union.Fields(getPathComponents(), "union2");
        }

        public com.linkedin.restli.examples.typeref.api.Point.Fields point() {
            return new com.linkedin.restli.examples.typeref.api.Point.Fields(getPathComponents(), "point");
        }

    }

}

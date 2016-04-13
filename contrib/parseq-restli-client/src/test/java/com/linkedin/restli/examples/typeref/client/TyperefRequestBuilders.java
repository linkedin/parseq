
package com.linkedin.restli.examples.typeref.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.ByteString;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.data.template.IntegerArray;
import com.linkedin.data.template.IntegerMap;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.typeref.api.BooleanRef;
import com.linkedin.restli.examples.typeref.api.BytesRef;
import com.linkedin.restli.examples.typeref.api.CustomNonNegativeLongRef;
import com.linkedin.restli.examples.typeref.api.DoubleRef;
import com.linkedin.restli.examples.typeref.api.FloatRef;
import com.linkedin.restli.examples.typeref.api.Fruits;
import com.linkedin.restli.examples.typeref.api.FruitsRef;
import com.linkedin.restli.examples.typeref.api.IntArrayRef;
import com.linkedin.restli.examples.typeref.api.IntMapRef;
import com.linkedin.restli.examples.typeref.api.IntRef;
import com.linkedin.restli.examples.typeref.api.LongRef;
import com.linkedin.restli.examples.typeref.api.Point;
import com.linkedin.restli.examples.typeref.api.PointRef;
import com.linkedin.restli.examples.typeref.api.StringRef;
import com.linkedin.restli.examples.typeref.api.TyperefRecord;


/**
 * Test for typeref param and return types in actions.
 * 
 * generated from: com.linkedin.restli.examples.typeref.server.TyperefTestResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.typeref.client.typeref.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "typeref";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> BytesFuncParams = new ArrayList<FieldDef<?>>();
        BytesFuncParams.add(new FieldDef<ByteString>("arg1", ByteString.class, DataTemplateUtil.getSchema(BytesRef.class)));
        requestMetadataMap.put("BytesFunc", new DynamicRecordMetadata("BytesFunc", BytesFuncParams));
        ArrayList<FieldDef<?>> CustomNonNegativeLongRefParams = new ArrayList<FieldDef<?>>();
        CustomNonNegativeLongRefParams.add(new FieldDef<com.linkedin.restli.examples.custom.types.CustomNonNegativeLong>("arg1", com.linkedin.restli.examples.custom.types.CustomNonNegativeLong.class, DataTemplateUtil.getSchema(CustomNonNegativeLongRef.class)));
        requestMetadataMap.put("CustomNonNegativeLongRef", new DynamicRecordMetadata("CustomNonNegativeLongRef", CustomNonNegativeLongRefParams));
        ArrayList<FieldDef<?>> FruitsRefParams = new ArrayList<FieldDef<?>>();
        FruitsRefParams.add(new FieldDef<Fruits>("arg1", Fruits.class, DataTemplateUtil.getSchema(FruitsRef.class)));
        requestMetadataMap.put("FruitsRef", new DynamicRecordMetadata("FruitsRef", FruitsRefParams));
        ArrayList<FieldDef<?>> IntArrayFuncParams = new ArrayList<FieldDef<?>>();
        IntArrayFuncParams.add(new FieldDef<IntegerArray>("arg1", IntegerArray.class, DataTemplateUtil.getSchema(IntArrayRef.class)));
        requestMetadataMap.put("IntArrayFunc", new DynamicRecordMetadata("IntArrayFunc", IntArrayFuncParams));
        ArrayList<FieldDef<?>> IntMapFuncParams = new ArrayList<FieldDef<?>>();
        IntMapFuncParams.add(new FieldDef<IntegerMap>("arg1", IntegerMap.class, DataTemplateUtil.getSchema(IntMapRef.class)));
        requestMetadataMap.put("IntMapFunc", new DynamicRecordMetadata("IntMapFunc", IntMapFuncParams));
        ArrayList<FieldDef<?>> PointRefParams = new ArrayList<FieldDef<?>>();
        PointRefParams.add(new FieldDef<Point>("arg1", Point.class, DataTemplateUtil.getSchema(PointRef.class)));
        requestMetadataMap.put("PointRef", new DynamicRecordMetadata("PointRef", PointRefParams));
        ArrayList<FieldDef<?>> StringFuncParams = new ArrayList<FieldDef<?>>();
        StringFuncParams.add(new FieldDef<java.lang.String>("arg1", java.lang.String.class, DataTemplateUtil.getSchema(StringRef.class)));
        requestMetadataMap.put("StringFunc", new DynamicRecordMetadata("StringFunc", StringFuncParams));
        ArrayList<FieldDef<?>> booleanFuncParams = new ArrayList<FieldDef<?>>();
        booleanFuncParams.add(new FieldDef<Boolean>("arg1", Boolean.class, DataTemplateUtil.getSchema(BooleanRef.class)));
        requestMetadataMap.put("booleanFunc", new DynamicRecordMetadata("booleanFunc", booleanFuncParams));
        ArrayList<FieldDef<?>> booleanFunc2Params = new ArrayList<FieldDef<?>>();
        booleanFunc2Params.add(new FieldDef<Boolean>("arg1", Boolean.class, DataTemplateUtil.getSchema(BooleanRef.class)));
        requestMetadataMap.put("booleanFunc2", new DynamicRecordMetadata("booleanFunc2", booleanFunc2Params));
        ArrayList<FieldDef<?>> doubleFuncParams = new ArrayList<FieldDef<?>>();
        doubleFuncParams.add(new FieldDef<Double>("arg1", Double.class, DataTemplateUtil.getSchema(DoubleRef.class)));
        requestMetadataMap.put("doubleFunc", new DynamicRecordMetadata("doubleFunc", doubleFuncParams));
        ArrayList<FieldDef<?>> doubleFunc2Params = new ArrayList<FieldDef<?>>();
        doubleFunc2Params.add(new FieldDef<Double>("arg1", Double.class, DataTemplateUtil.getSchema(DoubleRef.class)));
        requestMetadataMap.put("doubleFunc2", new DynamicRecordMetadata("doubleFunc2", doubleFunc2Params));
        ArrayList<FieldDef<?>> floatFuncParams = new ArrayList<FieldDef<?>>();
        floatFuncParams.add(new FieldDef<Float>("arg1", Float.class, DataTemplateUtil.getSchema(FloatRef.class)));
        requestMetadataMap.put("floatFunc", new DynamicRecordMetadata("floatFunc", floatFuncParams));
        ArrayList<FieldDef<?>> floatFunc2Params = new ArrayList<FieldDef<?>>();
        floatFunc2Params.add(new FieldDef<Float>("arg1", Float.class, DataTemplateUtil.getSchema(FloatRef.class)));
        requestMetadataMap.put("floatFunc2", new DynamicRecordMetadata("floatFunc2", floatFunc2Params));
        ArrayList<FieldDef<?>> intFuncParams = new ArrayList<FieldDef<?>>();
        intFuncParams.add(new FieldDef<Integer>("arg1", Integer.class, DataTemplateUtil.getSchema(IntRef.class)));
        requestMetadataMap.put("intFunc", new DynamicRecordMetadata("intFunc", intFuncParams));
        ArrayList<FieldDef<?>> intFunc2Params = new ArrayList<FieldDef<?>>();
        intFunc2Params.add(new FieldDef<Integer>("arg1", Integer.class, DataTemplateUtil.getSchema(IntRef.class)));
        requestMetadataMap.put("intFunc2", new DynamicRecordMetadata("intFunc2", intFunc2Params));
        ArrayList<FieldDef<?>> longFuncParams = new ArrayList<FieldDef<?>>();
        longFuncParams.add(new FieldDef<Long>("arg1", Long.class, DataTemplateUtil.getSchema(LongRef.class)));
        requestMetadataMap.put("longFunc", new DynamicRecordMetadata("longFunc", longFuncParams));
        ArrayList<FieldDef<?>> longFunc2Params = new ArrayList<FieldDef<?>>();
        longFunc2Params.add(new FieldDef<Long>("arg1", Long.class, DataTemplateUtil.getSchema(LongRef.class)));
        requestMetadataMap.put("longFunc2", new DynamicRecordMetadata("longFunc2", longFunc2Params));
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        responseMetadataMap.put("BytesFunc", new DynamicRecordMetadata("BytesFunc", Collections.singletonList(new FieldDef<ByteString>("value", ByteString.class, DataTemplateUtil.getSchema(BytesRef.class)))));
        responseMetadataMap.put("CustomNonNegativeLongRef", new DynamicRecordMetadata("CustomNonNegativeLongRef", Collections.singletonList(new FieldDef<com.linkedin.restli.examples.custom.types.CustomNonNegativeLong>("value", com.linkedin.restli.examples.custom.types.CustomNonNegativeLong.class, DataTemplateUtil.getSchema(CustomNonNegativeLongRef.class)))));
        responseMetadataMap.put("FruitsRef", new DynamicRecordMetadata("FruitsRef", Collections.singletonList(new FieldDef<Fruits>("value", Fruits.class, DataTemplateUtil.getSchema(FruitsRef.class)))));
        responseMetadataMap.put("IntArrayFunc", new DynamicRecordMetadata("IntArrayFunc", Collections.singletonList(new FieldDef<IntegerArray>("value", IntegerArray.class, DataTemplateUtil.getSchema(IntArrayRef.class)))));
        responseMetadataMap.put("IntMapFunc", new DynamicRecordMetadata("IntMapFunc", Collections.singletonList(new FieldDef<IntegerMap>("value", IntegerMap.class, DataTemplateUtil.getSchema(IntMapRef.class)))));
        responseMetadataMap.put("PointRef", new DynamicRecordMetadata("PointRef", Collections.singletonList(new FieldDef<Point>("value", Point.class, DataTemplateUtil.getSchema(PointRef.class)))));
        responseMetadataMap.put("StringFunc", new DynamicRecordMetadata("StringFunc", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(StringRef.class)))));
        responseMetadataMap.put("booleanFunc", new DynamicRecordMetadata("booleanFunc", Collections.singletonList(new FieldDef<Boolean>("value", Boolean.class, DataTemplateUtil.getSchema(BooleanRef.class)))));
        responseMetadataMap.put("booleanFunc2", new DynamicRecordMetadata("booleanFunc2", Collections.singletonList(new FieldDef<Boolean>("value", Boolean.class, DataTemplateUtil.getSchema(BooleanRef.class)))));
        responseMetadataMap.put("doubleFunc", new DynamicRecordMetadata("doubleFunc", Collections.singletonList(new FieldDef<Double>("value", Double.class, DataTemplateUtil.getSchema(DoubleRef.class)))));
        responseMetadataMap.put("doubleFunc2", new DynamicRecordMetadata("doubleFunc2", Collections.singletonList(new FieldDef<Double>("value", Double.class, DataTemplateUtil.getSchema(DoubleRef.class)))));
        responseMetadataMap.put("floatFunc", new DynamicRecordMetadata("floatFunc", Collections.singletonList(new FieldDef<Float>("value", Float.class, DataTemplateUtil.getSchema(FloatRef.class)))));
        responseMetadataMap.put("floatFunc2", new DynamicRecordMetadata("floatFunc2", Collections.singletonList(new FieldDef<Float>("value", Float.class, DataTemplateUtil.getSchema(FloatRef.class)))));
        responseMetadataMap.put("intFunc", new DynamicRecordMetadata("intFunc", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(IntRef.class)))));
        responseMetadataMap.put("intFunc2", new DynamicRecordMetadata("intFunc2", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(IntRef.class)))));
        responseMetadataMap.put("longFunc", new DynamicRecordMetadata("longFunc", Collections.singletonList(new FieldDef<Long>("value", Long.class, DataTemplateUtil.getSchema(LongRef.class)))));
        responseMetadataMap.put("longFunc2", new DynamicRecordMetadata("longFunc2", Collections.singletonList(new FieldDef<Long>("value", Long.class, DataTemplateUtil.getSchema(LongRef.class)))));
        HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo> keyParts = new HashMap<java.lang.String, com.linkedin.restli.common.CompoundKey.TypeInfo>();
        _resourceSpec = new ResourceSpecImpl(EnumSet.noneOf(ResourceMethod.class), requestMetadataMap, responseMetadataMap, Long.class, null, null, TyperefRecord.class, keyParts);
    }

    public TyperefRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public TyperefRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public TyperefDoBytesFuncRequestBuilder actionBytesFunc() {
        return new TyperefDoBytesFuncRequestBuilder(getBaseUriTemplate(), ByteString.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoCustomNonNegativeLongRefRequestBuilder actionCustomNonNegativeLongRef() {
        return new TyperefDoCustomNonNegativeLongRefRequestBuilder(getBaseUriTemplate(), com.linkedin.restli.examples.custom.types.CustomNonNegativeLong.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoFruitsRefRequestBuilder actionFruitsRef() {
        return new TyperefDoFruitsRefRequestBuilder(getBaseUriTemplate(), Fruits.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntArrayFuncRequestBuilder actionIntArrayFunc() {
        return new TyperefDoIntArrayFuncRequestBuilder(getBaseUriTemplate(), IntegerArray.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntMapFuncRequestBuilder actionIntMapFunc() {
        return new TyperefDoIntMapFuncRequestBuilder(getBaseUriTemplate(), IntegerMap.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoPointRefRequestBuilder actionPointRef() {
        return new TyperefDoPointRefRequestBuilder(getBaseUriTemplate(), Point.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoStringFuncRequestBuilder actionStringFunc() {
        return new TyperefDoStringFuncRequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoBooleanFuncRequestBuilder actionBooleanFunc() {
        return new TyperefDoBooleanFuncRequestBuilder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoBooleanFunc2RequestBuilder actionBooleanFunc2() {
        return new TyperefDoBooleanFunc2RequestBuilder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoDoubleFuncRequestBuilder actionDoubleFunc() {
        return new TyperefDoDoubleFuncRequestBuilder(getBaseUriTemplate(), Double.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoDoubleFunc2RequestBuilder actionDoubleFunc2() {
        return new TyperefDoDoubleFunc2RequestBuilder(getBaseUriTemplate(), Double.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoFloatFuncRequestBuilder actionFloatFunc() {
        return new TyperefDoFloatFuncRequestBuilder(getBaseUriTemplate(), Float.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoFloatFunc2RequestBuilder actionFloatFunc2() {
        return new TyperefDoFloatFunc2RequestBuilder(getBaseUriTemplate(), Float.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntFuncRequestBuilder actionIntFunc() {
        return new TyperefDoIntFuncRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntFunc2RequestBuilder actionIntFunc2() {
        return new TyperefDoIntFunc2RequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoLongFuncRequestBuilder actionLongFunc() {
        return new TyperefDoLongFuncRequestBuilder(getBaseUriTemplate(), Long.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoLongFunc2RequestBuilder actionLongFunc2() {
        return new TyperefDoLongFunc2RequestBuilder(getBaseUriTemplate(), Long.class, _resourceSpec, getRequestOptions());
    }

}

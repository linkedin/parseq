
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
import com.linkedin.restli.internal.common.URIParamUtils;


/**
 * Test for typeref param and return types in actions.
 * 
 * generated from: com.linkedin.restli.examples.typeref.server.TyperefTestResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.typeref.client.typeref.restspec.json.", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefBuilders {

    private final java.lang.String _baseUriTemplate;
    private RestliRequestOptions _requestOptions;
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

    public TyperefBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefBuilders(RestliRequestOptions requestOptions) {
        _baseUriTemplate = ORIGINAL_RESOURCE_PATH;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    public TyperefBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public TyperefBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        _baseUriTemplate = primaryResourceName;
        _requestOptions = assignRequestOptions(requestOptions);
    }

    private java.lang.String getBaseUriTemplate() {
        return _baseUriTemplate;
    }

    public RestliRequestOptions getRequestOptions() {
        return _requestOptions;
    }

    public java.lang.String[] getPathComponents() {
        return URIParamUtils.extractPathComponentsFromUriTemplate(_baseUriTemplate);
    }

    private static RestliRequestOptions assignRequestOptions(RestliRequestOptions requestOptions) {
        if (requestOptions == null) {
            return RestliRequestOptions.DEFAULT_OPTIONS;
        } else {
            return requestOptions;
        }
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public TyperefDoBytesFuncBuilder actionBytesFunc() {
        return new TyperefDoBytesFuncBuilder(getBaseUriTemplate(), ByteString.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoCustomNonNegativeLongRefBuilder actionCustomNonNegativeLongRef() {
        return new TyperefDoCustomNonNegativeLongRefBuilder(getBaseUriTemplate(), com.linkedin.restli.examples.custom.types.CustomNonNegativeLong.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoFruitsRefBuilder actionFruitsRef() {
        return new TyperefDoFruitsRefBuilder(getBaseUriTemplate(), Fruits.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntArrayFuncBuilder actionIntArrayFunc() {
        return new TyperefDoIntArrayFuncBuilder(getBaseUriTemplate(), IntegerArray.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntMapFuncBuilder actionIntMapFunc() {
        return new TyperefDoIntMapFuncBuilder(getBaseUriTemplate(), IntegerMap.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoPointRefBuilder actionPointRef() {
        return new TyperefDoPointRefBuilder(getBaseUriTemplate(), Point.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoStringFuncBuilder actionStringFunc() {
        return new TyperefDoStringFuncBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoBooleanFuncBuilder actionBooleanFunc() {
        return new TyperefDoBooleanFuncBuilder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoBooleanFunc2Builder actionBooleanFunc2() {
        return new TyperefDoBooleanFunc2Builder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoDoubleFuncBuilder actionDoubleFunc() {
        return new TyperefDoDoubleFuncBuilder(getBaseUriTemplate(), Double.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoDoubleFunc2Builder actionDoubleFunc2() {
        return new TyperefDoDoubleFunc2Builder(getBaseUriTemplate(), Double.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoFloatFuncBuilder actionFloatFunc() {
        return new TyperefDoFloatFuncBuilder(getBaseUriTemplate(), Float.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoFloatFunc2Builder actionFloatFunc2() {
        return new TyperefDoFloatFunc2Builder(getBaseUriTemplate(), Float.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntFuncBuilder actionIntFunc() {
        return new TyperefDoIntFuncBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoIntFunc2Builder actionIntFunc2() {
        return new TyperefDoIntFunc2Builder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoLongFuncBuilder actionLongFunc() {
        return new TyperefDoLongFuncBuilder(getBaseUriTemplate(), Long.class, _resourceSpec, getRequestOptions());
    }

    public TyperefDoLongFunc2Builder actionLongFunc2() {
        return new TyperefDoLongFunc2Builder(getBaseUriTemplate(), Long.class, _resourceSpec, getRequestOptions());
    }

}

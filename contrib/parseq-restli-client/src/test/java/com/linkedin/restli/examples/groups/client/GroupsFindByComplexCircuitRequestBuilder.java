
package com.linkedin.restli.examples.groups.client;

import javax.annotation.Generated;
import com.linkedin.data.ByteString;
import com.linkedin.data.template.DoubleArray;
import com.linkedin.data.template.IntegerArray;
import com.linkedin.data.template.IntegerMap;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.groups.api.Group;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;
import com.linkedin.restli.examples.groups.api.GroupMembershipParamArray;
import com.linkedin.restli.examples.typeref.api.Fixed16;
import com.linkedin.restli.examples.typeref.api.Union;
import com.linkedin.restli.examples.typeref.api.UnionArray;


/**
 * Test the default value for various types
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupsFindByComplexCircuitRequestBuilder
    extends FindRequestBuilderBase<Integer, Group, GroupsFindByComplexCircuitRequestBuilder>
{


    public GroupsFindByComplexCircuitRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Group.class, resourceSpec, requestOptions);
        super.name("complexCircuit");
    }

    public GroupsFindByComplexCircuitRequestBuilder nativeArrayParam(DoubleArray value) {
        super.setParam("nativeArray", value, DoubleArray.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder nativeArrayParam(Iterable<Double> value) {
        super.setParam("nativeArray", value, Double.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder addNativeArrayParam(Double value) {
        super.addParam("nativeArray", value, Double.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder coercedArrayParam(IntegerArray value) {
        super.setParam("coercedArray", value, IntegerArray.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder coercedArrayParam(Iterable<Integer> value) {
        super.setParam("coercedArray", value, Integer.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder addCoercedArrayParam(Integer value) {
        super.addParam("coercedArray", value, Integer.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder wrappedArrayParam(IntegerArray value) {
        super.setParam("wrappedArray", value, IntegerArray.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder wrappedArrayParam(Iterable<Integer> value) {
        super.setParam("wrappedArray", value, Integer.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder addWrappedArrayParam(Integer value) {
        super.addParam("wrappedArray", value, Integer.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder wrappedMapParam(IntegerMap value) {
        super.setParam("wrappedMap", value, IntegerMap.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder bytesParam(ByteString value) {
        super.setParam("bytes", value, ByteString.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder fixedParam(Fixed16 value) {
        super.setParam("fixed", value, Fixed16 .class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder unionParam(Union value) {
        super.setParam("union", value, Union.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder unionArrayParam(UnionArray value) {
        super.setParam("unionArray", value, UnionArray.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder unionArrayParam(Iterable<Union> value) {
        super.setParam("unionArray", value, Union.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder addUnionArrayParam(Union value) {
        super.addParam("unionArray", value, Union.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder recordParam(GroupMembershipParam value) {
        super.setParam("record", value, GroupMembershipParam.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder recordsParam(GroupMembershipParamArray value) {
        super.setParam("records", value, GroupMembershipParamArray.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder recordsParam(Iterable<GroupMembershipParam> value) {
        super.setParam("records", value, GroupMembershipParam.class);
        return this;
    }

    public GroupsFindByComplexCircuitRequestBuilder addRecordsParam(GroupMembershipParam value) {
        super.addParam("records", value, GroupMembershipParam.class);
        return this;
    }

}

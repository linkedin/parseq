
package com.linkedin.restli.examples.typeref.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.ActionRequestBuilderBase;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.typeref.api.Point;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class TyperefDoPointRefBuilder
    extends ActionRequestBuilderBase<Void, Point, TyperefDoPointRefBuilder>
{


    public TyperefDoPointRefBuilder(String baseUriTemplate, Class<Point> returnClass, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, returnClass, resourceSpec, requestOptions);
        super.name("PointRef");
    }

    public TyperefDoPointRefBuilder paramArg1(Point value) {
        super.setReqParam(_resourceSpec.getRequestMetadata("PointRef").getFieldDef("arg1"), value);
        return this;
    }

}

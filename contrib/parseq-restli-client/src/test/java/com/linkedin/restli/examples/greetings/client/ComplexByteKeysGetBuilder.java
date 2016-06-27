
package com.linkedin.restli.examples.greetings.client;

import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;
import com.linkedin.restli.examples.typeref.api.TyperefRecord;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ComplexByteKeysGetBuilder
    extends GetRequestBuilderBase<ComplexResourceKey<TyperefRecord, TwoPartKey> , TyperefRecord, ComplexByteKeysGetBuilder>
{


    public ComplexByteKeysGetBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, TyperefRecord.class, resourceSpec, requestOptions);
    }

}

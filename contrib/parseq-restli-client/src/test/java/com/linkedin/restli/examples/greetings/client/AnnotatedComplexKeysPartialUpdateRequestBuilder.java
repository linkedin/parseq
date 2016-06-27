
package com.linkedin.restli.examples.greetings.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.schema.validation.ValidationResult;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.PartialUpdateRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.validation.RestLiDataValidator;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.api.TwoPartKey;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AnnotatedComplexKeysPartialUpdateRequestBuilder
    extends PartialUpdateRequestBuilderBase<ComplexResourceKey<TwoPartKey, TwoPartKey> , Message, AnnotatedComplexKeysPartialUpdateRequestBuilder>
{


    public AnnotatedComplexKeysPartialUpdateRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
    }

    public static ValidationResult validateInput(PatchRequest<Message> patch) {
        Map<String, List<String>> annotations = new HashMap<String, List<String>>();
        RestLiDataValidator validator = new RestLiDataValidator(annotations, Message.class, ResourceMethod.PARTIAL_UPDATE);
        return validator.validateInput(patch);
    }

}

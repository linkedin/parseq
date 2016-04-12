
package com.linkedin.restli.examples.greetings.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.schema.validation.ValidationResult;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.PartialUpdateRequestBuilderBase;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.validation.RestLiDataValidator;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:22 PDT 2016")
public class GreetingsAuthPartialUpdateRequestBuilder
    extends PartialUpdateRequestBuilderBase<Long, Greeting, GreetingsAuthPartialUpdateRequestBuilder>
{


    public GreetingsAuthPartialUpdateRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
    }

    public static ValidationResult validateInput(PatchRequest<Greeting> patch) {
        Map<java.lang.String, List<java.lang.String>> annotations = new HashMap<java.lang.String, List<java.lang.String>>();
        RestLiDataValidator validator = new RestLiDataValidator(annotations, Greeting.class, ResourceMethod.PARTIAL_UPDATE);
        return validator.validateInput(patch);
    }

    public GreetingsAuthPartialUpdateRequestBuilder authParam(java.lang.String value) {
        super.setParam("auth", value, java.lang.String.class);
        return this;
    }

}

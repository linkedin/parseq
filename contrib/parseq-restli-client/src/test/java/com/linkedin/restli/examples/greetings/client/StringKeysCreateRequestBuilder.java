
package com.linkedin.restli.examples.greetings.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.schema.validation.ValidationResult;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.CreateIdRequestBuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.validation.RestLiDataValidator;
import com.linkedin.restli.examples.greetings.api.Message;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:23 PDT 2016")
public class StringKeysCreateRequestBuilder
    extends CreateIdRequestBuilderBase<java.lang.String, Message, StringKeysCreateRequestBuilder>
{


    public StringKeysCreateRequestBuilder(java.lang.String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Message.class, resourceSpec, requestOptions);
    }

    public static ValidationResult validateInput(Message input) {
        Map<java.lang.String, List<java.lang.String>> annotations = new HashMap<java.lang.String, List<java.lang.String>>();
        RestLiDataValidator validator = new RestLiDataValidator(annotations, Message.class, ResourceMethod.CREATE);
        return validator.validateInput(input);
    }

}

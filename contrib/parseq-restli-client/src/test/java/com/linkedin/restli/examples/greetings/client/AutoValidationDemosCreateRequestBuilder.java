
package com.linkedin.restli.examples.greetings.client;

import java.util.Arrays;
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
import com.linkedin.restli.examples.greetings.api.ValidationDemo;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:20 PDT 2016")
public class AutoValidationDemosCreateRequestBuilder
    extends CreateIdRequestBuilderBase<Integer, ValidationDemo, AutoValidationDemosCreateRequestBuilder>
{


    public AutoValidationDemosCreateRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ValidationDemo.class, resourceSpec, requestOptions);
    }

    public static ValidationResult validateInput(ValidationDemo input) {
        Map<String, List<String>> annotations = new HashMap<String, List<String>>();
        annotations.put("createOnly", Arrays.asList("stringB", "intB", "UnionFieldWithInlineRecord/com.linkedin.restli.examples.greetings.api.myRecord/foo2", "MapWithTyperefs/*/id"));
        annotations.put("readOnly", Arrays.asList("stringA", "intA", "UnionFieldWithInlineRecord/com.linkedin.restli.examples.greetings.api.myRecord/foo1", "ArrayWithInlineRecord/*/bar1", "validationDemoNext/stringB", "validationDemoNext/UnionFieldWithInlineRecord"));
        RestLiDataValidator validator = new RestLiDataValidator(annotations, ValidationDemo.class, ResourceMethod.CREATE);
        return validator.validateInput(input);
    }

}


package com.linkedin.restli.examples.greetings.client;

import java.util.Date;
import javax.annotation.Generated;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.FindRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.examples.greetings.api.Greeting;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:21 PDT 2016")
public class ChainedTyperefsFindByDateOnlyRequestBuilder
    extends FindRequestBuilderBase<CompoundKey, Greeting, ChainedTyperefsFindByDateOnlyRequestBuilder>
{


    public ChainedTyperefsFindByDateOnlyRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, Greeting.class, resourceSpec, requestOptions);
        super.name("dateOnly");
    }

    public ChainedTyperefsFindByDateOnlyRequestBuilder birthdayKey(Date key) {
        super.assocKey("birthday", key);
        return this;
    }

}

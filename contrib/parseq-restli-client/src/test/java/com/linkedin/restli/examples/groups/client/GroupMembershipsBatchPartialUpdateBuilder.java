
package com.linkedin.restli.examples.groups.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.schema.validation.ValidationResult;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BatchPartialUpdateRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.validation.RestLiDataValidator;
import com.linkedin.restli.examples.groups.api.GroupMembership;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsBatchPartialUpdateBuilder
    extends BatchPartialUpdateRequestBuilderBase<CompoundKey, GroupMembership, GroupMembershipsBatchPartialUpdateBuilder>
{


    public GroupMembershipsBatchPartialUpdateBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, GroupMembership.class, resourceSpec, requestOptions);
    }

    public static ValidationResult validateInput(PatchRequest<GroupMembership> patch) {
        Map<String, List<String>> annotations = new HashMap<String, List<String>>();
        RestLiDataValidator validator = new RestLiDataValidator(annotations, GroupMembership.class, ResourceMethod.BATCH_PARTIAL_UPDATE);
        return validator.validateInput(patch);
    }

}

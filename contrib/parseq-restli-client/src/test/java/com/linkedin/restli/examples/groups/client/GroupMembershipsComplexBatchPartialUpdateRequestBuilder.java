
package com.linkedin.restli.examples.groups.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.linkedin.data.schema.validation.ValidationResult;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BatchPartialUpdateRequestBuilderBase;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.validation.RestLiDataValidator;
import com.linkedin.restli.examples.groups.api.ComplexKeyGroupMembership;
import com.linkedin.restli.examples.groups.api.GroupMembershipKey;
import com.linkedin.restli.examples.groups.api.GroupMembershipParam;

@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder", date = "Thu Mar 31 14:16:24 PDT 2016")
public class GroupMembershipsComplexBatchPartialUpdateRequestBuilder
    extends BatchPartialUpdateRequestBuilderBase<ComplexResourceKey<GroupMembershipKey, GroupMembershipParam> , ComplexKeyGroupMembership, GroupMembershipsComplexBatchPartialUpdateRequestBuilder>
{


    public GroupMembershipsComplexBatchPartialUpdateRequestBuilder(String baseUriTemplate, ResourceSpec resourceSpec, RestliRequestOptions requestOptions) {
        super(baseUriTemplate, ComplexKeyGroupMembership.class, resourceSpec, requestOptions);
    }

    public static ValidationResult validateInput(PatchRequest<ComplexKeyGroupMembership> patch) {
        Map<String, List<String>> annotations = new HashMap<String, List<String>>();
        RestLiDataValidator validator = new RestLiDataValidator(annotations, ComplexKeyGroupMembership.class, ResourceMethod.BATCH_PARTIAL_UPDATE);
        return validator.validateInput(patch);
    }

}

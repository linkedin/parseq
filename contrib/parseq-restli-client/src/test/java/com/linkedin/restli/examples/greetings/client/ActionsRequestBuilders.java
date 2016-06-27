
package com.linkedin.restli.examples.greetings.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import javax.annotation.Generated;
import com.linkedin.data.template.DataTemplateUtil;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.data.template.FieldDef;
import com.linkedin.data.template.IntegerArray;
import com.linkedin.data.template.StringArray;
import com.linkedin.restli.client.OptionsRequestBuilder;
import com.linkedin.restli.client.RestliRequestOptions;
import com.linkedin.restli.client.base.BuilderBase;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import com.linkedin.restli.examples.greetings.api.Message;


/**
 * Various action tasks that demonstrate usual behavior, timeout, and exceptions.
 * 
 * generated from: com.linkedin.restli.examples.greetings.server.ActionsResource
 * 
 */
@Generated(value = "com.linkedin.pegasus.generator.JavaCodeUtil", comments = "Rest.li Request Builder. Generated from /Users/jodzga/dev/pegasus_trunk/pegasus/restli-int-test-api/src/main/idl/com.linkedin.restli.examples.greetings.client.actions.restspec.json.", date = "Thu Mar 31 14:16:19 PDT 2016")
public class ActionsRequestBuilders
    extends BuilderBase
{

    private final static java.lang.String ORIGINAL_RESOURCE_PATH = "actions";
    private final static ResourceSpec _resourceSpec;

    static {
        HashMap<java.lang.String, DynamicRecordMetadata> requestMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        ArrayList<FieldDef<?>> arrayPromiseParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("arrayPromise", new DynamicRecordMetadata("arrayPromise", arrayPromiseParams));
        ArrayList<FieldDef<?>> echoParams = new ArrayList<FieldDef<?>>();
        echoParams.add(new FieldDef<java.lang.String>("input", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        requestMetadataMap.put("echo", new DynamicRecordMetadata("echo", echoParams));
        ArrayList<FieldDef<?>> echoMessageParams = new ArrayList<FieldDef<?>>();
        echoMessageParams.add(new FieldDef<Message>("message", Message.class, DataTemplateUtil.getSchema(Message.class)));
        requestMetadataMap.put("echoMessage", new DynamicRecordMetadata("echoMessage", echoMessageParams));
        ArrayList<FieldDef<?>> echoMessageArrayParams = new ArrayList<FieldDef<?>>();
        echoMessageArrayParams.add(new FieldDef<com.linkedin.restli.examples.greetings.api.MessageArray>("messages", com.linkedin.restli.examples.greetings.api.MessageArray.class, DataTemplateUtil.getSchema(com.linkedin.restli.examples.greetings.api.MessageArray.class)));
        requestMetadataMap.put("echoMessageArray", new DynamicRecordMetadata("echoMessageArray", echoMessageArrayParams));
        ArrayList<FieldDef<?>> echoStringArrayParams = new ArrayList<FieldDef<?>>();
        echoStringArrayParams.add(new FieldDef<StringArray>("strings", StringArray.class, DataTemplateUtil.getSchema(StringArray.class)));
        requestMetadataMap.put("echoStringArray", new DynamicRecordMetadata("echoStringArray", echoStringArrayParams));
        ArrayList<FieldDef<?>> echoToneArrayParams = new ArrayList<FieldDef<?>>();
        echoToneArrayParams.add(new FieldDef<com.linkedin.restli.examples.greetings.api.ToneArray>("tones", com.linkedin.restli.examples.greetings.api.ToneArray.class, DataTemplateUtil.getSchema(com.linkedin.restli.examples.greetings.api.ToneArray.class)));
        requestMetadataMap.put("echoToneArray", new DynamicRecordMetadata("echoToneArray", echoToneArrayParams));
        ArrayList<FieldDef<?>> failCallbackCallParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failCallbackCall", new DynamicRecordMetadata("failCallbackCall", failCallbackCallParams));
        ArrayList<FieldDef<?>> failCallbackThrowParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failCallbackThrow", new DynamicRecordMetadata("failCallbackThrow", failCallbackThrowParams));
        ArrayList<FieldDef<?>> failPromiseCallParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failPromiseCall", new DynamicRecordMetadata("failPromiseCall", failPromiseCallParams));
        ArrayList<FieldDef<?>> failPromiseThrowParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failPromiseThrow", new DynamicRecordMetadata("failPromiseThrow", failPromiseThrowParams));
        ArrayList<FieldDef<?>> failTaskCallParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failTaskCall", new DynamicRecordMetadata("failTaskCall", failTaskCallParams));
        ArrayList<FieldDef<?>> failTaskThrowParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failTaskThrow", new DynamicRecordMetadata("failTaskThrow", failTaskThrowParams));
        ArrayList<FieldDef<?>> failThrowInTaskParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("failThrowInTask", new DynamicRecordMetadata("failThrowInTask", failThrowInTaskParams));
        ArrayList<FieldDef<?>> getParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("get", new DynamicRecordMetadata("get", getParams));
        ArrayList<FieldDef<?>> nullPromiseParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("nullPromise", new DynamicRecordMetadata("nullPromise", nullPromiseParams));
        ArrayList<FieldDef<?>> nullTaskParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("nullTask", new DynamicRecordMetadata("nullTask", nullTaskParams));
        ArrayList<FieldDef<?>> parseqParams = new ArrayList<FieldDef<?>>();
        parseqParams.add(new FieldDef<Integer>("a", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        parseqParams.add(new FieldDef<java.lang.String>("b", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        parseqParams.add(new FieldDef<Boolean>("c", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)));
        requestMetadataMap.put("parseq", new DynamicRecordMetadata("parseq", parseqParams));
        ArrayList<FieldDef<?>> parseq3Params = new ArrayList<FieldDef<?>>();
        parseq3Params.add(new FieldDef<Integer>("a", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        parseq3Params.add(new FieldDef<java.lang.String>("b", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)));
        parseq3Params.add(new FieldDef<Boolean>("c", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)));
        requestMetadataMap.put("parseq3", new DynamicRecordMetadata("parseq3", parseq3Params));
        ArrayList<FieldDef<?>> returnBoolParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnBool", new DynamicRecordMetadata("returnBool", returnBoolParams));
        ArrayList<FieldDef<?>> returnBoolOptionalParamParams = new ArrayList<FieldDef<?>>();
        returnBoolOptionalParamParams.add(new FieldDef<Boolean>("param", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)));
        requestMetadataMap.put("returnBoolOptionalParam", new DynamicRecordMetadata("returnBoolOptionalParam", returnBoolOptionalParamParams));
        ArrayList<FieldDef<?>> returnIntParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnInt", new DynamicRecordMetadata("returnInt", returnIntParams));
        ArrayList<FieldDef<?>> returnIntOptionalParamParams = new ArrayList<FieldDef<?>>();
        returnIntOptionalParamParams.add(new FieldDef<Integer>("param", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        requestMetadataMap.put("returnIntOptionalParam", new DynamicRecordMetadata("returnIntOptionalParam", returnIntOptionalParamParams));
        ArrayList<FieldDef<?>> returnVoidParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("returnVoid", new DynamicRecordMetadata("returnVoid", returnVoidParams));
        ArrayList<FieldDef<?>> taskCreationDelayParams = new ArrayList<FieldDef<?>>();
        taskCreationDelayParams.add(new FieldDef<Integer>("delay", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        requestMetadataMap.put("taskCreationDelay", new DynamicRecordMetadata("taskCreationDelay", taskCreationDelayParams));
        ArrayList<FieldDef<?>> taskExecutionDelayParams = new ArrayList<FieldDef<?>>();
        taskExecutionDelayParams.add(new FieldDef<Integer>("delay", Integer.class, DataTemplateUtil.getSchema(Integer.class)));
        requestMetadataMap.put("taskExecutionDelay", new DynamicRecordMetadata("taskExecutionDelay", taskExecutionDelayParams));
        ArrayList<FieldDef<?>> timeoutParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("timeout", new DynamicRecordMetadata("timeout", timeoutParams));
        ArrayList<FieldDef<?>> timeoutCallbackParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("timeoutCallback", new DynamicRecordMetadata("timeoutCallback", timeoutCallbackParams));
        ArrayList<FieldDef<?>> ultimateAnswerParams = new ArrayList<FieldDef<?>>();
        requestMetadataMap.put("ultimateAnswer", new DynamicRecordMetadata("ultimateAnswer", ultimateAnswerParams));
        HashMap<java.lang.String, DynamicRecordMetadata> responseMetadataMap = new HashMap<java.lang.String, DynamicRecordMetadata>();
        responseMetadataMap.put("arrayPromise", new DynamicRecordMetadata("arrayPromise", Collections.singletonList(new FieldDef<IntegerArray>("value", IntegerArray.class, DataTemplateUtil.getSchema(IntegerArray.class)))));
        responseMetadataMap.put("echo", new DynamicRecordMetadata("echo", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        responseMetadataMap.put("echoMessage", new DynamicRecordMetadata("echoMessage", Collections.singletonList(new FieldDef<Message>("value", Message.class, DataTemplateUtil.getSchema(Message.class)))));
        responseMetadataMap.put("echoMessageArray", new DynamicRecordMetadata("echoMessageArray", Collections.singletonList(new FieldDef<com.linkedin.restli.examples.greetings.api.MessageArray>("value", com.linkedin.restli.examples.greetings.api.MessageArray.class, DataTemplateUtil.getSchema(com.linkedin.restli.examples.greetings.api.MessageArray.class)))));
        responseMetadataMap.put("echoStringArray", new DynamicRecordMetadata("echoStringArray", Collections.singletonList(new FieldDef<StringArray>("value", StringArray.class, DataTemplateUtil.getSchema(StringArray.class)))));
        responseMetadataMap.put("echoToneArray", new DynamicRecordMetadata("echoToneArray", Collections.singletonList(new FieldDef<com.linkedin.restli.examples.greetings.api.ToneArray>("value", com.linkedin.restli.examples.greetings.api.ToneArray.class, DataTemplateUtil.getSchema(com.linkedin.restli.examples.greetings.api.ToneArray.class)))));
        responseMetadataMap.put("failCallbackCall", new DynamicRecordMetadata("failCallbackCall", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("failCallbackThrow", new DynamicRecordMetadata("failCallbackThrow", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("failPromiseCall", new DynamicRecordMetadata("failPromiseCall", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("failPromiseThrow", new DynamicRecordMetadata("failPromiseThrow", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("failTaskCall", new DynamicRecordMetadata("failTaskCall", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("failTaskThrow", new DynamicRecordMetadata("failTaskThrow", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("failThrowInTask", new DynamicRecordMetadata("failThrowInTask", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("get", new DynamicRecordMetadata("get", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        responseMetadataMap.put("nullPromise", new DynamicRecordMetadata("nullPromise", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        responseMetadataMap.put("nullTask", new DynamicRecordMetadata("nullTask", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        responseMetadataMap.put("parseq", new DynamicRecordMetadata("parseq", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        responseMetadataMap.put("parseq3", new DynamicRecordMetadata("parseq3", Collections.singletonList(new FieldDef<java.lang.String>("value", java.lang.String.class, DataTemplateUtil.getSchema(java.lang.String.class)))));
        responseMetadataMap.put("returnBool", new DynamicRecordMetadata("returnBool", Collections.singletonList(new FieldDef<Boolean>("value", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)))));
        responseMetadataMap.put("returnBoolOptionalParam", new DynamicRecordMetadata("returnBoolOptionalParam", Collections.singletonList(new FieldDef<Boolean>("value", Boolean.class, DataTemplateUtil.getSchema(Boolean.class)))));
        responseMetadataMap.put("returnInt", new DynamicRecordMetadata("returnInt", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("returnIntOptionalParam", new DynamicRecordMetadata("returnIntOptionalParam", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        responseMetadataMap.put("returnVoid", new DynamicRecordMetadata("returnVoid", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("taskCreationDelay", new DynamicRecordMetadata("taskCreationDelay", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("taskExecutionDelay", new DynamicRecordMetadata("taskExecutionDelay", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("timeout", new DynamicRecordMetadata("timeout", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("timeoutCallback", new DynamicRecordMetadata("timeoutCallback", Collections.<FieldDef<?>>emptyList()));
        responseMetadataMap.put("ultimateAnswer", new DynamicRecordMetadata("ultimateAnswer", Collections.singletonList(new FieldDef<Integer>("value", Integer.class, DataTemplateUtil.getSchema(Integer.class)))));
        _resourceSpec = new ResourceSpecImpl(EnumSet.noneOf(ResourceMethod.class), requestMetadataMap, responseMetadataMap, Void.class, null, null, null, Collections.<String, Class<?>>emptyMap());
    }

    public ActionsRequestBuilders() {
        this(RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ActionsRequestBuilders(RestliRequestOptions requestOptions) {
        super(ORIGINAL_RESOURCE_PATH, requestOptions);
    }

    public ActionsRequestBuilders(java.lang.String primaryResourceName) {
        this(primaryResourceName, RestliRequestOptions.DEFAULT_OPTIONS);
    }

    public ActionsRequestBuilders(java.lang.String primaryResourceName, RestliRequestOptions requestOptions) {
        super(primaryResourceName, requestOptions);
    }

    public static java.lang.String getPrimaryResource() {
        return ORIGINAL_RESOURCE_PATH;
    }

    public OptionsRequestBuilder options() {
        return new OptionsRequestBuilder(getBaseUriTemplate(), getRequestOptions());
    }

    public ActionsDoArrayPromiseRequestBuilder actionArrayPromise() {
        return new ActionsDoArrayPromiseRequestBuilder(getBaseUriTemplate(), IntegerArray.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoEchoRequestBuilder actionEcho() {
        return new ActionsDoEchoRequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoEchoMessageRequestBuilder actionEchoMessage() {
        return new ActionsDoEchoMessageRequestBuilder(getBaseUriTemplate(), Message.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoEchoMessageArrayRequestBuilder actionEchoMessageArray() {
        return new ActionsDoEchoMessageArrayRequestBuilder(getBaseUriTemplate(), com.linkedin.restli.examples.greetings.api.MessageArray.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoEchoStringArrayRequestBuilder actionEchoStringArray() {
        return new ActionsDoEchoStringArrayRequestBuilder(getBaseUriTemplate(), StringArray.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoEchoToneArrayRequestBuilder actionEchoToneArray() {
        return new ActionsDoEchoToneArrayRequestBuilder(getBaseUriTemplate(), com.linkedin.restli.examples.greetings.api.ToneArray.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by calling the callback
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailCallbackCallRequestBuilder actionFailCallbackCall() {
        return new ActionsDoFailCallbackCallRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by throwing an exception
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailCallbackThrowRequestBuilder actionFailCallbackThrow() {
        return new ActionsDoFailCallbackThrowRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by calling SettablePromise.fail
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailPromiseCallRequestBuilder actionFailPromiseCall() {
        return new ActionsDoFailPromiseCallRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by throwing an exception, returning a promise
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailPromiseThrowRequestBuilder actionFailPromiseThrow() {
        return new ActionsDoFailPromiseThrowRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by calling SettablePromise.fail promise in a task
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailTaskCallRequestBuilder actionFailTaskCall() {
        return new ActionsDoFailTaskCallRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by throwing an exception, returning a task
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailTaskThrowRequestBuilder actionFailTaskThrow() {
        return new ActionsDoFailTaskThrowRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Action that fails by throwing an exception in the task
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoFailThrowInTaskRequestBuilder actionFailThrowInTask() {
        return new ActionsDoFailThrowInTaskRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoGetRequestBuilder actionGet() {
        return new ActionsDoGetRequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoNullPromiseRequestBuilder actionNullPromise() {
        return new ActionsDoNullPromiseRequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoNullTaskRequestBuilder actionNullTask() {
        return new ActionsDoNullTaskRequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Performs three "slow" tasks and collects the results. This uses the passed context
     *  parameter to execute tasks. The position of the context argument is arbitrary.
     * Service Returns: Concatenation of binary representation of a, all caps of b, and string value
     * of c
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoParseqRequestBuilder actionParseq() {
        return new ActionsDoParseqRequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Performs three "slow" tasks and collects the results. This returns a task and lets
     *  the RestLi server invoke it.
     * Service Returns: Concatenation of binary representation of a, all caps of b, and string value
     * of c
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoParseq3RequestBuilder actionParseq3() {
        return new ActionsDoParseq3RequestBuilder(getBaseUriTemplate(), java.lang.String.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoReturnBoolRequestBuilder actionReturnBool() {
        return new ActionsDoReturnBoolRequestBuilder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoReturnBoolOptionalParamRequestBuilder actionReturnBoolOptionalParam() {
        return new ActionsDoReturnBoolOptionalParamRequestBuilder(getBaseUriTemplate(), Boolean.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoReturnIntRequestBuilder actionReturnInt() {
        return new ActionsDoReturnIntRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoReturnIntOptionalParamRequestBuilder actionReturnIntOptionalParam() {
        return new ActionsDoReturnIntOptionalParamRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoReturnVoidRequestBuilder actionReturnVoid() {
        return new ActionsDoReturnVoidRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Simulates a delay in an asynchronous resource caused by ParSeq execution plan creation. The delay is simulated as
     *  {@link Thread#sleep(long)} because execution plan creation is a synchronous operation.
     * Service Returns: Nothing
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoTaskCreationDelayRequestBuilder actionTaskCreationDelay() {
        return new ActionsDoTaskCreationDelayRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    /**
     * Simulates a delay in an asynchronous resource. The delay is simulated using a scheduled task (asynchronously).
     *  That is how a typical async resource looks like in terms of delays.
     * Service Returns: Nothing
     * 
     * @return
     *     builder for the resource method
     */
    public ActionsDoTaskExecutionDelayRequestBuilder actionTaskExecutionDelay() {
        return new ActionsDoTaskExecutionDelayRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoTimeoutRequestBuilder actionTimeout() {
        return new ActionsDoTimeoutRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoTimeoutCallbackRequestBuilder actionTimeoutCallback() {
        return new ActionsDoTimeoutCallbackRequestBuilder(getBaseUriTemplate(), Void.class, _resourceSpec, getRequestOptions());
    }

    public ActionsDoUltimateAnswerRequestBuilder actionUltimateAnswer() {
        return new ActionsDoUltimateAnswerRequestBuilder(getBaseUriTemplate(), Integer.class, _resourceSpec, getRequestOptions());
    }

}

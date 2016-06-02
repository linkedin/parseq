// Generated from com/linkedin/restli/client/config/ResourceConfigKey.g4 by ANTLR 4.5
package com.linkedin.restli.client.config;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ResourceConfigKeyParser}.
 */
public interface ResourceConfigKeyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#key}.
	 * @param ctx the parse tree
	 */
	void enterKey(ResourceConfigKeyParser.KeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#key}.
	 * @param ctx the parse tree
	 */
	void exitKey(ResourceConfigKeyParser.KeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#inbound}.
	 * @param ctx the parse tree
	 */
	void enterInbound(ResourceConfigKeyParser.InboundContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#inbound}.
	 * @param ctx the parse tree
	 */
	void exitInbound(ResourceConfigKeyParser.InboundContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#outbound}.
	 * @param ctx the parse tree
	 */
	void enterOutbound(ResourceConfigKeyParser.OutboundContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#outbound}.
	 * @param ctx the parse tree
	 */
	void exitOutbound(ResourceConfigKeyParser.OutboundContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#operationIn}.
	 * @param ctx the parse tree
	 */
	void enterOperationIn(ResourceConfigKeyParser.OperationInContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#operationIn}.
	 * @param ctx the parse tree
	 */
	void exitOperationIn(ResourceConfigKeyParser.OperationInContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#operationOut}.
	 * @param ctx the parse tree
	 */
	void enterOperationOut(ResourceConfigKeyParser.OperationOutContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#operationOut}.
	 * @param ctx the parse tree
	 */
	void exitOperationOut(ResourceConfigKeyParser.OperationOutContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#simpleOp}.
	 * @param ctx the parse tree
	 */
	void enterSimpleOp(ResourceConfigKeyParser.SimpleOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#simpleOp}.
	 * @param ctx the parse tree
	 */
	void exitSimpleOp(ResourceConfigKeyParser.SimpleOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#httpExtraOp}.
	 * @param ctx the parse tree
	 */
	void enterHttpExtraOp(ResourceConfigKeyParser.HttpExtraOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#httpExtraOp}.
	 * @param ctx the parse tree
	 */
	void exitHttpExtraOp(ResourceConfigKeyParser.HttpExtraOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#complex}.
	 * @param ctx the parse tree
	 */
	void enterComplex(ResourceConfigKeyParser.ComplexContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#complex}.
	 * @param ctx the parse tree
	 */
	void exitComplex(ResourceConfigKeyParser.ComplexContext ctx);
	/**
	 * Enter a parse tree produced by {@link ResourceConfigKeyParser#complexOp}.
	 * @param ctx the parse tree
	 */
	void enterComplexOp(ResourceConfigKeyParser.ComplexOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link ResourceConfigKeyParser#complexOp}.
	 * @param ctx the parse tree
	 */
	void exitComplexOp(ResourceConfigKeyParser.ComplexOpContext ctx);
}
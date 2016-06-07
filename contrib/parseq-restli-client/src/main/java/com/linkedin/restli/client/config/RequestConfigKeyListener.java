// Generated from com/linkedin/restli/client/config/RequestConfigKey.g4 by ANTLR 4.5
package com.linkedin.restli.client.config;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RequestConfigKeyParser}.
 */
public interface RequestConfigKeyListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#key}.
	 * @param ctx the parse tree
	 */
	void enterKey(RequestConfigKeyParser.KeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#key}.
	 * @param ctx the parse tree
	 */
	void exitKey(RequestConfigKeyParser.KeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#inbound}.
	 * @param ctx the parse tree
	 */
	void enterInbound(RequestConfigKeyParser.InboundContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#inbound}.
	 * @param ctx the parse tree
	 */
	void exitInbound(RequestConfigKeyParser.InboundContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#outbound}.
	 * @param ctx the parse tree
	 */
	void enterOutbound(RequestConfigKeyParser.OutboundContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#outbound}.
	 * @param ctx the parse tree
	 */
	void exitOutbound(RequestConfigKeyParser.OutboundContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#operationIn}.
	 * @param ctx the parse tree
	 */
	void enterOperationIn(RequestConfigKeyParser.OperationInContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#operationIn}.
	 * @param ctx the parse tree
	 */
	void exitOperationIn(RequestConfigKeyParser.OperationInContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#operationOut}.
	 * @param ctx the parse tree
	 */
	void enterOperationOut(RequestConfigKeyParser.OperationOutContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#operationOut}.
	 * @param ctx the parse tree
	 */
	void exitOperationOut(RequestConfigKeyParser.OperationOutContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#simpleOp}.
	 * @param ctx the parse tree
	 */
	void enterSimpleOp(RequestConfigKeyParser.SimpleOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#simpleOp}.
	 * @param ctx the parse tree
	 */
	void exitSimpleOp(RequestConfigKeyParser.SimpleOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#httpExtraOp}.
	 * @param ctx the parse tree
	 */
	void enterHttpExtraOp(RequestConfigKeyParser.HttpExtraOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#httpExtraOp}.
	 * @param ctx the parse tree
	 */
	void exitHttpExtraOp(RequestConfigKeyParser.HttpExtraOpContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#complex}.
	 * @param ctx the parse tree
	 */
	void enterComplex(RequestConfigKeyParser.ComplexContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#complex}.
	 * @param ctx the parse tree
	 */
	void exitComplex(RequestConfigKeyParser.ComplexContext ctx);
	/**
	 * Enter a parse tree produced by {@link RequestConfigKeyParser#complexOp}.
	 * @param ctx the parse tree
	 */
	void enterComplexOp(RequestConfigKeyParser.ComplexOpContext ctx);
	/**
	 * Exit a parse tree produced by {@link RequestConfigKeyParser#complexOp}.
	 * @param ctx the parse tree
	 */
	void exitComplexOp(RequestConfigKeyParser.ComplexOpContext ctx);
}
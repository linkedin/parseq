// Generated from com/linkedin/restli/client/config/ResourceConfigKey.g4 by ANTLR 4.5
package com.linkedin.restli.client.config;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ResourceConfigKeyParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, Name=23;
	public static final int
		RULE_key = 0, RULE_property = 1, RULE_path = 2, RULE_inbound = 3, RULE_outbound = 4, 
		RULE_operation = 5, RULE_simpleOp = 6, RULE_complex = 7, RULE_complexOp = 8;
	public static final String[] ruleNames = {
		"key", "property", "path", "inbound", "outbound", "operation", "simpleOp", 
		"complex", "complexOp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'/'", "'timeoutNs'", "'batchingEnabled'", "'maxBatchSize'", "'batchingDryRun'", 
		"'*'", "'.'", "'GET'", "'BATCH_GET'", "'CREATE'", "'BATCH_CREATE'", "'PARTIAL_UPDATE'", 
		"'UPDATE'", "'BATCH_UPDATE'", "'DELETE'", "'BATCH_PARTIAL_UPDATE'", "'BATCH_DELETE'", 
		"'GET_ALL'", "'OPTIONS'", "'-'", "'FINDER'", "'ACTION'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, "Name"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ResourceConfigKey.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ResourceConfigKeyParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class KeyContext extends ParserRuleContext {
		public PathContext path() {
			return getRuleContext(PathContext.class,0);
		}
		public PropertyContext property() {
			return getRuleContext(PropertyContext.class,0);
		}
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitKey(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_key);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			path();
			setState(19);
			match(T__0);
			setState(20);
			property();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PropertyContext extends ParserRuleContext {
		public PropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_property; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitProperty(this);
		}
	}

	public final PropertyContext property() throws RecognitionException {
		PropertyContext _localctx = new PropertyContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_property);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PathContext extends ParserRuleContext {
		public InboundContext inbound() {
			return getRuleContext(InboundContext.class,0);
		}
		public OutboundContext outbound() {
			return getRuleContext(OutboundContext.class,0);
		}
		public PathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_path; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitPath(this);
		}
	}

	public final PathContext path() throws RecognitionException {
		PathContext _localctx = new PathContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_path);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(24);
				inbound();
				}
				break;
			case 2:
				{
				setState(25);
				match(T__5);
				}
				break;
			}
			setState(28);
			match(T__0);
			setState(31);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(29);
				outbound();
				}
				break;
			case 2:
				{
				setState(30);
				match(T__5);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InboundContext extends ParserRuleContext {
		public TerminalNode Name() { return getToken(ResourceConfigKeyParser.Name, 0); }
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public InboundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inbound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterInbound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitInbound(this);
		}
	}

	public final InboundContext inbound() throws RecognitionException {
		InboundContext _localctx = new InboundContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_inbound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			_la = _input.LA(1);
			if ( !(_la==T__5 || _la==Name) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(34);
			match(T__6);
			setState(37);
			switch (_input.LA(1)) {
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__20:
			case T__21:
				{
				setState(35);
				operation();
				}
				break;
			case T__5:
				{
				setState(36);
				match(T__5);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutboundContext extends ParserRuleContext {
		public TerminalNode Name() { return getToken(ResourceConfigKeyParser.Name, 0); }
		public OperationContext operation() {
			return getRuleContext(OperationContext.class,0);
		}
		public OutboundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outbound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterOutbound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitOutbound(this);
		}
	}

	public final OutboundContext outbound() throws RecognitionException {
		OutboundContext _localctx = new OutboundContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_outbound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			_la = _input.LA(1);
			if ( !(_la==T__5 || _la==Name) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(40);
			match(T__6);
			setState(43);
			switch (_input.LA(1)) {
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__20:
			case T__21:
				{
				setState(41);
				operation();
				}
				break;
			case T__5:
				{
				setState(42);
				match(T__5);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OperationContext extends ParserRuleContext {
		public SimpleOpContext simpleOp() {
			return getRuleContext(SimpleOpContext.class,0);
		}
		public ComplexContext complex() {
			return getRuleContext(ComplexContext.class,0);
		}
		public OperationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitOperation(this);
		}
	}

	public final OperationContext operation() throws RecognitionException {
		OperationContext _localctx = new OperationContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_operation);
		try {
			setState(47);
			switch (_input.LA(1)) {
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
			case T__17:
			case T__18:
				enterOuterAlt(_localctx, 1);
				{
				setState(45);
				simpleOp();
				}
				break;
			case T__20:
			case T__21:
				enterOuterAlt(_localctx, 2);
				{
				setState(46);
				complex();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleOpContext extends ParserRuleContext {
		public SimpleOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterSimpleOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitSimpleOp(this);
		}
	}

	public final SimpleOpContext simpleOp() throws RecognitionException {
		SimpleOpContext _localctx = new SimpleOpContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_simpleOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComplexContext extends ParserRuleContext {
		public ComplexOpContext complexOp() {
			return getRuleContext(ComplexOpContext.class,0);
		}
		public TerminalNode Name() { return getToken(ResourceConfigKeyParser.Name, 0); }
		public ComplexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterComplex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitComplex(this);
		}
	}

	public final ComplexContext complex() throws RecognitionException {
		ComplexContext _localctx = new ComplexContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_complex);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			complexOp();
			setState(52);
			match(T__19);
			setState(53);
			_la = _input.LA(1);
			if ( !(_la==T__5 || _la==Name) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ComplexOpContext extends ParserRuleContext {
		public ComplexOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complexOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).enterComplexOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ResourceConfigKeyListener ) ((ResourceConfigKeyListener)listener).exitComplexOp(this);
		}
	}

	public final ComplexOpContext complexOp() throws RecognitionException {
		ComplexOpContext _localctx = new ComplexOpContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_complexOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			_la = _input.LA(1);
			if ( !(_la==T__20 || _la==T__21) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\31<\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\5\4\35\n\4\3\4\3\4\3\4\5\4\"\n\4\3\5\3\5\3\5\3\5"+
		"\5\5(\n\5\3\6\3\6\3\6\3\6\5\6.\n\6\3\7\3\7\5\7\62\n\7\3\b\3\b\3\t\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\2\2\13\2\4\6\b\n\f\16\20\22\2\6\3\2\4\7\4\2\b\b\31"+
		"\31\3\2\n\25\3\2\27\30\67\2\24\3\2\2\2\4\30\3\2\2\2\6\34\3\2\2\2\b#\3"+
		"\2\2\2\n)\3\2\2\2\f\61\3\2\2\2\16\63\3\2\2\2\20\65\3\2\2\2\229\3\2\2\2"+
		"\24\25\5\6\4\2\25\26\7\3\2\2\26\27\5\4\3\2\27\3\3\2\2\2\30\31\t\2\2\2"+
		"\31\5\3\2\2\2\32\35\5\b\5\2\33\35\7\b\2\2\34\32\3\2\2\2\34\33\3\2\2\2"+
		"\35\36\3\2\2\2\36!\7\3\2\2\37\"\5\n\6\2 \"\7\b\2\2!\37\3\2\2\2! \3\2\2"+
		"\2\"\7\3\2\2\2#$\t\3\2\2$\'\7\t\2\2%(\5\f\7\2&(\7\b\2\2\'%\3\2\2\2\'&"+
		"\3\2\2\2(\t\3\2\2\2)*\t\3\2\2*-\7\t\2\2+.\5\f\7\2,.\7\b\2\2-+\3\2\2\2"+
		"-,\3\2\2\2.\13\3\2\2\2/\62\5\16\b\2\60\62\5\20\t\2\61/\3\2\2\2\61\60\3"+
		"\2\2\2\62\r\3\2\2\2\63\64\t\4\2\2\64\17\3\2\2\2\65\66\5\22\n\2\66\67\7"+
		"\26\2\2\678\t\3\2\28\21\3\2\2\29:\t\5\2\2:\23\3\2\2\2\7\34!\'-\61";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
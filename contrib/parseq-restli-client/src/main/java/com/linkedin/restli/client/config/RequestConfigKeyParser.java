// Generated from com/linkedin/restli/client/config/RequestConfigKey.g4 by ANTLR 4.5
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
public class RequestConfigKeyParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, Name=24;
	public static final int
		RULE_key = 0, RULE_inbound = 1, RULE_outbound = 2, RULE_operationIn = 3, 
		RULE_operationOut = 4, RULE_simpleOp = 5, RULE_httpExtraOp = 6, RULE_complex = 7, 
		RULE_complexOp = 8;
	public static final String[] ruleNames = {
		"key", "inbound", "outbound", "operationIn", "operationOut", "simpleOp", 
		"httpExtraOp", "complex", "complexOp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'/'", "'*'", "'.'", "'GET'", "'BATCH_GET'", "'CREATE'", "'BATCH_CREATE'", 
		"'PARTIAL_UPDATE'", "'UPDATE'", "'BATCH_UPDATE'", "'DELETE'", "'BATCH_PARTIAL_UPDATE'", 
		"'BATCH_DELETE'", "'GET_ALL'", "'OPTIONS'", "'HEAD'", "'POST'", "'PUT'", 
		"'TRACE'", "'CONNECT'", "'-'", "'FINDER'", "'ACTION'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"Name"
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
	public String getGrammarFileName() { return "RequestConfigKey.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RequestConfigKeyParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class KeyContext extends ParserRuleContext {
		public InboundContext inbound() {
			return getRuleContext(InboundContext.class,0);
		}
		public OutboundContext outbound() {
			return getRuleContext(OutboundContext.class,0);
		}
		public TerminalNode EOF() { return getToken(RequestConfigKeyParser.EOF, 0); }
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitKey(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_key);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(18);
			inbound();
			setState(19);
			match(T__0);
			setState(20);
			outbound();
			setState(21);
			match(EOF);
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
		public TerminalNode Name() { return getToken(RequestConfigKeyParser.Name, 0); }
		public OperationInContext operationIn() {
			return getRuleContext(OperationInContext.class,0);
		}
		public InboundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inbound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterInbound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitInbound(this);
		}
	}

	public final InboundContext inbound() throws RecognitionException {
		InboundContext _localctx = new InboundContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_inbound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(23);
			_la = _input.LA(1);
			if ( !(_la==T__1 || _la==Name) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(24);
			match(T__2);
			setState(27);
			switch (_input.LA(1)) {
			case T__3:
			case T__4:
			case T__5:
			case T__6:
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
			case T__19:
			case T__21:
			case T__22:
				{
				setState(25);
				operationIn();
				}
				break;
			case T__1:
				{
				setState(26);
				match(T__1);
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
		public TerminalNode Name() { return getToken(RequestConfigKeyParser.Name, 0); }
		public OperationOutContext operationOut() {
			return getRuleContext(OperationOutContext.class,0);
		}
		public OutboundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outbound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterOutbound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitOutbound(this);
		}
	}

	public final OutboundContext outbound() throws RecognitionException {
		OutboundContext _localctx = new OutboundContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_outbound);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			_la = _input.LA(1);
			if ( !(_la==T__1 || _la==Name) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			setState(30);
			match(T__2);
			setState(33);
			switch (_input.LA(1)) {
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
			case T__21:
			case T__22:
				{
				setState(31);
				operationOut();
				}
				break;
			case T__1:
				{
				setState(32);
				match(T__1);
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

	public static class OperationInContext extends ParserRuleContext {
		public SimpleOpContext simpleOp() {
			return getRuleContext(SimpleOpContext.class,0);
		}
		public ComplexContext complex() {
			return getRuleContext(ComplexContext.class,0);
		}
		public HttpExtraOpContext httpExtraOp() {
			return getRuleContext(HttpExtraOpContext.class,0);
		}
		public OperationInContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationIn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterOperationIn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitOperationIn(this);
		}
	}

	public final OperationInContext operationIn() throws RecognitionException {
		OperationInContext _localctx = new OperationInContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_operationIn);
		try {
			setState(38);
			switch (_input.LA(1)) {
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
				enterOuterAlt(_localctx, 1);
				{
				setState(35);
				simpleOp();
				}
				break;
			case T__21:
			case T__22:
				enterOuterAlt(_localctx, 2);
				{
				setState(36);
				complex();
				}
				break;
			case T__15:
			case T__16:
			case T__17:
			case T__18:
			case T__19:
				enterOuterAlt(_localctx, 3);
				{
				setState(37);
				httpExtraOp();
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

	public static class OperationOutContext extends ParserRuleContext {
		public SimpleOpContext simpleOp() {
			return getRuleContext(SimpleOpContext.class,0);
		}
		public ComplexContext complex() {
			return getRuleContext(ComplexContext.class,0);
		}
		public OperationOutContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operationOut; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterOperationOut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitOperationOut(this);
		}
	}

	public final OperationOutContext operationOut() throws RecognitionException {
		OperationOutContext _localctx = new OperationOutContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_operationOut);
		try {
			setState(42);
			switch (_input.LA(1)) {
			case T__3:
			case T__4:
			case T__5:
			case T__6:
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case T__11:
			case T__12:
			case T__13:
			case T__14:
				enterOuterAlt(_localctx, 1);
				{
				setState(40);
				simpleOp();
				}
				break;
			case T__21:
			case T__22:
				enterOuterAlt(_localctx, 2);
				{
				setState(41);
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
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterSimpleOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitSimpleOp(this);
		}
	}

	public final SimpleOpContext simpleOp() throws RecognitionException {
		SimpleOpContext _localctx = new SimpleOpContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_simpleOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14))) != 0)) ) {
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

	public static class HttpExtraOpContext extends ParserRuleContext {
		public HttpExtraOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_httpExtraOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterHttpExtraOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitHttpExtraOp(this);
		}
	}

	public final HttpExtraOpContext httpExtraOp() throws RecognitionException {
		HttpExtraOpContext _localctx = new HttpExtraOpContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_httpExtraOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(46);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) ) {
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
		public TerminalNode Name() { return getToken(RequestConfigKeyParser.Name, 0); }
		public ComplexContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_complex; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterComplex(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitComplex(this);
		}
	}

	public final ComplexContext complex() throws RecognitionException {
		ComplexContext _localctx = new ComplexContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_complex);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			complexOp();
			setState(49);
			match(T__20);
			setState(50);
			_la = _input.LA(1);
			if ( !(_la==T__1 || _la==Name) ) {
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
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterComplexOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitComplexOp(this);
		}
	}

	public final ComplexOpContext complexOp() throws RecognitionException {
		ComplexOpContext _localctx = new ComplexOpContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_complexOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			_la = _input.LA(1);
			if ( !(_la==T__21 || _la==T__22) ) {
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\329\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\2\3\2\3\3\3\3\3\3\3\3\5\3\36\n\3\3\4\3\4\3\4\3\4\5\4$\n\4\3\5\3\5\3"+
		"\5\5\5)\n\5\3\6\3\6\5\6-\n\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3"+
		"\n\2\2\13\2\4\6\b\n\f\16\20\22\2\6\4\2\4\4\32\32\3\2\6\21\3\2\22\26\3"+
		"\2\30\31\64\2\24\3\2\2\2\4\31\3\2\2\2\6\37\3\2\2\2\b(\3\2\2\2\n,\3\2\2"+
		"\2\f.\3\2\2\2\16\60\3\2\2\2\20\62\3\2\2\2\22\66\3\2\2\2\24\25\5\4\3\2"+
		"\25\26\7\3\2\2\26\27\5\6\4\2\27\30\7\2\2\3\30\3\3\2\2\2\31\32\t\2\2\2"+
		"\32\35\7\5\2\2\33\36\5\b\5\2\34\36\7\4\2\2\35\33\3\2\2\2\35\34\3\2\2\2"+
		"\36\5\3\2\2\2\37 \t\2\2\2 #\7\5\2\2!$\5\n\6\2\"$\7\4\2\2#!\3\2\2\2#\""+
		"\3\2\2\2$\7\3\2\2\2%)\5\f\7\2&)\5\20\t\2\')\5\16\b\2(%\3\2\2\2(&\3\2\2"+
		"\2(\'\3\2\2\2)\t\3\2\2\2*-\5\f\7\2+-\5\20\t\2,*\3\2\2\2,+\3\2\2\2-\13"+
		"\3\2\2\2./\t\3\2\2/\r\3\2\2\2\60\61\t\4\2\2\61\17\3\2\2\2\62\63\5\22\n"+
		"\2\63\64\7\27\2\2\64\65\t\2\2\2\65\21\3\2\2\2\66\67\t\5\2\2\67\23\3\2"+
		"\2\2\6\35#(,";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
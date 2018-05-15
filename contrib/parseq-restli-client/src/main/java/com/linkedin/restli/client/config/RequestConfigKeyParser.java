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
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		Name=25;
	public static final int
		RULE_key = 0, RULE_inbound = 1, RULE_outbound = 2, RULE_restResource = 3, 
		RULE_operationIn = 4, RULE_operationOut = 5, RULE_simpleOp = 6, RULE_httpExtraOp = 7, 
		RULE_complex = 8, RULE_complexOp = 9;
	public static final String[] ruleNames = {
		"key", "inbound", "outbound", "restResource", "operationIn", "operationOut", 
		"simpleOp", "httpExtraOp", "complex", "complexOp"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'/'", "'*'", "'.'", "'-'", "':'", "'GET'", "'BATCH_GET'", "'CREATE'", 
		"'BATCH_CREATE'", "'PARTIAL_UPDATE'", "'UPDATE'", "'BATCH_UPDATE'", "'DELETE'", 
		"'BATCH_PARTIAL_UPDATE'", "'BATCH_DELETE'", "'GET_ALL'", "'OPTIONS'", 
		"'HEAD'", "'POST'", "'PUT'", "'TRACE'", "'CONNECT'", "'FINDER'", "'ACTION'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, "Name"
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
			setState(20);
			inbound();
			setState(21);
			match(T__0);
			setState(22);
			outbound();
			setState(23);
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
		public RestResourceContext restResource() {
			return getRuleContext(RestResourceContext.class,0);
		}
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			switch (_input.LA(1)) {
			case Name:
				{
				setState(25);
				restResource();
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
			setState(29);
			match(T__2);
			setState(32);
			switch (_input.LA(1)) {
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
			case T__20:
			case T__21:
			case T__22:
			case T__23:
				{
				setState(30);
				operationIn();
				}
				break;
			case T__1:
				{
				setState(31);
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
		public RestResourceContext restResource() {
			return getRuleContext(RestResourceContext.class,0);
		}
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			switch (_input.LA(1)) {
			case Name:
				{
				setState(34);
				restResource();
				}
				break;
			case T__1:
				{
				setState(35);
				match(T__1);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(38);
			match(T__2);
			setState(41);
			switch (_input.LA(1)) {
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
			case T__22:
			case T__23:
				{
				setState(39);
				operationOut();
				}
				break;
			case T__1:
				{
				setState(40);
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

	public static class RestResourceContext extends ParserRuleContext {
		public List<TerminalNode> Name() { return getTokens(RequestConfigKeyParser.Name); }
		public TerminalNode Name(int i) {
			return getToken(RequestConfigKeyParser.Name, i);
		}
		public RestResourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_restResource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).enterRestResource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RequestConfigKeyListener ) ((RequestConfigKeyListener)listener).exitRestResource(this);
		}
	}

	public final RestResourceContext restResource() throws RecognitionException {
		RestResourceContext _localctx = new RestResourceContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_restResource);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			match(Name);
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(44);
				match(T__3);
				setState(45);
				match(Name);
				}
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(51);
				match(T__4);
				setState(52);
				match(Name);
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__3) {
					{
					{
					setState(53);
					match(T__3);
					setState(54);
					match(Name);
					}
					}
					setState(59);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				}
				setState(64);
				_errHandler.sync(this);
				_la = _input.LA(1);
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
		enterRule(_localctx, 8, RULE_operationIn);
		try {
			setState(68);
			switch (_input.LA(1)) {
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
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				simpleOp();
				}
				break;
			case T__22:
			case T__23:
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				complex();
				}
				break;
			case T__17:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
				enterOuterAlt(_localctx, 3);
				{
				setState(67);
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
		enterRule(_localctx, 10, RULE_operationOut);
		try {
			setState(72);
			switch (_input.LA(1)) {
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
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				simpleOp();
				}
				break;
			case T__22:
			case T__23:
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
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
		enterRule(_localctx, 12, RULE_simpleOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16))) != 0)) ) {
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
		enterRule(_localctx, 14, RULE_httpExtraOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21))) != 0)) ) {
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
		enterRule(_localctx, 16, RULE_complex);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			complexOp();
			setState(79);
			match(T__3);
			setState(80);
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
		enterRule(_localctx, 18, RULE_complexOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			_la = _input.LA(1);
			if ( !(_la==T__22 || _la==T__23) ) {
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\33W\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\3"+
		"\2\3\2\3\2\3\2\3\2\3\3\3\3\5\3\36\n\3\3\3\3\3\3\3\5\3#\n\3\3\4\3\4\5\4"+
		"\'\n\4\3\4\3\4\3\4\5\4,\n\4\3\5\3\5\3\5\7\5\61\n\5\f\5\16\5\64\13\5\3"+
		"\5\3\5\3\5\3\5\7\5:\n\5\f\5\16\5=\13\5\7\5?\n\5\f\5\16\5B\13\5\3\6\3\6"+
		"\3\6\5\6G\n\6\3\7\3\7\5\7K\n\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3"+
		"\13\3\13\2\2\f\2\4\6\b\n\f\16\20\22\24\2\6\3\2\b\23\3\2\24\30\4\2\4\4"+
		"\33\33\3\2\31\32V\2\26\3\2\2\2\4\35\3\2\2\2\6&\3\2\2\2\b-\3\2\2\2\nF\3"+
		"\2\2\2\fJ\3\2\2\2\16L\3\2\2\2\20N\3\2\2\2\22P\3\2\2\2\24T\3\2\2\2\26\27"+
		"\5\4\3\2\27\30\7\3\2\2\30\31\5\6\4\2\31\32\7\2\2\3\32\3\3\2\2\2\33\36"+
		"\5\b\5\2\34\36\7\4\2\2\35\33\3\2\2\2\35\34\3\2\2\2\36\37\3\2\2\2\37\""+
		"\7\5\2\2 #\5\n\6\2!#\7\4\2\2\" \3\2\2\2\"!\3\2\2\2#\5\3\2\2\2$\'\5\b\5"+
		"\2%\'\7\4\2\2&$\3\2\2\2&%\3\2\2\2\'(\3\2\2\2(+\7\5\2\2),\5\f\7\2*,\7\4"+
		"\2\2+)\3\2\2\2+*\3\2\2\2,\7\3\2\2\2-\62\7\33\2\2./\7\6\2\2/\61\7\33\2"+
		"\2\60.\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63@\3\2\2\2\64"+
		"\62\3\2\2\2\65\66\7\7\2\2\66;\7\33\2\2\678\7\6\2\28:\7\33\2\29\67\3\2"+
		"\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<?\3\2\2\2=;\3\2\2\2>\65\3\2\2\2?B\3"+
		"\2\2\2@>\3\2\2\2@A\3\2\2\2A\t\3\2\2\2B@\3\2\2\2CG\5\16\b\2DG\5\22\n\2"+
		"EG\5\20\t\2FC\3\2\2\2FD\3\2\2\2FE\3\2\2\2G\13\3\2\2\2HK\5\16\b\2IK\5\22"+
		"\n\2JH\3\2\2\2JI\3\2\2\2K\r\3\2\2\2LM\t\2\2\2M\17\3\2\2\2NO\t\3\2\2O\21"+
		"\3\2\2\2PQ\5\24\13\2QR\7\6\2\2RS\t\4\2\2S\23\3\2\2\2TU\t\5\2\2U\25\3\2"+
		"\2\2\13\35\"&+\62;@FJ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
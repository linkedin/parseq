// Generated from com/linkedin/restli/client/config/ResourceConfigKey.g4 by ANTLR 4.5
package com.linkedin.restli.client.config;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ResourceConfigKeyLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, Name=23;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "T__21", "Name"
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


	public ResourceConfigKeyLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ResourceConfigKey.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\31\u0100\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2"+
		"\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30"+
		"\6\30\u00fd\n\30\r\30\16\30\u00fe\2\2\31\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\3\2\3\5\2\62;C\\c|\u0100\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\3\61\3\2\2\2\5\63\3\2\2\2\7=\3\2\2"+
		"\2\tM\3\2\2\2\13Z\3\2\2\2\ri\3\2\2\2\17k\3\2\2\2\21m\3\2\2\2\23q\3\2\2"+
		"\2\25{\3\2\2\2\27\u0082\3\2\2\2\31\u008f\3\2\2\2\33\u009e\3\2\2\2\35\u00a5"+
		"\3\2\2\2\37\u00b2\3\2\2\2!\u00b9\3\2\2\2#\u00ce\3\2\2\2%\u00db\3\2\2\2"+
		"\'\u00e3\3\2\2\2)\u00eb\3\2\2\2+\u00ed\3\2\2\2-\u00f4\3\2\2\2/\u00fc\3"+
		"\2\2\2\61\62\7\61\2\2\62\4\3\2\2\2\63\64\7v\2\2\64\65\7k\2\2\65\66\7o"+
		"\2\2\66\67\7g\2\2\678\7q\2\289\7w\2\29:\7v\2\2:;\7P\2\2;<\7u\2\2<\6\3"+
		"\2\2\2=>\7d\2\2>?\7c\2\2?@\7v\2\2@A\7e\2\2AB\7j\2\2BC\7k\2\2CD\7p\2\2"+
		"DE\7i\2\2EF\7G\2\2FG\7p\2\2GH\7c\2\2HI\7d\2\2IJ\7n\2\2JK\7g\2\2KL\7f\2"+
		"\2L\b\3\2\2\2MN\7o\2\2NO\7c\2\2OP\7z\2\2PQ\7D\2\2QR\7c\2\2RS\7v\2\2ST"+
		"\7e\2\2TU\7j\2\2UV\7U\2\2VW\7k\2\2WX\7|\2\2XY\7g\2\2Y\n\3\2\2\2Z[\7d\2"+
		"\2[\\\7c\2\2\\]\7v\2\2]^\7e\2\2^_\7j\2\2_`\7k\2\2`a\7p\2\2ab\7i\2\2bc"+
		"\7F\2\2cd\7t\2\2de\7{\2\2ef\7T\2\2fg\7w\2\2gh\7p\2\2h\f\3\2\2\2ij\7,\2"+
		"\2j\16\3\2\2\2kl\7\60\2\2l\20\3\2\2\2mn\7I\2\2no\7G\2\2op\7V\2\2p\22\3"+
		"\2\2\2qr\7D\2\2rs\7C\2\2st\7V\2\2tu\7E\2\2uv\7J\2\2vw\7a\2\2wx\7I\2\2"+
		"xy\7G\2\2yz\7V\2\2z\24\3\2\2\2{|\7E\2\2|}\7T\2\2}~\7G\2\2~\177\7C\2\2"+
		"\177\u0080\7V\2\2\u0080\u0081\7G\2\2\u0081\26\3\2\2\2\u0082\u0083\7D\2"+
		"\2\u0083\u0084\7C\2\2\u0084\u0085\7V\2\2\u0085\u0086\7E\2\2\u0086\u0087"+
		"\7J\2\2\u0087\u0088\7a\2\2\u0088\u0089\7E\2\2\u0089\u008a\7T\2\2\u008a"+
		"\u008b\7G\2\2\u008b\u008c\7C\2\2\u008c\u008d\7V\2\2\u008d\u008e\7G\2\2"+
		"\u008e\30\3\2\2\2\u008f\u0090\7R\2\2\u0090\u0091\7C\2\2\u0091\u0092\7"+
		"T\2\2\u0092\u0093\7V\2\2\u0093\u0094\7K\2\2\u0094\u0095\7C\2\2\u0095\u0096"+
		"\7N\2\2\u0096\u0097\7a\2\2\u0097\u0098\7W\2\2\u0098\u0099\7R\2\2\u0099"+
		"\u009a\7F\2\2\u009a\u009b\7C\2\2\u009b\u009c\7V\2\2\u009c\u009d\7G\2\2"+
		"\u009d\32\3\2\2\2\u009e\u009f\7W\2\2\u009f\u00a0\7R\2\2\u00a0\u00a1\7"+
		"F\2\2\u00a1\u00a2\7C\2\2\u00a2\u00a3\7V\2\2\u00a3\u00a4\7G\2\2\u00a4\34"+
		"\3\2\2\2\u00a5\u00a6\7D\2\2\u00a6\u00a7\7C\2\2\u00a7\u00a8\7V\2\2\u00a8"+
		"\u00a9\7E\2\2\u00a9\u00aa\7J\2\2\u00aa\u00ab\7a\2\2\u00ab\u00ac\7W\2\2"+
		"\u00ac\u00ad\7R\2\2\u00ad\u00ae\7F\2\2\u00ae\u00af\7C\2\2\u00af\u00b0"+
		"\7V\2\2\u00b0\u00b1\7G\2\2\u00b1\36\3\2\2\2\u00b2\u00b3\7F\2\2\u00b3\u00b4"+
		"\7G\2\2\u00b4\u00b5\7N\2\2\u00b5\u00b6\7G\2\2\u00b6\u00b7\7V\2\2\u00b7"+
		"\u00b8\7G\2\2\u00b8 \3\2\2\2\u00b9\u00ba\7D\2\2\u00ba\u00bb\7C\2\2\u00bb"+
		"\u00bc\7V\2\2\u00bc\u00bd\7E\2\2\u00bd\u00be\7J\2\2\u00be\u00bf\7a\2\2"+
		"\u00bf\u00c0\7R\2\2\u00c0\u00c1\7C\2\2\u00c1\u00c2\7T\2\2\u00c2\u00c3"+
		"\7V\2\2\u00c3\u00c4\7K\2\2\u00c4\u00c5\7C\2\2\u00c5\u00c6\7N\2\2\u00c6"+
		"\u00c7\7a\2\2\u00c7\u00c8\7W\2\2\u00c8\u00c9\7R\2\2\u00c9\u00ca\7F\2\2"+
		"\u00ca\u00cb\7C\2\2\u00cb\u00cc\7V\2\2\u00cc\u00cd\7G\2\2\u00cd\"\3\2"+
		"\2\2\u00ce\u00cf\7D\2\2\u00cf\u00d0\7C\2\2\u00d0\u00d1\7V\2\2\u00d1\u00d2"+
		"\7E\2\2\u00d2\u00d3\7J\2\2\u00d3\u00d4\7a\2\2\u00d4\u00d5\7F\2\2\u00d5"+
		"\u00d6\7G\2\2\u00d6\u00d7\7N\2\2\u00d7\u00d8\7G\2\2\u00d8\u00d9\7V\2\2"+
		"\u00d9\u00da\7G\2\2\u00da$\3\2\2\2\u00db\u00dc\7I\2\2\u00dc\u00dd\7G\2"+
		"\2\u00dd\u00de\7V\2\2\u00de\u00df\7a\2\2\u00df\u00e0\7C\2\2\u00e0\u00e1"+
		"\7N\2\2\u00e1\u00e2\7N\2\2\u00e2&\3\2\2\2\u00e3\u00e4\7Q\2\2\u00e4\u00e5"+
		"\7R\2\2\u00e5\u00e6\7V\2\2\u00e6\u00e7\7K\2\2\u00e7\u00e8\7Q\2\2\u00e8"+
		"\u00e9\7P\2\2\u00e9\u00ea\7U\2\2\u00ea(\3\2\2\2\u00eb\u00ec\7/\2\2\u00ec"+
		"*\3\2\2\2\u00ed\u00ee\7H\2\2\u00ee\u00ef\7K\2\2\u00ef\u00f0\7P\2\2\u00f0"+
		"\u00f1\7F\2\2\u00f1\u00f2\7G\2\2\u00f2\u00f3\7T\2\2\u00f3,\3\2\2\2\u00f4"+
		"\u00f5\7C\2\2\u00f5\u00f6\7E\2\2\u00f6\u00f7\7V\2\2\u00f7\u00f8\7K\2\2"+
		"\u00f8\u00f9\7Q\2\2\u00f9\u00fa\7P\2\2\u00fa.\3\2\2\2\u00fb\u00fd\t\2"+
		"\2\2\u00fc\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff\60\3\2\2\2\4\2\u00fe\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
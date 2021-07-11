/**
 * This software is Copyright (C) 2020 Tod G. Harter. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.giantelectronicbrain.catfood.hairball;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

/**
 * Define the core 'native' word set.
 * 
 * @author tharter
 *
 */
public class HairballVocabulary {
	
	public static IVocabulary create() {
		IVocabulary hbVocab = new Vocabulary("HAIRBALL");
		for(Definition def : defList) {
			hbVocab.add(def);
		}
		return hbVocab;
	}
	
	private static final List<Definition> defList = new ArrayList<>();
	static {
		/**
		 * Default compile time behavior for words, take the runtime
		 * behavior token and insert it into the current definition's
		 * token list. Which list that will be is determined by the
		 * mode, DOER or DOES.
		 * 
		 * Note that there is no actual Hairball definition for this, it
		 * is effectively how compiling mode does its job and could be thought
		 * of as the compile time behavior of the parser.
		 */
		Token compile = new NativeToken("compile",(interpreter) -> {
				Definition ourDef = (Definition) interpreter.pop();
				Token rtoken = ourDef.getRunTime();
				interpreter.getParserContext().getDictionary().addToken(rtoken);
			});

		/**
		 * Compile the token on the top of the stack. this simply adds the token to the
		 * current dictionary entry.
		 */
		Token compileToken = new NativeToken("compileToken",(interpreter) -> {
			interpreter.getParserContext().getDictionary().addToken((Token)interpreter.pop());
		});
		defList.add(new Definition(new Word("/HERE!"),compile,compileToken));
		
		/**
		 * Store a token at an arbitrary location in the current definition. This might be useful
		 * to do something like go back and replace a placeholder with a literal to form a branch
		 * target, for example. Or to provide a very simple type of data structure.
		 */
		Token store = new NativeToken("store",(interpreter) -> {
			int pc = (Integer) interpreter.pop();
			Token token = (Token) interpreter.pop();
			interpreter.getParserContext().getDictionary().putToken(token,pc);
		});
		defList.add(new Definition(new Word("/!"),compile,store));
		
		/**
		 * Call the inner interpreter to execute the token on TOS.
		 */
		Token execute = new NativeToken("execute",(interpreter) -> {
			Token token = (Token) interpreter.pop();
			interpreter.execute(token);
		});
		defList.add(new Definition(new Word("/EXECUTE"),compile,execute));
		
		/**
		 * Emit TOS to the output.
		 */
		Token emit = new NativeToken("emit",(interpreter) -> {
				try {
					interpreter.getParserContext().getOutput().emit(interpreter.pop().toString());
				} catch (IOException e) {
					throw new HairballException("Failed to write output",e);
				}
			});
		defList.add(new Definition(new Word("/."),compile,emit));
		
		/**
		 * Get a word from the input stream and push it onto TOS.
		 */
		Token word = new NativeToken("word",(interpreter) -> {
				try {
					Word nword = interpreter.getParserContext().getWordStream().getNextWord();
					interpreter.push(nword);
				} catch (IOException e) {
					throw new HairballException("Word could not read a token from input",e);
				}
			});
		defList.add(new Definition(new Word("/W"),compile,word));

		
		Token define = new NativeToken("define",(interpreter) -> {
				interpreter.getParserContext().getDictionary().define();
			});
		
		/**
		 * Put the parser in compiling mode.
		 */
		Token compiling = new NativeToken("compiling",(interpreter) -> { 
				try {
					interpreter.getParserContext().getParser().compile();
				} catch (IOException e) {
					throw new HairballException("Error during compilation",e);
				}
			});
		defList.add(new Definition(new Word("/COMPILING"),compile,compiling));
		
		/**
		 * Put the parser in interpreting mode.
		 */
		Token interpreting = new NativeToken("interpreting",(interpreter) -> {
				try {
					interpreter.getParserContext().getParser().interpret();
				} catch (IOException e) {
					throw new HairballException("Error during interpretation",e);
				}
			});
		defList.add(new Definition(new Word("/INTERPRETING"),compile,interpreting));
		
		/**
		 * Put the dictionary in DOES mode where added tokens go to the runtime behavior of the
		 * new definition.
		 */
		Token does = new NativeToken("does",(interpreter) -> {
				interpreter.getParserContext().getDictionary().does();
			});
		defList.add(new Definition(new Word("/DOES"),does,does));
		
		/**
		 * Put the dictionary in DOER mode where added tokens go to the compile time behavior of the
		 * new definition.
		 */
		Token doer = new NativeToken("doer",(interpreter) -> {
				interpreter.getParserContext().getDictionary().doer();
			});
		defList.add(new Definition(new Word("/DOER"),doer,doer));
		
		/**
		 * Start a new definition with the given name, and put the parser into
		 * compiling mode. We also call /DOER, since defining a runtime behavior
		 * is the default thing to do.
		 */
		Token create = new NativeToken("create",(interpreter) -> {
				interpreter.getParserContext().getDictionary()
					.create((Word)interpreter.pop());
			});
		Token colon = InterpreterToken.makeToken("colon",word,create,compiling,does);
		defList.add(new Definition(new Word("/:"), compile, colon));
		
		/**
		 * Quote some text and push it onto the stack as a literal.
		 */
/*		Token quoteCT = new NativeToken("quote_CT",(interpreter) -> {
			try {
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				LiteralToken lt = new LiteralToken(quoted,quoted);
				interpreter.getParserContext().getDictionary().addToken(lt);
			} catch (IOException e) {
				throw new HairballException("Word could not read a token from input",e);
			}
		}); */
		Token quoteRT = new NativeToken("quote",(interpreter) -> {
			try {
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				interpreter.push(quoted);
//				emit.execute(interpreter);
			} catch (IOException e) {
				throw new HairballException("Word could not read a token from input",e);
			}
		});
		defList.add(new Definition(new Word("/\""),compile,quoteRT));
		
		/**
		 * Throw an exception, taking a string from TOS and using it as the message.
		 */
		Token abort = new NativeToken("abort",(interpreter) -> {
				throw new HairballException((String)interpreter.pop());
			});
		defList.add(new Definition(new Word("/ABORT"),compile,abort));
		
		/**
		 * Drop the TOS.
		 */
		Token drop = new NativeToken("drop",(interpreter) -> {
				interpreter.pop();
			});
		defList.add(new Definition(new Word("/DROP"),compile,drop));
		
		/**
		 * End a definition, immediate word which puts us back into interpreting mode and
		 * finalizes the current definition.
		 */
		Token colonSlashRTErrorMsg = new LiteralToken("colonSlashRTErrorMsg",":/ must be matched with /: or another defining word");
		Token colonSlashCT = InterpreterToken.makeToken("colonSlash_CT",define,interpreting,drop);
		Token colonSlashRuntime = InterpreterToken.makeToken("colonSlash",colonSlashRTErrorMsg,abort);
		defList.add(new Definition(new Word(":/"), colonSlashCT, colonSlashRuntime));

		/**
		 * Output some quoted text. This will IMMEDIATELY output the text, even in compile mode, you don't
		 * need an interpreted mode version of that behavior since it is the default behavior of Hairball. 
		 */
		Token dotQuoteCT = new NativeToken("dotQuote_CT",(interpreter) -> {
			try {
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				LiteralToken lt = new LiteralToken("dotQuote_Literal",quoted);
				interpreter.getParserContext().getDictionary().addToken(lt);
				interpreter.getParserContext().getDictionary().addToken(emit);
			} catch (IOException e) {
				throw new HairballException("Word could not read a token from input",e);
			}
		});
		Token dotQuoteRT = new NativeToken("dotQuote",(interpreter) -> {
			try {
				String quoted = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				interpreter.push(quoted);
				emit.execute(interpreter);
			} catch (IOException e) {
				throw new HairballException("Word could not read a token from input",e);
			}
		});
		defList.add(new Definition(new Word("/.\""),dotQuoteCT,dotQuoteRT));
		
		/**
		 * This just generates an error message complaining about unbalanced quotes, because it is
		 * not really ever called as a word, but just used by quoting operators as a delimiter. So
		 * if it is encountered outside that context, something is unbalanced.
		 */
		Token quoteSlashErrorMsg = new LiteralToken("quoteSlashErrorMsg","\"/ must match with a quoting operator");
		Token quoteSlashRT = InterpreterToken.makeToken("quoteSlash",quoteSlashErrorMsg,abort);
		defList.add(new Definition(new Word("\"/"),quoteSlashRT,quoteSlashRT));
		
		/**
		 * A do-nothing word, which can be used as a placeholder, or for testing
		 */
		Token noop = new NativeToken("noop",(interpreter) -> { });
		defList.add(new Definition(new Word("//"), compile, noop));
		

		/**
		 * Emit TOS to the output.
		 */
		defList.add(new Definition(new Word("/."),compile,emit));
		
		/**
		 * Exit Java program. This is probably not something you want to call unless you
		 * ran Hairball interactively.
		 */
		Token exit = new NativeToken("EXIT",(interpreter) -> {
//NOTE: doesn't work with JS				System.exit((Integer)interpreter.pop());
			});
		defList.add(new Definition(new Word("/EXIT"),compile,exit));
		
		/**
		 * Produce a parameter stack dump.
		 */
		Token dotS = new NativeToken("dotS",(interpreter) -> {
				try {
					String depth = Integer.valueOf(interpreter.getParameterStack().size()).toString();
					Output output = interpreter.getParserContext().getOutput();
					output.emit(depth);
					output.space();
					Object[] stack = new Object[interpreter.getParameterStack().size()];
					interpreter.getParameterStack().copyInto(stack);
					for(Object obj : stack) {
						output.emit(obj.toString());
						output.emit("\n");
					}
				} catch (IOException e) {
					throw new HairballException(e);
				}
			});
		defList.add(new Definition(new Word("/.S"),compile,dotS));
		
		Token quit = new NativeToken("quit",(interpreter)-> {
//NOTE: doesn't work with JS				System.exit(0);
			System.exit(0);
			});
		defList.add(new Definition(new Word("/QUIT"),compile,quit));
		
		Token commentin = new NativeToken("commentin",(interpreter) -> {
				try {
					interpreter.getParserContext().getWordStream().getToMatching("*/");
				} catch (IOException e) {
					throw new HairballException("Word could not read a token from input",e);
				}
			});
		defList.add(new Definition(new Word("/*"),commentin,commentin));
		Token commentout = new LiteralToken("commentout","*/ must match with a quoting operator");
		defList.add(new Definition(new Word("*/"),commentout,commentout));
		
		Token spaceToken = new NativeToken("space",(interpreter) -> {
				try {
					interpreter.getParserContext().getOutput().space();
				} catch (IOException e) {
					throw new HairballException("Failed to output text",e);
				}
			});
		defList.add(new Definition(new Word("/SPACE"),compile,spaceToken));
		
		/*
		 * Source input from another file. This will grab the file name from 
		 * input, construct a new parser context and a new parser, and then
		 * launch the parser. Output should be interpolated seamlessly into
		 * the current output stream. 
		 */
		Token source = new NativeToken("source", (interpreter) -> {
			Vertx vertx = Vertx.vertx();
			FileSystem fileSystem = vertx.fileSystem();
			ParserContext cContext = interpreter.getParserContext();
			String fileName = null;
			try {
				fileName = interpreter.getParserContext().getWordStream().getToMatching("\"/");
				IWordStream wordStream = new BucketWordStream(fileSystem,fileName,".");
				Parser nParser = new Parser();
				ParserContext nContext = new ParserContext(wordStream, cContext.getDictionary(), 
						interpreter, cContext.getOutput(), nParser);
				nParser.setParserContext(nContext);
				nParser.parse();
			} catch (IOException e) {
				throw new HairballException("Failed to parse /SOURCE\" file"+fileName);
			}
		});
		defList.add(new Definition(new Word("/SOURCE\""),compile,source));
	}

}

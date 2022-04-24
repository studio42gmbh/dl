// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2020 Studio 42 GmbH ( https://www.s42m.de ).
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>

lexer grammar DLLexer ;

MULTILINE_COMMENT :		'/*' .*? '*/' -> channel(HIDDEN) ;
SINGLELINE_COMMENT :	'//' ~[\r\n]* -> channel(HIDDEN) ;

KEYWORD_ANNOTATION :	'annotation' ;
KEYWORD_TYPE :			'type' ;
KEYWORD_EXTENDS :		'extends' ;
KEYWORD_CONTAINS :		'contains' ;
KEYWORD_EXTERN :		'extern' ;
KEYWORD_REQUIRE :		'require' ;
KEYWORD_ENUM :			'enum' ;
KEYWORD_ABSTRACT :		'abstract' ;
KEYWORD_ALIAS :			'alias' ;
KEYWORD_FINAL :			'final' ;
KEYWORD_PRAGMA :		'pragma' ;

BOOLEAN_LITERAL :		'true' | 'false' ;

// string literal which strips the leading and trailing quotes 
// and also removes escaping \ already at lexer level
fragment ESCAPED_QUOTE :'\\"' ;
STRING_LITERAL :		'"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"' { setText(getText().substring(1, getText().length() - 1).replace("\\\"", "\"").replace("\\n", "\n")); } ;

FLOAT_LITERAL :			[-]? [0-9]+ '.' [0-9]+ ('E' [-+]? [0-9]+)? ;

// https://github.com/studio42gmbh/dl/issues/26 DLHrfParsing Allow hexadecimal numbers ad basic format in HRF DL 0x00...
INTEGER_LITERAL :		[-]? [0-9] [xXbB]? [0-9]* ;

// rather restrictive - but symbols should be well readable anyways not some special sign party
REF :					'$' [a-zA-Z_#] [a-zA-Z0-9\-_.#$]* { setText(getText().substring(1)); } ;	

// rather restrictive - but symbols should be well readable anyways not some special sign party
SYMBOL :				[a-zA-Z_#] [a-zA-Z0-9\-_.#$]* ;	

AT :					'@' ;
COLON :					':' ;
SEMI_COLON :			';' ;
SCOPE_OPEN :			'{' ;
SCOPE_CLOSE :			'}' ;
PARENTHESES_OPEN :		'(' ;
PARENTHESES_CLOSE :		')' ;
GENERIC_OPEN :			'<' ;
GENERIC_CLOSE :			'>' ;
COMMA :					',' ;
EQUALS :				'==' ;
XOR :					'!=' ;
NOT :					'!' ;
AND :					'&' ;
OR :					'|' ;
PLUS :					'+' ;
MINUS :					'-' ;
MUL :					'*' ;
DIV :					'/' ;
POW :					'^' ;

WS :					[ \t\n\r]+ -> skip ;
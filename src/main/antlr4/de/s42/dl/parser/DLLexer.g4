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

/**
 * See https://www.antlr.org/api/Java/org/antlr/v4/runtime/package-summary.html
 */
lexer grammar DLLexer ;


// COMMENTS

MULTILINE_COMMENT :		'/*' .*? '*/' -> channel(HIDDEN) ;
SINGLELINE_COMMENT :	'//' ~[\r\n]* -> channel(HIDDEN) ;


// KEYWORDS

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
KEYWORD_DECLARE :		'declare' ;
KEYWORD_ASSERT :		'assert' ;
KEYWORD_DYNAMIC :		'dynamic' ;


// RESERVED KEYWORDS (currently quite some - but this shall ensure future extensions to be intuitive and get pruned as DL evolves)

fragment RESERVED_KEYWORD_NEW :		'new';
fragment RESERVED_KEYWORD_COPY :	'copy' ;
fragment RESERVED_KEYWORD_SCOPE :	'scope' ;
fragment RESERVED_KEYWORD_PACKAGE :	'package' ;
fragment RESERVED_KEYWORD_MODULE :	'module' ;
fragment RESERVED_KEYWORD_NAMESPACE :'namespace' ;
fragment RESERVED_KEYWORD_DEFINE :	'define' ;
fragment RESERVED_KEYWORD_UNDEF :	'undef' ;
fragment RESERVED_KEYWORD_IN :		'in' ;
fragment RESERVED_KEYWORD_CONTAINED :'contained' ;
fragment RESERVED_KEYWORD_AND :		'and' ;
fragment RESERVED_KEYWORD_OR :		'or' ;
fragment RESERVED_KEYWORD_NOT :		'not' ;
fragment RESERVED_KEYWORD_NAND :	'nand' ;
fragment RESERVED_KEYWORD_XOR :		'xor' ;
fragment RESERVED_KEYWORD_VOLATILE :'volatile' ;
fragment RESERVED_KEYWORD_ATOMIC :	'atomic' ;
fragment RESERVED_KEYWORD_UNION :	'union' ;
fragment RESERVED_KEYWORD_STRUCT :	'struct' ;
fragment RESERVED_KEYWORD_TEMPLATE :'template' ;
fragment RESERVED_KEYWORD_CLASS :	'class' ;
fragment RESERVED_KEYWORD_INTERFACE:'interface' ;
fragment RESERVED_KEYWORD_IMPLEMENTS:'implements' ;
fragment RESERVED_KEYWORD_CONTRACT:	'contract' ;
fragment RESERVED_KEYWORD_CONCEPT:	'concept' ;
fragment RESERVED_KEYWORD_IF :		'if' ;
fragment RESERVED_KEYWORD_PRIVATE :	'private' ;
fragment RESERVED_KEYWORD_THIS :	'this' ;
fragment RESERVED_KEYWORD_THROW :	'throw' ;
fragment RESERVED_KEYWORD_ELSE :	'else' ;
fragment RESERVED_KEYWORD_IMPORT :	'import' ;
fragment RESERVED_KEYWORD_PUBLIC :	'public' ;
fragment RESERVED_KEYWORD_PROTECTED:'protected' ;
fragment RESERVED_KEYWORD_THROWS :	'throws' ;
fragment RESERVED_KEYWORD_BREAK :	'break' ;
fragment RESERVED_KEYWORD_RETURN :	'return' ;
fragment RESERVED_KEYWORD_CASE :	'case' ;
fragment RESERVED_KEYWORD_STATIC :	'static' ;
fragment RESERVED_KEYWORD_TRY :		'try' ;
fragment RESERVED_KEYWORD_CATCH :	'catch' ;
fragment RESERVED_KEYWORD_VOID :	'void' ;
fragment RESERVED_KEYWORD_NULL :	'null' ;
fragment RESERVED_KEYWORD_LAMBDA :	'lambda' ;
fragment RESERVED_KEYWORD_DO :		'do' ;
fragment RESERVED_KEYWORD_WHILE :	'while' ;
fragment RESERVED_KEYWORD_FOR :		'for' ;
fragment RESERVED_KEYWORD_DEFAULT :	'default' ;
fragment RESERVED_KEYWORD_INSTANCEOF:'instanceof' ;
fragment RESERVED_KEYWORD_USE :		'use' ;
fragment RESERVED_KEYWORD_UNUSE :	'unuse' ;
fragment RESERVED_KEYWORD_CALL :	'call' ;
fragment RESERVED_KEYWORD_CLONE :	'clone' ;
fragment RESERVED_KEYWORD_SELECT :	'select' ;
fragment RESERVED_KEYWORD_WHEN :	'when' ;
fragment RESERVED_KEYWORD_SWITCH :	'switch' ;
fragment RESERVED_KEYWORD_DISTINCT :'distinct' ;

RESERVED_KEYWORD : ( 
	RESERVED_KEYWORD_NEW | 
	RESERVED_KEYWORD_COPY | 
	RESERVED_KEYWORD_SCOPE |
	RESERVED_KEYWORD_PACKAGE |
	RESERVED_KEYWORD_MODULE |
	RESERVED_KEYWORD_NAMESPACE |
	RESERVED_KEYWORD_DEFINE |
	RESERVED_KEYWORD_UNDEF |
	RESERVED_KEYWORD_IN |
	RESERVED_KEYWORD_CONTAINED |
	RESERVED_KEYWORD_AND |
	RESERVED_KEYWORD_OR |
	RESERVED_KEYWORD_NOT |
	RESERVED_KEYWORD_NAND |
	RESERVED_KEYWORD_XOR |
	RESERVED_KEYWORD_VOLATILE |
	RESERVED_KEYWORD_ATOMIC |
	RESERVED_KEYWORD_UNION |
	RESERVED_KEYWORD_STRUCT |
	RESERVED_KEYWORD_TEMPLATE |
	RESERVED_KEYWORD_CLASS |
	RESERVED_KEYWORD_INTERFACE |
	RESERVED_KEYWORD_IMPLEMENTS |
	RESERVED_KEYWORD_CONTRACT |
	RESERVED_KEYWORD_CONCEPT |
	RESERVED_KEYWORD_IF |
	RESERVED_KEYWORD_PRIVATE |
	RESERVED_KEYWORD_THIS |
	RESERVED_KEYWORD_THROW |
	RESERVED_KEYWORD_ELSE |
	RESERVED_KEYWORD_IMPORT |
	RESERVED_KEYWORD_PUBLIC |
	RESERVED_KEYWORD_PROTECTED |
	RESERVED_KEYWORD_THROWS |
	RESERVED_KEYWORD_BREAK |
	RESERVED_KEYWORD_RETURN |
	RESERVED_KEYWORD_CASE |
	RESERVED_KEYWORD_STATIC |
	RESERVED_KEYWORD_TRY |
	RESERVED_KEYWORD_CATCH |
	RESERVED_KEYWORD_VOID |
	RESERVED_KEYWORD_NULL |
	RESERVED_KEYWORD_LAMBDA |
	RESERVED_KEYWORD_DO |
	RESERVED_KEYWORD_WHILE |
	RESERVED_KEYWORD_FOR |
	RESERVED_KEYWORD_DEFAULT |
	RESERVED_KEYWORD_INSTANCEOF |
	RESERVED_KEYWORD_USE |
	RESERVED_KEYWORD_UNUSE |
	RESERVED_KEYWORD_CALL |
	RESERVED_KEYWORD_CLONE |
	RESERVED_KEYWORD_SELECT |
	RESERVED_KEYWORD_WHEN |
	RESERVED_KEYWORD_SWITCH |
	RESERVED_KEYWORD_DISTINCT
) ;


// LITERALS

BOOLEAN_LITERAL :		( T R U E ) | ( F A L S E ) ;

// string literal which strips the leading and trailing quotes 
// and also removes escaping \ already at lexer level
STRING_LITERAL :		'"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"' ;

FLOAT_LITERAL :			'-'? DIGIT+ '.' DIGIT+ ( E [-+]? DIGIT+)? ;

// DLHrfParsing Allow hexadecimal numbers ad basic format in HRF DL 0x00... (#26)
INTEGER_LITERAL :		'-'? ( 
    ( '0' X [0-9a-fA-F]+ ) |	// Hex
    ( '0' B [0-1]+ ) |			// Binary
    ( '0' [1-7]+ ) |			// Octal	
	'0' | ( [1-9] DIGIT* ) )	// Decimal
;

// Rather restrictive - but ref symbols should be well readable anyways not some special sign party
fragment REF_PART :		[a-zA-Z_#] [a-zA-Z0-9\-_#$]* ;
REF :					'$' '?'? REF_PART ( '.' '?'? REF_PART )* ;	

// rather restrictive - but symbols should be well readable anyways not some special sign party
SYMBOL :				[a-zA-Z_#] [a-zA-Z0-9\-_.#$]* ;	


// CHARS

AT :					'@' ;
COLON :					':' ;
SEMI_COLON :			';' ;
SCOPE_OPEN :			'{' ;
SCOPE_CLOSE :			'}' ;
PARENTHESES_OPEN :		'(' ;
PARENTHESES_CLOSE :		')' ;
LESSER_EQUALS :			'<=' ;
LESSER :				'<' ;
GREATER_EQUALS :		'>=' ;
GREATER :				'>' ;
COMMA :					',' ;
EQUALS :				'==' ;
XOR :					'!=' ;
LIKE :					'~=' ;
NOT :					'!' ;
AND :					'&' ;
OR :					'|' ;
PLUS :					'+' ;
MINUS :					'-' ;
MUL :					'*' ;
DIV :					'/' ;
POW :					'^' ;


// WHITESPACE -> Hide

WHITESPACES :			[ \t\r]+  -> channel(HIDDEN) ;

NEWLINE :				[\n] -> channel(HIDDEN) ;

// CAPTURE THE REST AS UNKNOWN TOKENs 
// this makes usind this lexer easier to use in IDEs etc. as 
// it can not end in a partial state like STRING which annoys the parsers

UNKNOWN :				. ;

fragment ESCAPED_QUOTE : '\\"' ;

fragment DIGIT : [0-9] ;

fragment A : [aA] ;
fragment B : [bB] ;
fragment C : [cC] ;
fragment D : [dD] ;
fragment E : [eE] ;
fragment F : [fF] ;
fragment G : [gG] ;
fragment H : [hH] ;
fragment I : [iI] ;
fragment J : [jJ] ;
fragment K : [kK] ;
fragment L : [lL] ;
fragment M : [mM] ;
fragment N : [nN] ;
fragment O : [oO] ;
fragment P : [pP] ;
fragment Q : [qQ] ;
fragment R : [rR] ;
fragment S : [sS] ;
fragment T : [tT] ;
fragment U : [uU] ;
fragment V : [vV] ;
fragment W : [wW] ;
fragment X : [xX] ;
fragment Y : [yY] ;
fragment Z : [zZ] ;

// END
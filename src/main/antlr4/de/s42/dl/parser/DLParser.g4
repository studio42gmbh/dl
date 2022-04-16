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

parser grammar DLParser ;

options { tokenVocab=DLLexer; }

data : declaration* EOF ;

declaration : 
	instanceDefinition |
	typeDefinition |
	enumDefinition |
	annotationDefinition |
	attributeAssignment |
	pragma |
	require |
	alias ;


/* generic */

identifier : SYMBOL ;
typeIdentifier : identifier ;
staticParameters : PARENTHESES_OPEN ( staticParameter ( COMMA staticParameter )* )? PARENTHESES_CLOSE ;
staticParameter : STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL | BOOLEAN_LITERAL | SYMBOL ;
symbolOrString : STRING_LITERAL | SYMBOL ;


/* pragma */

pragma : 
	KEYWORD_PRAGMA 
	pragmaName
	staticParameters? 
	SEMI_COLON ;

pragmaName : identifier ;


/* annotation */

annotation : 
	AT 
	annotationName
	staticParameters? ;

annotationName : identifier ;


/* alias */

alias : 
	KEYWORD_ALIAS 
	aliasRedefinition 
	aliasDefinition 
	SEMI_COLON ;

aliasRedefinition : identifier ;
aliasDefinition : identifier ;


/* require */

require : 
	KEYWORD_REQUIRE 
	requireModule 
	SEMI_COLON ;

requireModule : symbolOrString ;


/* annotationDefinition */

annotationDefinition : 
	KEYWORD_EXTERN? 
	KEYWORD_ANNOTATION 
	annotationDefinitionName
	annotation* 
	SEMI_COLON ;

annotationDefinitionName : identifier;


/* typeDefinition */

typeDefinition : 
	KEYWORD_EXTERN? 
	( KEYWORD_FINAL | KEYWORD_ABSTRACT )?
	KEYWORD_TYPE 
	typeDefinitionName 
	annotation* 
	( KEYWORD_EXTENDS parentTypeName ( COMMA parentTypeName )* )?
	( KEYWORD_CONTAINS containsTypeName ( COMMA containsTypeName )* )?
	( typeBody | SEMI_COLON ) ;

typeDefinitionName : identifier;
parentTypeName : identifier ;
containsTypeName : identifier ;
typeBody : SCOPE_OPEN ( typeAttributeDefinition | require )* SCOPE_CLOSE ;


/* enumDefinition */

enumDefinition : 
	KEYWORD_EXTERN? 
	KEYWORD_ENUM 
	enumName
	annotation* 
	( enumBody | SEMI_COLON ) ;

enumName : identifier;
enumBody : SCOPE_OPEN enumValueDefinition ( COMMA enumValueDefinition )* COMMA? SCOPE_CLOSE ;
enumValueDefinition : symbolOrString ;


/* typeAttributeDefinition */

typeAttributeDefinition : 
	typeAttributeDefinitionType 
	typeAttributeDefinitionName 
	annotation* 
	( COLON typeAttributeDefinitionDefault )? 
	// @todo DL allow multiple value assignment as default value
	// ( COLON typeAttributeDefinitionDefault ( COMMA typeAttributeDefinitionDefault )* COMMA? )? 
	SEMI_COLON ;

typeAttributeDefinitionType : typeIdentifier typeAttributeDefinitionGeneric? ;
typeAttributeDefinitionName : identifier ;
typeAttributeDefinitionGeneric : GENERIC_OPEN identifier ( COMMA identifier )* COMMA? GENERIC_CLOSE ;
typeAttributeDefinitionDefault : instanceDefinition | STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL | BOOLEAN_LITERAL | SYMBOL | REF ;


/* instanceDefinition */

instanceDefinition : 
	instanceType instanceName?
	annotation* 
	( instanceBody | SEMI_COLON ) ;

instanceType : typeIdentifier ;
instanceName : identifier ;
instanceBody : SCOPE_OPEN ( attributeAssignment | instanceDefinition | require )* SCOPE_CLOSE ;


/* attributeAssignment */

attributeAssignment : 
	( ( attributeType attributeName ) | attributeName )
	annotation* 
	COLON attributeAssignable ( COMMA attributeAssignable )* COMMA? 
	SEMI_COLON ;

attributeType : typeIdentifier ;
attributeName : identifier ;
attributeAssignable : instanceDefinition | STRING_LITERAL | FLOAT_LITERAL | INTEGER_LITERAL | BOOLEAN_LITERAL | SYMBOL | REF ;

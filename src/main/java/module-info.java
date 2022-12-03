// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 * 
 * Copyright 2022 Studio 42 GmbH ( https://www.s42m.de ).
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

module de.sft.dl
{
	requires org.antlr.antlr4.runtime;
	requires de.sft.log;
	requires de.sft.base;
	requires java.desktop;
	requires java.compiler;
	requires org.json;

	exports de.s42.dl;
	exports de.s42.dl.annotations;
	exports de.s42.dl.attributes;
	exports de.s42.dl.core;
	exports de.s42.dl.exceptions;
	exports de.s42.dl.instances;
	exports de.s42.dl.io;
	exports de.s42.dl.io.binary;
	exports de.s42.dl.io.hrf;
	exports de.s42.dl.io.json;
	exports de.s42.dl.java;
	exports de.s42.dl.parser;
	exports de.s42.dl.parser.expression;
	exports de.s42.dl.parser2;
	exports de.s42.dl.parameters;
	exports de.s42.dl.pragmas;
	exports de.s42.dl.types;
	exports de.s42.dl.util;

	opens de.s42.dl.types;
	opens de.s42.dl;
}

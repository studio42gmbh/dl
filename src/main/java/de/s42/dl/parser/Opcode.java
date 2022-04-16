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
package de.s42.dl.parser;

/**
 *
 * @author Benjamin Schiller
 */
public enum Opcode
{
	BeginModule(2),
	EndModule(3),
	DefineSymbol(4),
	BeginInstance(5),
	BeginAnonymousInstance(6),
	EndInstance(7),
	SetStringAttribute(8),
	SetIntAttribute(9),
	SetLongAttribute(10),
	SetFloatAttribute(11),
	SetDoubleAttribute(12),
	SetBooleanAttribute(13),
	SetBinaryAttribute(14), /*
	Version(1),
	DefineString(3),
	DefineExternType(5),
	DefineExternAnnotation(4),
	DefineAlias(4),
	BeginDefineType(3),
	EndDefineType(3),
	BeginDefineAttribute(6),
	EndDefineAttribute(6),
	BeginDefineEnum(4),
	EndDefineEnum(5),
	SetPragma(5),
	BeginInstance(6),
	EndInstance(7),
	BeginAnnotation(7),
	EndAnnotation(7),
	BeginAttribute(6),
	EndAttribute(6),
	DefineParameter(6),*/;

	public final int code;
	
	private final static Opcode[] opcodes = new Opcode[15];

	private Opcode(int code)
	{
		assert code > 0;

		this.code = code;
	}
	
	static {
		for (Opcode opcode : Opcode.values()) {
			opcodes[opcode.code] = opcode;
		}
	}
	
	public static Opcode valueOf(int code)
	{
		if (code < 0 || code >= opcodes.length) {
			throw new IllegalArgumentException("Opcode wih code " + code + " does not exist - out of range");
		}
		
		Opcode opcode = opcodes[code];
		
		if (opcode == null) {		
			throw new IllegalArgumentException("Opcode wih code " + code + " does not exist");
		}
		
		return opcode;
	}
}

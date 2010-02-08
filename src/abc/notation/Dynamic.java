// Copyright 2006-2008 Lionel Gueganton
// This file is part of abc4j.
//
// abc4j is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// abc4j is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with abc4j.  If not, see <http://www.gnu.org/licenses/>.
package abc.notation;

/**
 * Constants for dynamics (crescendo, decrescendo, mp, fff...)
 */
public class Dynamic implements SymbolElement {
	
	/** <I>pppp</I> dynamic, should be played at volume 15 in midi */
	public static final byte PPPP = 0;
	/** <I>ppp</I> dynamic, should be played at volume 30 in midi */
	public static final byte PPP = 1;
	/** <I>pp</I> (pianissimo) dynamic, should be played at volume 45 in midi */
	public static final byte PP = 2;
	/** <I>p</I> (piano) dynamic, should be played at volume 60 in midi */
	public static final byte P = 3;
	/** <I>mp</I> (mezzopiano) dynamic, should be played at volume 75 in midi */
	public static final byte MP = 4;
	/** <I>mf</I> (mezzoforte) dynamic, should be played at volume 90 in midi */
	public static final byte MF = 5;
	/** <I>f</I> (forte) dynamic, should be played at volume 105 in midi */
	public static final byte F = 6;
	/** <I>ff</I> (fortissimo) dynamic, should be played at volume 120 in midi */
	public static final byte FF = 7;
	/** <I>fff</I> dynamic, should be played at volume 127 in midi */
	public static final byte FFF = 8;
	/** <I>ffff</I> dynamic, should be played at volume 127 in midi */
	public static final byte FFFF = 9;
	/** <I>fp</I> (fortepiano) dynamic, one note at {@link #F}
	 * and the nexts at {@link #P} volume */
	public static final byte FP = 10;
	public static final byte SF = 11;
	public static final byte SFP = 12;
	public static final byte SFPP = 13;
	public static final byte SFZ = 14;
	public static final byte FZ = 15;
	public static final byte SFFZ = 16;
	
	public static byte convertToType(String str)
	{
		byte type = UNKNOWN;
		if (str.equals("pppp")) type = PPPP;
		else if (str.equals("ppp")) type = PPP;
		else if (str.equals("pp")) type = PP;
		else if (str.equals("p")) type = P;
		else if (str.equals("mp")) type = MP;
		else if (str.equals("mf")) type = MF;
		else if (str.equals("f")) type = F;
		else if (str.equals("ff")) type = FFF;
		else if (str.equals("fff")) type = FFF;
		else if (str.equals("ffff")) type = FFFF;
		else if (str.equals("fp")) type = FP;
		else if (str.equals("sf")) type = SF;
		else if (str.equals("sfp")) type = SFP;
		else if (str.equals("sfpp")) type = SFPP;
		else if (str.equals("sfz")) type = SFZ;
		else if (str.equals("fz")) type = FZ;
		else if (str.equals("sffz")) type = SFFZ;
		return type;
	}
	
	public static final byte UNKNOWN = -128;
	
	/** The type of this dynamic */
	private byte m_type = UNKNOWN;

	/** Creates a new dynamic with the corresponding type.
	 * @param type The type of dynamic to be created :
	 * {@link #PP}, {@link #MF}, {@link #FFF}...
	 **/
	public Dynamic(byte type) {
		m_type = type;
	}

	/** Returns the type of this dynamic. */
	public byte getType() {
		return m_type;
	}

	/** Checks if the dynamic's type is the same as the give type. */
	public boolean isType(byte type) {
		return (m_type == type);
	}

	public boolean equals(Object o) {
		if (o instanceof Dynamic) {
			return isType(((Dynamic) o).getType());
		} else
			return super.equals(o);
	}

}
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
package abc.ui.swing;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import abc.notation.MusicElement;
import abc.notation.TimeSignature;

/** This class is in charge of rendering a time signature. */
class JTimeSignature extends JScoreElementAbstract {
	
	protected TimeSignature m_ts = null;
	
	protected char[] m_numChars = null;
	protected char[] m_denomChars = null;
	protected int m_topNumY = -1;
	protected int m_bottomNumY = -1;

	public JTimeSignature(TimeSignature ts, Point2D base, ScoreMetrics c) {
		super(c);
		m_ts = ts;
		m_numChars = getMetrics().getMusicalFont().getTimeSignatureDigits(ts.getNumerator());
		m_denomChars = getMetrics().getMusicalFont().getTimeSignatureDigits(ts.getDenominator());
		setBase(base);
	}
	
	public double getWidth() {
		//compute width, no limit, we can have 16/128 if we want ;)
		return Math.max(
			getMetrics().getBounds(m_numChars).getWidth(),
			getMetrics().getBounds(m_denomChars).getWidth());
	}
	
	public MusicElement getMusicElement() {
		return m_ts;
	}
	
	protected void onBaseChanged() {
		//FIXME what if the signature numbers are not supported ? => arrayOutOfBounds ! :/
		m_topNumY = (int)(getBase().getY()-getMetrics().getNoteHeight()*3.0);
		m_bottomNumY = (int)(getBase().getY()-getMetrics().getNoteHeight()*0.9);
	}
	
	public double render(Graphics2D context){
		super.render(context);
		double width = getWidth();
		context.drawChars(m_numChars, 0, m_numChars.length,
				(int)(getBase().getX() + width/2
						- (getMetrics().getBounds(m_numChars).getWidth())/2),
				m_topNumY);
		context.drawChars(m_denomChars, 0, m_denomChars.length,
				(int)(getBase().getX() + width/2
						- (getMetrics().getBounds(m_denomChars).getWidth())/2),
				m_bottomNumY);
		return width;
	}
}


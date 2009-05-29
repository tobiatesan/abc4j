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
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Vector;

import abc.notation.MusicElement;

class JStaffLine extends JScoreElementAbstract {

	protected Vector m_staffElements = null;
	
	//protected Vector m_beginningSlurElements = null;
	
	public JStaffLine(Point2D base, ScoreMetrics c) {
		super (c);
		m_staffElements = new Vector();
		//m_beginningSlurElements = new Vector();
	}
	
	public Point2D getBase() {
		return ((JScoreElementAbstract)m_staffElements.elementAt(0)).getBase();
	}
	
	public MusicElement getMusicElement() {
		return null;
	}
	
	protected void onBaseChanged() {
		
	}
	
	public void addElement(JScoreElementAbstract r) {
		m_staffElements.addElement(r);
		r.setStaffLine(this);
		computeWidth();
	}
	
	/*public Vector getBeginningSlurElements() {
		return m_beginningSlurElements;
	}*/
	
	public JScoreElementAbstract[] toArray() {
		JScoreElementAbstract[] r = new JScoreElementAbstract[m_staffElements.size()];
		m_staffElements.toArray(r);
		return r;
	}
	
	public JScoreElement getScoreElementAt(Point point) {
		JScoreElement scoreEl = null;
		for (int i=0; i<m_staffElements.size(); i++) {
			scoreEl = ((JScoreElement)m_staffElements.elementAt(i)).getScoreElementAt(point);
			if (scoreEl!=null)
				return scoreEl;
		}
		return scoreEl;
	}
	
	public double render(Graphics2D g) {
		//super.render(g);
		/*Color previousColor = g.getColor();
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)(getBase().getX()), (int)(getBase().getY()-m_metrics.getStaffCharBounds().getHeight()), 
				(int)getWidth(), (int)(m_metrics.getStaffCharBounds().getHeight()));
		g.setColor(previousColor);*/
		JScoreElementAbstract[] elmts = toArray();
		for (int j=0; j<elmts.length; j++) {
			elmts[j].render((Graphics2D)g);
			/*if (elmts[j] instanceof SNote) {
				Note note = ((SNote)elmts[j]).getNote();
				if (note.isBeginingSlur())
					m_beginningSlurElements.addElement(note);
					
			}*/
		}
		int staffCharNb = (int)(getWidth()/m_metrics.getStaffCharBounds().getWidth());
		//System.out.println("char staff nb : " + staffCharNb);
		char[] staffS = new char[staffCharNb+2];
		for (int j=0; j<staffS.length; j++)
			staffS[j] = ScoreMetrics.STAFF_SIX_LINES;
		g.drawChars(staffS, 0, staffS.length, 0, (int)getBase().getY());
		return m_width;
	}
	
	public void scaleToWidth(double newWidth) {
		for (int i=0; i<m_staffElements.size(); i++){
			JScoreElementAbstract elmt = (JScoreElementAbstract)m_staffElements.elementAt(i);
			if ( (!(elmt instanceof JClef)) && (!(elmt instanceof JKeySignature))
					&& (!(elmt instanceof JTimeSignature))
					)  {
				double newXpos =(elmt.getBase().getX()*newWidth/getWidth());
				Point2D base = elmt.getBase();
				base.setLocation(newXpos, base.getY());
				elmt.setBase(base);
			}
		}
		computeWidth();
		//System.out.println("StaffLine, required Width : " + newWidth + " | result width=" + m_width);
	}
	
	private void computeWidth() {
		JScoreElementAbstract lastElmt = (JScoreElementAbstract)m_staffElements.lastElement();
		m_width = lastElmt.getBase().getX()+lastElmt.getWidth();
	}

}

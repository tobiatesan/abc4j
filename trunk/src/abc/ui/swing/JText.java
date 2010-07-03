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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Iterator;
import java.util.Map;

import abc.notation.AccidentalType;
import abc.notation.Chord;
import abc.notation.MusicElement;
import abc.ui.scoretemplates.ScoreElements;

/**
 * TODO doc
 */
public class JText extends JScoreElementAbstract {

	private byte m_textField;

	private String m_text = null;

	/**
	 * Constructor
	 * 
	 * @param mtrx
	 *            The score metrics needed
	 * @param text
	 *            The text
	 * @param textField
	 *            One of {@link ScoreElements} constants
	 */
	protected JText(ScoreMetrics mtrx, String text, byte textField) {
		super(mtrx);
		this.m_text = text;
		this.m_textField = textField;
	}

	/**
	 * Returns the horizontal alignment
	 * 
	 * @see ScoreTemplate#getPosition(byte)
	 * @return one of {@link abc.ui.scoretemplates.HorizontalAlign} constants
	 */
	public byte getHorizontalAlignment() {
		return getTemplate().getPosition(m_textField)[1];
	}


	/**
	 * Returns the vertical alignment
	 * 
	 * @see ScoreTemplate#getPosition(byte)
	 * @return one of {@link abc.ui.scoretemplates.VerticalAlign} constants
	 */
	public byte getVerticalAlignment() {
		return getTemplate().getPosition(m_textField)[0];
	}
	
	public Rectangle2D getBoundingBox() {
		Dimension dim = getDimension();
		return new Rectangle2D.Double(getBase().getX(),
				getBase().getY()-dim.getHeight(),
				dim.getWidth(),
				dim.getHeight());
	}
	
	public Dimension getDimension() {
		return new Dimension((int) getWidth(), (int) getHeight());
	}

	/**
	 * Returns the height of this score element.
	 * 
	 * @return The height of this score element.
	 */
	public double getHeight() {
		return (double) getMetrics().getTextFontHeight(m_textField);
	}

	/**
	 * Returns the tune's music element represented by this graphical score
	 * element.
	 * 
	 * @return The tune's music element represented by this graphical score
	 *         element. <TT>null</TT> if this graphical score element is not
	 *         related to any music element.
	 * @see MusicElement
	 */
	public MusicElement getMusicElement() {
		return null;
	}

	public String getText() {
		String p = getTemplate().getTextPrefix(m_textField);
		String s = getTemplate().getTextSuffix(m_textField);
		p = p!=null?p:"";
		s = s!=null?s:"";
		return p+m_text+s;
	}

	/**
	 * Returns the width of this score element.
	 * 
	 * @return The width of this score element.
	 */
	public double getWidth() {
		return (double) getMetrics().getTextFontWidth(m_textField, getText());
	}

	/** Callback invoked when the base has changed for this object. */
	protected void onBaseChanged() {
		// does nothing
	}

	/**
	 * Renders this Score element to the given graphic context.
	 * 
	 * @param g2
	 */
	public double render(Graphics2D g2) {
		Font previousFont = g2.getFont();
		Color previousColor = g2.getColor();

		Font font = getTemplate().getTextFont(m_textField);
		g2.setFont(font);
		setColor(g2, m_textField);
		LineMetrics lineMetrics = font.getLineMetrics(getText(), g2.getFontRenderContext());
		float leading = (float) Math.ceil(Math.max(lineMetrics.getLeading(), lineMetrics.getDescent()));
		//System.out.println("leading "+getText()+" = "+leading);

		//music font for sharp and flats
		Font musicFont;
		try {
			musicFont = getMusicalFont().getFont();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		musicFont = musicFont.deriveFont(font.getSize()*3f);
		//sharp is translated up a little :)
		AffineTransform at = AffineTransform.getTranslateInstance(0, -font.getSize()/2);
		Font musicFontSharp = musicFont.deriveFont(at);
		
		char sharp = getMusicalFont().getAccidental(AccidentalType.SHARP);
		char flat = getMusicalFont().getAccidental(AccidentalType.FLAT);
		String text = getText().replace(Chord.UNICODE_SHARP, sharp)
								.replace(Chord.UNICODE_FLAT, flat);
		AttributedString as = new AttributedString(text);
		as.addAttributes(font.getAttributes(), 0, text.length());
		int begin = -1;
		for (int i = 0; i < text.length(); i++) {
			char charAtI = text.charAt(i);
			if (begin == -1)
				begin = i;
			if ((charAtI == sharp) || (charAtI == flat)) {
				if (begin != i) {
					as.addAttribute(TextAttribute.FONT, font, begin, i);
				}
				as.addAttribute(TextAttribute.FONT,
						charAtI==sharp?musicFontSharp:musicFont,
						i, i+1);
				begin = -1;
			}
		}
		if (begin != -1) {
			as.addAttributes(font.getAttributes(), begin, text.length());
		}

		Map attrib = getTemplate().getTextAttributes(m_textField);
		if (attrib != null) {
			Iterator it = attrib.keySet().iterator();
			while (it.hasNext()) {
				Attribute ta = (Attribute) it.next();
				as.addAttribute(ta, attrib.get(ta));
			}
		}
		//end of flat/sharp replacement
		
		g2.drawString(as.getIterator(),//getText(),
				(float) getBase().getX(),
				(float) getBase().getY()-leading);
		
		g2.setFont(previousFont);
		g2.setColor(previousColor);
		
		//renderDebugBoundingBox(g2);
		return getWidth();
	}

}
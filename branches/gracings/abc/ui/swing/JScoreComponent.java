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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import abc.notation.MusicElement;
import abc.notation.Tune;

/**
 * This JComponent displays tunes scores. You can get scores such as : <BR/>
 * <CENTER>
 * <IMG src="../../../images/scoreEx.jpg"/> </CENTER>
 * <BR/>
 * To render a tune score, just invoke the <TT>setTune(Tune)</TT> method
 * with your tune as parameter.<BR/>
 * Basically, a score if composed of {@link abc.ui.swing.JScoreElement score elements}
 * @see Tune
 * @see JScoreElement
 */
public class JScoreComponent extends JComponent {

	private static final long serialVersionUID = 7903517456075406436L;
	private static final Color SELECTED_ITEM_COLOR = Color.RED;
	/** The graphical representation of the tune currently set.
	 * <TT>null</TT> if no tune is set. */
	private JTune m_jTune = null;

	//private int staffLinesOffset = -1;
	/** The dimensions of this score. */
	private Dimension m_dimension = null;
	/** The space of the left margin */
	// NOTE: must set XOffset for JTune.setMarginLeft() for offset to have any effect
	private int XOffset = 0;
	/** The place where all spacing dimensions are expressed. */
	private ScoreMetrics m_metrics = null;
	private Engraver m_engraver = null;
	/** The buffer where the score image is put before rendition in the swing component. */
	private BufferedImage m_bufferedImage = null;
	/** The graphic context of the buffered image used to generate the score. */
	private Graphics2D m_bufferedImageGfx = null;
	/** Set to <TT>true</TT> if the score drawn into the buffered image is
	 * outdated and does not represent the tune currently set. */
	private boolean m_isBufferedImageOutdated = true;
	/** The size used for the score scale. */
	//protected float m_size = 45;
	/** <TT>true</TT> if the rendition of the score should be justified,
	 * <TT>false</TT> otherwise. */
	private boolean m_isJustified = false;
	/** The selected item in this score. <TT>null</TT> if no
	 * item is selected. */
	private JScoreElement m_selectedItem = null;

	//protected int staffLinesSpacing = -1;

	/** Default constructor. */
	public JScoreComponent() {
		m_dimension = new Dimension(1,1);
		initGfx();
	}

	protected void initGfx(){
		m_bufferedImage = new BufferedImage((int)m_dimension.getWidth(), (int)m_dimension.getHeight(), BufferedImage.TYPE_INT_ARGB);
		m_bufferedImageGfx = (Graphics2D)m_bufferedImage.createGraphics();
		//staffLinesSpacing = (int)(m_metrics.getStaffCharBounds().getHeight()*2.5);
	}

	public ScoreMetrics getScoreMetrics() {
		if (m_metrics == null)
			m_metrics = new ScoreMetrics(m_bufferedImageGfx);
		return m_metrics;
	}
	public Engraver getEngraver() {
		if (m_engraver == null)
			m_engraver = new Engraver(Engraver.DEFAULT);
		return m_engraver;
	}

	/** Draws the current tune score into the given graphic context.
	 * @param g Graphic context. */
	protected void drawIn(Graphics2D g){
		if(m_jTune!=null) {
			m_jTune.render(g);
		}
	}

	public void paint(Graphics g){
		if (m_isBufferedImageOutdated) {
			//System.out.println("buf image is outdated");
			if (m_bufferedImage==null || m_dimension.getWidth()>m_bufferedImage.getWidth()
					|| m_dimension.getHeight()>m_bufferedImage.getHeight()) {
				initGfx();
			}
			m_bufferedImageGfx.setColor(getBackground());
			m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
			drawIn(m_bufferedImageGfx);
			m_isBufferedImageOutdated=false;
		}
		((Graphics2D)g).drawImage(m_bufferedImage, 0, 0, null);
		//if (m_jTune!=null)
		//	m_jTune.render((Graphics2D)g);
	}

	/** The size of the font used to display the music score.
	 * @param size The size of the font used to display the music score expressed in ?
	 * @deprecated use {@link #getScoreMetrics() getScoreMetrics()}.{@link ScoreMetrics#setNotationSize(float) setNotationSize(float)} and then {@link #refresh()}
	 */
	public void setSize(float size){
		getScoreMetrics().setNotationFontSize(size);
		refresh();
	}

	/**
	 * Refresh the score, blanks the component, compute and draw
	 * the score.<br>Call this method to refresh the component
	 * after changes on its {@link #getEngraver() Engraver} and
	 * {@link #getScoreMetrics() ScoreMetric}.
	 */
	public void refresh() {
		initGfx();
		if (m_jTune!=null)
			setTune(m_jTune.getTune());
		repaint();
	}

	/** Writes the currently set tune score to a PNG file.
	 * @param file The PNG output file.
	 * @throws IOException Thrown if the given file cannot be accessed. */
	public void writeScoreTo(File file) throws IOException {
		//if (m_bufferedImage==null || m_dimension.getWidth()>m_bufferedImage.getWidth()
		//		|| m_dimension.getHeight()>m_bufferedImage.getHeight()) {
		if (m_metrics == null)
			initGfx();
		//}
		m_bufferedImageGfx.setColor(getBackground());
		//m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
		m_bufferedImageGfx.setComposite(AlphaComposite.Clear);
		m_bufferedImageGfx.fillRect(0, 0, (int)m_bufferedImage.getWidth(), (int)m_bufferedImage.getHeight());
		m_bufferedImageGfx.setComposite(AlphaComposite.SrcOver);
		drawIn(m_bufferedImageGfx);
		m_isBufferedImageOutdated=false;
		ImageIO.write(m_bufferedImage,"png",file);
	}

	/** Sets the tune to be renderered.
	 * @param tune The tune to be displayed. */
	public void setTune(Tune tune){
		if (m_metrics==null)
			initGfx();
		m_jTune = new JTune(tune,
							new Point(XOffset, 0),
							getScoreMetrics(),
							getEngraver(),
							isJustified());
		//m_jTune.setStaffLinesSpacing(staffLinesSpacing);
		m_selectedItem = null;
		m_dimension.setSize(m_jTune.getWidth(), m_jTune.getHeight());
				//componentHeight+m_metrics.getStaffCharBounds().getHeight());
		setPreferredSize(m_dimension);
		setSize(m_dimension);
		//if (m_isJustified)
		//	justify();
		m_isBufferedImageOutdated=true;
		repaint();
	}

	/** Changes the justification of the rendition score. This will
	 * set the staff lines aligment to justify in order to have a more
	 * elegant display.
	 * @param isJustified <TT>true</TT> if the score rendition should be
	 * justified, <TT>false</TT> otherwise.
	 * @see #isJustified()
	 * @deprecated call {@link #setJustified(boolean)} and then
	 * {@link #refresh()} when you have finished to change all
	 * settings (justification, engraving, score metrics...)
	 */
	public void setJustification(boolean isJustified) {
		m_isJustified = isJustified;
		//triggers the recalculation of the tune
		if (m_jTune!=null)
			setTune(m_jTune.getTune());
		//m_jTune = new m_jTune(m_jTune.getTune(), )
		//repaint();
	}

	/** Changes the justification of the rendition score. This will
	 * set the staff lines aligment to justify in order to have a more
	 * elegant display.
	 * @param isJustified <TT>true</TT> if the score rendition should be
	 * justified, <TT>false</TT> otherwise.
	 * @see #isJustified()
	 */
	public void setJustified(boolean isJustified) {
		m_isJustified = isJustified;
	}

	/*public void setStaffLinesSpacing(int spacing) {
		staffLinesSpacing = spacing;
		//triggers the recalculation of the tune
		if (m_jTune!=null)
			setTune(m_jTune.getTune());
		//if (m_jTune!=null)
		//	setTune(m_jTune.getTune());
		//m_jTune = new m_jTune(m_jTune.getTune(), )
		//repaint();
	}  */

	/*public int getStaffLinesSpacing() {
		return staffLinesSpacing;
	}*/

	/** Return <TT>true</TT> if the rendition staff lines alignment is
	 * justified, <TT>false</TT> otherwise.
	 * @return <TT>true</TT> if the rendition staff lines alignment is
	 * justified, <TT>false</TT> otherwise.
	 * @see #setJustification(boolean) */
	public boolean isJustified() {
		return m_isJustified;
	}

	/** Returns the graphical score element fount at the given location.
	 * @param location A point in the score.
	 * @return The graphical score element found at the specified location.
	 * <TT>null</TT> is returned if no item is found at the given location. */
	public JScoreElement getScoreElementAt(Point location) {
		if (m_jTune!=null)
			return m_jTune.getScoreElementAt(location);
		else
			return null;
	}

	/** Highlights the given score element in the score.
	 * If an item was previously selected, this previous item
	 * is unselected.
	 * @param elmnt The music element to be highlighted in the
	 * score. <TT>null</TT> can be specified to remove
	 * highlighting.
	 * @see #setSelectedItem(JScoreElement) */
	public void setSelectedItem(MusicElement elmnt) {
		JScoreElementAbstract r = null;
		if (elmnt!=null)
			r = (JScoreElementAbstract)m_jTune.getRenditionObjectsMapping().get(elmnt);
		//if (r!=null)
		//	System.out.println("Selecting item " + elmnt + "->" + r + "@" + r.getBase());
		setSelectedItem(r);
	}

	/** Highlights the given score element in the score.
	 * If an item was previously selected, this previous item
	 * is unselected.
	 * @param elmnt The score rendition element to be highlighted in the
	 * score. <TT>null</TT> can be specified to remove
	 * highlighting.
	 * @see #setSelectedItem(MusicElement) */
	public void setSelectedItem(JScoreElement elmnt){
		if (m_selectedItem!=null) {
			m_bufferedImageGfx.setColor(Color.BLACK);
			((JScoreElementAbstract)m_selectedItem).render(m_bufferedImageGfx);
		}
		if (elmnt!=null) {
			m_selectedItem = elmnt;
			m_bufferedImageGfx.setColor(SELECTED_ITEM_COLOR);
			((JScoreElementAbstract)m_selectedItem).render(m_bufferedImageGfx);
		}
		repaint();
	}

	/** Returns the graphical element that corresponds to a tune element.
	 * @param elmnt A tune element.
	 * @return The graphical score element that corresponds to the given
	 * tune element. <TT>null</TT> is returned if the given tune element
	 * does not have any graphical representation. */
	public JScoreElement getRenditionElementFor(MusicElement elmnt) {
		if (m_jTune!=null)
			return (JScoreElement)m_jTune.getRenditionObjectsMapping().get(elmnt);
		else
			return null;
	}

}
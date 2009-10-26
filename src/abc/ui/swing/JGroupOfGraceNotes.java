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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.Note;
import abc.notation.NoteAbstract;

/** This class is in charge of rendering a group of grace notes whose stems should be linked. */
class JGroupOfGraceNotes extends JGroupOfNotes {

	// used to request glyph-specific metrics
	// in a genric way that enables positioning, sizing, rendering
	// to be done generically
	// subclasses should override this attrribute.
	protected int NOTATION_CONTEXT = ScoreMetrics.GRACENOTE_GLYPH;

	protected boolean renderSlash = true;
	protected Point2D slashStart = null;
	protected Point2D slashEnd = null;

	public JGroupOfGraceNotes (ScoreMetrics metrics, Point2D base, NoteAbstract[] notes, Engraver engraver){
		super(metrics, base, notes, engraver);

		JGraceNotePartOfGroup graceNote = null;
		for (int i=0; i<notes.length; i++) {
			graceNote = new JGraceNotePartOfGroup((Note)m_notes[i], base /*new Point2D.Double()*/, metrics);
			graceNote.setAutoStem(false);
			graceNote.setStemUp(true);
			m_jNotes[i] = graceNote;
		}

		slashStart = new Point2D.Double(0,0);
		slashEnd = new Point2D.Double(0,0);

		// force stem direction - gracenotes are always stemmed UP
		setAutoStem(false);
		setStemUp(true);
	}

	public void setRenderSlash(boolean render) {
		renderSlash = render;
	}

	public void onBaseChanged() {
		super.onBaseChanged();

		// calculate slash position
		Dimension d = getMetrics().getGlyphDimension(NOTATION_CONTEXT);

		if (d == null) return;
		JGroupableNote firstNote = m_jNotes[0];
		Point2D displayPosition = ((JNote)firstNote).getDisplayPosition();
		boolean isUp = ((JNote)firstNote).isStemUp();
		int startX = 0;
		int endX = 0;
		int startY = 0;
		int endY = 0;
		if (isUp) {
			startX = (int) (displayPosition.getX() + d.getWidth()*0.5);
			endX = (int) (displayPosition.getX() + d.getWidth()*5.5);
			startY = (int) (firstNote.getStemYEnd() + d.getWidth()*2.5);
			endY = (int) (firstNote.getStemYEnd() - d.getWidth()*1.5);
		} else {
			startX = (int) (displayPosition.getX() + d.getWidth()*0.5);
			endX = (int) (displayPosition.getX() + d.getWidth()*5.5);
			startY = (int) (firstNote.getStemYEnd() - d.getWidth()*2.5);
			endY = (int) (firstNote.getStemYEnd() + d.getWidth()*1.5);
		}
		slashStart.setLocation(startX, startY);
		slashEnd.setLocation(endX, endY);
	}

	public double render(Graphics2D context) {
		super.render(context);

		if (renderSlash) {
			Stroke dfs = context.getStroke();
			context.setStroke(getMetrics().getStemStroke());
			context.drawLine((int)slashStart.getX(), (int)slashStart.getY(), (int)slashEnd.getX(), (int)slashEnd.getY());
			context.setStroke(dfs);
		}
		return getWidth();
	}
}

package abc.notation;

import java.util.Vector;

/** A class to define tuplets. */
public class Tuplet
{
  /** Notes composing the tuplet. */
  private Vector m_notes = null;
  private int m_totalRelativeLength = -1;
  private short m_totalDuration = -1;

  /** Creates a new tuplet composed of the specified notes. The total length
   * of this tuplet will be equals to the totalRelativeLength * defaultLength.
   * @param notes The <TT>NoteAbstract</TT> obejcts composing this tuplet,
   * encapsulated inside a <TT>Vector</TT>.
   * @param totalRelativeLength The total relative length of this tuplet
   * multiplied by the delfault relative length gives the total absolute length
   * of this tuplet. */
  public Tuplet (Vector notes, int totalRelativeLength, short defaultNoteLength)
  {
    m_notes = notes;
    m_totalRelativeLength = totalRelativeLength;
    m_totalDuration = (short)(m_totalRelativeLength * defaultNoteLength);
    for (int i=0; i<notes.size(); i++)
      ((NoteAbstract)notes.elementAt(i)).setTuplet(this);
  }

  /** Returns the total relative length of this tuplet.
   * @return The total relative length of this tuplet. The total relative length
   * of this tuplet multiplied by the default relative length gives the total
   * absolute length of this tuplet.
   * @deprecated use totalDuration() instead. Reference to relative length should
   * be avoided in the API bacause this is only related to the "abc world". 
   * @see #getTotalDuration() */
  public int getTotalRelativeLength()
  { return m_totalRelativeLength; }
  
  /** Returns the total duration of this tuplet.
   * @return The total duration of this tuplet. */  
  public short getTotalDuration() {
	  return m_totalDuration;
  }

  /** Returns a new vector containing all notes of this multi note.
   * @return A new vector containing all notes of this multi note. */
  public Vector getNotesAsVector()
  { return (Vector)m_notes.clone(); }

}
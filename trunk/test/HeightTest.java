import junit.framework.TestCase;
import abc.midi.MidiConverterAbstract;
import abc.notation.AccidentalType;
import abc.notation.KeySignature;
import abc.notation.MultiNote;
import abc.notation.Note;
import abc.notation.Tune;
import abc.parser.TuneParser;


public class HeightTest extends TestCase {
	public static void main(String[] args) {
	}

	public HeightTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void test1(){
		Note note = new Note(Note.C, AccidentalType.NONE);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(0, note.getOctaveTransposition());
		
		note = new Note(Note.c, AccidentalType.NONE);
		assertEquals(Note.C, note.getStrictHeight());
		note.setAccidental(AccidentalType.SHARP);
		assertEquals(Note.C, note.getStrictHeight());
		
		note = new Note(Note.C, AccidentalType.NONE, (byte)1);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(Note.c, note.getHeight());
		assertEquals(1, note.getOctaveTransposition());
		
		note = new Note(Note.c, AccidentalType.NONE, (byte)1);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(2, note.getOctaveTransposition());
		
		note = new Note(Note.c, AccidentalType.NONE);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(Note.c, note.getHeight());
		assertEquals(1, note.getOctaveTransposition());
		
		note = new Note(Note.C, AccidentalType.NONE, (byte)2);
		assertEquals(Note.C, note.getStrictHeight());
		assertEquals(2, note.getOctaveTransposition());
		assertEquals(Note.c*2, note.getHeight());
		
		note = new Note(Note.REST, AccidentalType.NONE);
		assertEquals(Note.REST, note.getStrictHeight());
		assertEquals(0, note.getOctaveTransposition());
	}
	
	public void testMidiHeight() {
		Note note = new Note(Note.C, AccidentalType.NONE);
		KeySignature ks = new KeySignature(Note.C, KeySignature.MAJOR);
		//60 corresponds to the midi note number for the C note.
		assertEquals(60, MidiConverterAbstract.getMidiNoteNumber(note, ks));
		note.setAccidental(AccidentalType.SHARP);
		assertEquals(61, MidiConverterAbstract.getMidiNoteNumber(note, ks));
		note.setAccidental(AccidentalType.NONE);
		note.setOctaveTransposition((byte)1);
		assertEquals(72, MidiConverterAbstract.getMidiNoteNumber(note, ks));
		
	}
	
	public void testNoteHeightComparison() {
		String tuneAsString = "X:1\nT:test\nK:c\nabcdef\n";
		TuneParser tuneParser = new TuneParser();
		Tune tune = tuneParser.parse(tuneAsString);
		Note firstNote = (Note)tune.getScore().elementAt(1);
		Note lastNote = (Note)tune.getScore().elementAt(6);
		//System.out.println("highest note : " + tune.getScore().getHighestNoteBewteen(0, tune.getScore().size()-1));
		assertEquals(Note.b, tune.getScore().getHighestNoteBewteen(firstNote, lastNote).getHeight());
	}
	
	/** */
	public void test2(){
		String tuneAsString = "X:1\nT:test\nK:c\nb/2b2bb4b8\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		Note firstNote = (Note)tune.getScore().elementAt(1);
		Note secondNote = (Note)tune.getScore().elementAt(2);
		Note thirdNote = (Note)tune.getScore().elementAt(3);
		Note fourthNote = (Note)tune.getScore().elementAt(4);
		assertEquals(firstNote.getHeight(), Note.b);
		assertEquals(secondNote.getHeight(), Note.b);
		assertEquals(thirdNote.getHeight(), Note.b);
		assertEquals(fourthNote.getHeight(), Note.b);
	}
	
	/** */
	public void test3(){
		Tune tune = new TuneParser().parse("X:1\nT:test\nK:c\naFc\n");
		Note firstNote = (Note)tune.getScore().elementAt(1);
		Note secondNote = (Note)tune.getScore().elementAt(2);
		Note thirdNote = (Note)tune.getScore().elementAt(3);
		assertEquals(tune.getScore().getLowestNoteBewteen(firstNote, thirdNote), secondNote);
		assertEquals(tune.getScore().getHighestNoteBewteen(firstNote, thirdNote), firstNote);
	}
	
	public void test4(){
		Tune tune = new TuneParser().parse("X:1\nT:test\nK:c\n[aFc]\n");
		MultiNote firstNote = (MultiNote)tune.getScore().elementAt(1);
		//Note secondNote = (Note)tune.getScore().elementAt(2);
		//Note thirdNote = (Note)tune.getScore().elementAt(3);
		assertEquals(firstNote.getHighestNote().getStrictHeight(), Note.A);
		assertEquals(firstNote.getLowestNote().getStrictHeight(), Note.F);
	}
	
	public void testNoteVariousHeightComparison() {
		String tuneAsString = "X:1\nT:test\nK:c\nA,B,CDEFa,b,cdef\n";
		Tune tune = new TuneParser().parse(tuneAsString);
		for (int i=0; i<tune.getScore().size()-1; i++)
			if (tune.getScore().elementAt(i) instanceof Note && tune.getScore().elementAt(i+1) instanceof Note)
				assertFalse(((Note)tune.getScore().elementAt(i)).isHigherThan((Note)tune.getScore().elementAt(i+1)));
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}

package ballphys;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Plays tones for the world
 *
 * @author knispeja.
 *         Created Dec 21, 2014.
 */
public class MusicBox {

	SourceDataLine line;
	
    public MusicBox() {
        final AudioFormat af =
            new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
        
		try {
			this.line = AudioSystem.getSourceDataLine(af);
	        this.line.open(af, Note.SAMPLE_RATE);
	        this.line.start();
	        for  (Note n : Note.values()) {
	            play(n, 65);
	            play(Note.REST, 7);
	        }
	        
	        //line.drain();
	        //line.close();
		} catch (LineUnavailableException e) {
			System.err.println("Error with audio: " + e.getStackTrace());
		}
    }
    
    public void play(final Note note, final int ms){
    	play(this.line, note, ms);
    }
    
    /**
     * Plays a note for a given amount of time
     * @param line 
     * @param note
     * @param ms
     */
    public static void play(final SourceDataLine line, final Note note, final int ms) {
    	
		Runnable tickTock = new Runnable() {
			@Override
			public void run() {
		        final AudioFormat af =
		                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, true);
				int ms2 = Math.min(ms, Note.SECONDS * 1000);
				int length = Note.SAMPLE_RATE * ms2 / 1000;
				line.write(note.data(), 0, length);
			}
		};
		
		new Thread(tickTock).start();
    }
}
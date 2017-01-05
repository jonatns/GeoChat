//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
	
	private Clip clip;
	
	public Sound(URL url) {
		
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audioStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void playLoop() {
		clip.flush();
		clip.start();
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		System.out.println("playing sound...");
	}
	
	public void stop() {
		clip.stop();
		clip.flush();
	}
}

//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class playThread extends Thread {
    static SourceDataLine speakers;
	
	public static SourceDataLine getSpeakers() {
		return speakers;
	}

	public static void setSpeakers(SourceDataLine speakers) {
		playThread.speakers = speakers;
	}

	public void run() {
		
		super.run();
		
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        int numBytesRead = 0;
        int bytesRead = 0;
        
        byte[] data = new byte[100];

        try {
	        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
	        speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
	        speakers.open(format);
	        speakers.start();
	        
	        while(Caller.isCapturing) {
	        	
		        numBytesRead = Caller.in.read(data, bytesRead, data.length - bytesRead);
		        
		        if (numBytesRead >= 0) {
		        	
			        bytesRead += numBytesRead;
			        
			        if (bytesRead == data.length) {
				        speakers.write(data, 0, data.length); 
			            bytesRead = 0;
			        }
	        	} else {
	        		break;
	        	}
		 
	        }
	        
        } catch (IOException e) {
            e.printStackTrace();
        } catch(LineUnavailableException e) {
        	e.printStackTrace();
        }
		
	}

}

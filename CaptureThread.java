//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class CaptureThread extends Thread {
    TargetDataLine microphone;
		
	public void run() {
		
		super.run();
	
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        int numBytesRead = 0;
        int bytesRead = 0;
		
		try {
			
		    microphone = AudioSystem.getTargetDataLine(format);
	        microphone = (TargetDataLine) AudioSystem.getLine(info);
	        microphone.open(format);
	        
	        byte[] data = new byte[100];

	        microphone.start();
		    
	        while(Caller.isCapturing) {
	
		        numBytesRead = microphone.read(data, bytesRead, data.length - bytesRead);
		        
		        if (numBytesRead >= 0) {
		        	
			        bytesRead += numBytesRead;
			        
			        if (bytesRead == data.length) {
				        Caller.out.write(data, 0, data.length); 
			            bytesRead = 0;
			        }
	        	} else {
	        		break;
	        	}
		 
	        }
	        
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	  }
}

//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.awt.Frame;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

public class SocketReaderThread extends Thread {
	
    private static BufferedReader in;
    int incomingCall;

	public SocketReaderThread() {
    	try {
    		setIn(new BufferedReader(new InputStreamReader(Connection.getSocket().getInputStream())));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public void run() {
		
		super.run();
				
		try {
			String serverIn;
			while (true) {
				serverIn = in.readLine();
				String cleanString = serverIn.replaceAll("\\s+"," ");
																					
				if(cleanString.contains("new")) {
					String[] data = cleanString.split(" ");
					String[] location = {data[2], data[3]};
					MapFrame.addMarker(data[1], location);
					cleanString = "";
					serverIn = "";		
				} else if(cleanString.contains("users")) {
					int amount = Integer.parseInt(cleanString.split(":")[1]);
					while(amount > 0) {
						serverIn = in.readLine();
						String userData[] = serverIn.split(",");
						String[] userLocation = {userData[1], userData[2]};
						MapFrame.addMarker(userData[0], userLocation);	
						amount--;
					}
					cleanString = "";
					serverIn = "";	
				} else if(cleanString.contains("updatelocation")) {
					String[] data = cleanString.split(" ");
					String[] location = {data[2], data[3]};
					MapFrame.updateMarker(data[1], location);
					cleanString = "";
					serverIn = "";	
				} else if(cleanString.contains("incomingcall")) {
			        URL soundURL = JMapViewer.class.getResource("sounds/call.wav");
					Sound callSound = new Sound(soundURL);
					callSound.playLoop();
					String[] data = cleanString.split(" ");
					String user = data[1];
			        URL callURL = JMapViewer.class.getResource("images/call.png");
			        final ImageIcon callIcon = new ImageIcon(callURL);
			        String[] options = new String[2];
			        options[0] = new String("Answer");
			        options[1] = new String("Ignore");
					incomingCall = JOptionPane.showOptionDialog(MapFrame.map, user + " is calling!", "Incoming Call", 0, JOptionPane.YES_NO_OPTION, callIcon, options, null);
					if(incomingCall == JOptionPane.YES_OPTION) {
						callSound.stop();
						SocketWriterThread.getOut().println("acceptedcall " + user + " " + MapFrame.uniqueID);
						MapFrame.call = new Caller(user);
						Caller.callBtn.setEnabled(false);
						Caller.endBtn.setEnabled(true);
						Caller.callLabel.setText("Call in progress...");
						Caller.acceptCall();
					} else {
						callSound.stop();
						SocketWriterThread.getOut().println("ignoredcall " + user);
					}
					cleanString = "";
					serverIn = "";	
				} else if(cleanString.contains("acceptedcall")) {
					System.out.println("accepted");
					Caller.isCapturing = true;
					Caller.out = new BufferedOutputStream(Connection.getSocket().getOutputStream());
					Caller.in = new BufferedInputStream(Connection.getSocket().getInputStream());
					Caller.captureAudio();
					Caller.playAudio();
					Caller.callLabel.setText("Call in progress...");
					//call.acceptCall();
					cleanString = "";
					serverIn = "";	
				} else if(cleanString.contains("ignoredcall")) {
					Caller.callLabel.setText("Call ended");
					Caller.callBtn.setEnabled(false);
			        Caller.endBtn.setEnabled(true);
					cleanString = "";
					serverIn = "";	
				} else if(cleanString.contains("endcall")) {
					Caller.callLabel.setText("Call ended");
					cleanString = "";
					serverIn = "";	
				} else if(cleanString.contains("end")) {
					String[] data = cleanString.split(" ");
					MapFrame.removeMarker(data[1]);
					cleanString = "";
					serverIn = "";									
				} else {
					cleanString = "";
					serverIn = "";
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
    public static BufferedReader getIn() {
		return in;
	}

	public static void setIn(BufferedReader in) {
		SocketReaderThread.in = in;
	}

}

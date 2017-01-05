//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Caller extends JDialog {
	
	static Boolean isCapturing = false;
	static String userID = "0";
    static BufferedOutputStream out;
    static BufferedInputStream in;
	static JButton callBtn = new JButton("Call");
	static JButton endBtn = new JButton("End");
	static JLabel callLabel = new JLabel();
	static Thread captureThread;
	static Thread playThread;
	
	public Caller() {		
				
		endBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		        endBtn.setEnabled(false);
		        isCapturing = false;
				SocketWriterThread.getOut().println("endcall " + userID);
		        dispose();
			}
			
		});
		
		JPanel callerPanel = new JPanel();
		callerPanel.add(callLabel);
		getContentPane().add(callerPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(endBtn); 
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - 200) / 2);
	    int y = (int) ((dimension.getHeight() - 200) / 2);
		setLocation(x, y);
		pack(); 
		setVisible(true);

	}
 	
	public Caller(String user) {
		
		userID = user;
		
		
		callBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				callBtn.setEnabled(false);
		        endBtn.setEnabled(true);
		        call();
			}
			
		});
		endBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
		        isCapturing = false;
		        SocketWriterThread.getOut().println("endcall " + userID);
		        dispose();
			}
			
		});
		
		JPanel callerPanel = new JPanel();
		callerPanel.add(callLabel);
		getContentPane().add(callerPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(callBtn); 
		buttonPanel.add(endBtn); 
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - 200) / 2);
	    int y = (int) ((dimension.getHeight() - 200) / 2);
		pack(); 
        setLocationRelativeTo(MapFrame.map);
        setSize(new Dimension(250, 100));
		setVisible(true);

	}
	
	public static void call() {
		SocketWriterThread.getOut().println("call " + userID + " " + MapFrame.uniqueID);
	}
	
	public static void endCall() {
		isCapturing = false;
	}
	
	
	public static void ignoredCall() {

	}
	
	public static void acceptCall() {
		
        Connection conn = new Connection();

		try {
			isCapturing = true;
			out = new BufferedOutputStream(conn.getSocket().getOutputStream());
			in = new BufferedInputStream(conn.getSocket().getInputStream());
			captureAudio();
			playAudio();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void captureAudio() {
		
		captureThread = new CaptureThread();
		captureThread.start();
	
	}
	
	public static void playAudio() {
		playThread = new playThread();
		playThread.start();
	}

}

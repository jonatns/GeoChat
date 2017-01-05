//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Connection {
	
    private static Socket socket;

	public Connection() {
		try {
			socket = new Socket("138.68.27.112", 5000);
			//socket = new Socket("localhost", 5000);
			new SocketWriterThread();
			new SocketReaderThread().start();
	        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	            public void run() {
					SocketWriterThread.getOut().println("end " + MapFrame.uniqueID);
	            }
	        }, "Shutdown-thread"));
		} catch (IOException e) {
			e.printStackTrace();
        	JOptionPane.showMessageDialog(null, "Cannot connect to server");
        	System.exit(1);
		}	
	}
	
	public static Socket getSocket() {
		return socket;
	}

	public static void setSocket(Socket socket) {
		Connection.socket = socket;
	}
}

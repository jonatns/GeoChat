//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class SocketWriterThread {
	
    private static PrintWriter out;
	
    public SocketWriterThread() {
    	try {
			setOut(new PrintWriter(Connection.getSocket().getOutputStream(), true));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
	public static PrintWriter getOut() {
		return out;
	}

	public static void setOut(PrintWriter out) {
		SocketWriterThread.out = out;
	}
    

}

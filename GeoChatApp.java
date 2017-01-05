//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.io.IOException;

public class GeoChatApp {
	
	public GeoChatApp() {
		new Connection();
		try {
			new MapFrame();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
  
    public static void main(String[] args) {
    	new GeoChatApp();
    }
}

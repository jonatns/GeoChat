//Jonathan P. Navarrete
//802-09-5108
//12 Dec. 2016

package apps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;

@SuppressWarnings("serial")
public class MapFrame extends JFrame {
	
	static JMapViewer map = new JMapViewer();
    static MapMarkerDot myMapMarkerDot;
    static DecimalFormat df = new DecimalFormat("######.0000");
    static Boolean isChangingLocation = false;
    static String uniqueID;
    static Map<String, MapMarker> users = new HashMap<String, MapMarker>();
	static Caller call;
    
    public MapFrame() throws IOException {
    	
        String url = "https://ipinfo.io/json";
        String locationJSON = getData(url);
        double lat = 18.46633;
        double lon = -66.10572;
        
        if(!locationJSON.isEmpty()) {
        	
	        Genson genson = new Genson();
	        Map<String, String> locationMap = genson.deserialize(locationJSON, new GenericType<Map<String, String>>(){});
	        lat = Double.parseDouble(locationMap.get("loc").split(",")[0]);
	        lon = Double.parseDouble(locationMap.get("loc").split(",")[1]);
	        
        }
	
	        Layer myLayer = new Layer("you");
	        myMapMarkerDot = new MapMarkerDot(myLayer, "you", new Coordinate(lat, lon), new Style());
	        myMapMarkerDot.setColor(new Color(255, 69, 1));
	        myMapMarkerDot.setBackColor(new Color(255, 69, 1));
	                   		
	        map.addMapMarker(myMapMarkerDot);
		    map.setDisplayToFitMapMarkers();
		    map.setZoom(16);	
		    
	        URL markerURL = JMapViewer.class.getResource("images/marker.png");
	        final ImageIcon markerIcon = new ImageIcon(markerURL);
	        JButton myLocationBtn = new JButton(markerIcon);
			final JLabel infoLabel = new JLabel("Changing location...");

	        myLocationBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!isChangingLocation) {
						int changeLocationDialog = JOptionPane.showConfirmDialog(map, "Do you want to change your location?", "Message", 0, JOptionPane.YES_NO_OPTION, markerIcon);

						if(changeLocationDialog == JOptionPane.YES_OPTION) {
							isChangingLocation = true;
							infoLabel.setBounds(25, map.getSize().height - 100, 500, 100);
							infoLabel.setFont(new Font("Serif", Font.PLAIN, 24));
							map.add(infoLabel);
							map.repaint();
							JOptionPane.showMessageDialog(map, "Press the location button when you are done.", "Message", JOptionPane.INFORMATION_MESSAGE, markerIcon);
						}
						
					} else {
						map.remove(infoLabel);
						map.repaint();
						isChangingLocation = false;
					}
				}
			});
	        
	        myLocationBtn.setBounds(0, 200, 50, 50);
	        myLocationBtn.setBackground(Color.GREEN);
	        myLocationBtn.setForeground(Color.GREEN);
	        map.add(myLocationBtn);
		
        new DefaultMapController(map){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (doubleClickZoomEnabled && e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
                	for (MapMarker marker : map.getMapMarkerList()) {
                		String mousePositionLat = df.format(map.getPosition(e.getPoint()).getLat());
                		String mousePositionLon = df.format(map.getPosition(e.getPoint()).getLon());
                		String[] LatLon1 = {mousePositionLat, mousePositionLon};
                		String[] LatLon2 = {String.valueOf(marker.getLat()) , String.valueOf(marker.getLon()) };
                		if(distance(LatLon1, LatLon2) && !marker.equals(myMapMarkerDot)) {
                			for(String userMarker : users.keySet()) {
                				if(users.get(userMarker).equals(marker)) {
                					new Caller(userMarker.trim());
                					Caller.callLabel.setBounds(0, 0, 100, 100);
                					Caller.callLabel.setText("Do you want to call this person?");
                        			break;
                				}
                			}
                		}
                	}
                	
                }
               if(e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {
            	   
            	   if(isChangingLocation) {
	            	   myMapMarkerDot.setLat(Double.parseDouble(df.format(map.getPosition(e.getPoint()).getLat())));
	            	   myMapMarkerDot.setLon(Double.parseDouble(df.format(map.getPosition(e.getPoint()).getLon())));
	            	   map.repaint();
	            	   SocketWriterThread.getOut().flush();
	            	   SocketWriterThread.getOut().println("updatelocation " + uniqueID + " " + df.format(map.getPosition(e.getPoint()).getLat()) + " " + df.format(map.getPosition(e.getPoint()).getLon()));
            	   }
        		 
               }
            }
        };
		
        addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent we) {
        		
				int exitConfirm = JOptionPane.showConfirmDialog(map, "Are you sure you want to exit the chat?", "Message", JOptionPane.YES_NO_OPTION);
				if(exitConfirm == JOptionPane.YES_NO_OPTION) {
	        		dispose();
	        		System.exit(0);
				}
        	}
        });    
        
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        add(map, BorderLayout.CENTER);
        
        setVisible(true);
        
		uniqueID = UUID.randomUUID().toString();
		PrintWriter out = SocketWriterThread.getOut();
		out.println("new " + uniqueID + " " + lat + " " + lon);
		users.put(uniqueID, myMapMarkerDot);

	}
	
    public static String getData(String url) {
        StringBuffer output = new StringBuffer();
        
        try {
		
			URL dataURL = new URL(url);
			URLConnection res = dataURL.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()));
            String line = "";

            while ((line = reader.readLine())!= null) {
                 output.append(line + "\n");
            }    
            
        } catch (Exception e) {
        	e.printStackTrace();
            return "";
        }

        return output.toString();
    }
    
    public static Boolean distance(String[] LatLon1, String[] LatLon2) {
    	int R = 6371000;
        double lat1 = Double.parseDouble(LatLon1[0]);
        double lat2 = Double.parseDouble(LatLon2[0]);
        double lon1 = Double.parseDouble(LatLon1[1]);
        double lon2 = Double.parseDouble(LatLon2[1]);
        double latDifference = lat2 - lat1;
        double lonDifference = lon2 - lon1;

        double a = Math.sin(latDifference / 2) * Math.sin(latDifference / 2) +
        		Math.cos(lat1) * Math.cos(lat2) *
                Math.sin(lonDifference / 2) * Math.sin(lonDifference / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        double d = R * c;
                        
        if(d < 1000) {
            return true;
        } else {
        	return false;
        }
        
    }
    
    public static void addMarker(String id, String[] LatLon) {
    	MapMarkerDot marker = new MapMarkerDot(Double.parseDouble(LatLon[0]), Double.parseDouble(LatLon[1]));
        map.addMapMarker(marker);
		users.put(id, marker);    
    }
    
    public static void updateMarker(String id, String[] LatLon) {
    	MapMarkerDot marker = (MapMarkerDot) users.get(id);
    	marker.setLat(Double.parseDouble(LatLon[0].trim()));
    	marker.setLon(Double.parseDouble(LatLon[1].trim()));
    	for (MapMarker userMarker : map.getMapMarkerList()) {
    		if(userMarker.equals(marker)) {
    	    	userMarker.setLat(Double.parseDouble(LatLon[0].trim()));
    	    	userMarker.setLon(Double.parseDouble(LatLon[1].trim()));   
    	    	break;
    	    }
    	}
    	map.repaint();
		users.put(id, marker);
    }
    
    public static void removeMarker(String id) {
    	MapMarker mapMarkerToDelete = users.get(id);
    	map.removeMapMarker(mapMarkerToDelete);
		users.remove(id);   
		map.repaint();
    }

}

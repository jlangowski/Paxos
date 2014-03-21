package uwt.paxos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

public class Learner {
	Integer[] locks = new Integer[100];
	ArrayList<String> ips = new ArrayList<String>();
	PrintStream out;
	public Learner() {
		try {
			out = new PrintStream(new File("LearnerLog.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject isLocked(int lock) throws JSONException {
		JSONObject response = new JSONObject();
		if (locks[lock] != null) {
			response.append("locked", true);// = "{\"locked\":true, \"clientId\":" + locks[lock]+"}";
			response.append("clientId", locks[lock]);
		} else {
			response.append("locked", false); // "{\"locked\":false}";
		}
		out.println(response);
		return response;
	}
	
	public JSONObject lock(int lock, int clientId) throws JSONException {
		JSONObject response = new JSONObject();
		if (locks[lock] != null) {
			response.append("locked", false);// = "{\"locked\":false, \"clientId\":" + locks[lock]+"}";
			response.append("clientId", locks[lock]);
		} else {
			locks[lock] = new Integer(clientId);
			response.append("locked", true); // "{\"locked\":true}";
		}
		out.println(response);
		return response;
	}
	
	public JSONObject unlock(int lock, int clientId) throws JSONException {
		JSONObject response = new JSONObject();
		if (locks[lock] != clientId) {
			response.append("locked", true);// = "{\"locked\":true, \"clientId\":" + locks[lock]+"}";
			response.append("clientId", locks[lock]);
		} else {
			locks[lock] = null;
			response.append("locked", false); // "{\"locked\":false}";
		}
		out.println(response);
		return response;
	}
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Need atleast 4 ip addresses");
			return;
		}
		String ip = "";
		try {
			 ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Learner l = new Learner();
		l.ips.addAll(Arrays.asList(args));
		l.ips.add(ip);
		System.out.println(l.ips);
		while (true) {
			try  { 
				    ServerSocket serverSocket = new ServerSocket(9003);
				    Socket clientSocket = serverSocket.accept();
				    ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
				    JSONObject request = (JSONObject) is.readObject();
				    l.out.println(request);
				    JSONObject ret = null;
				    switch (request.getString("action")) {
				    	case "isLocked":
				    		ret = l.isLocked(request.getInt("lock"));
				    		break;
				    	case "lock":
				    		ret = l.lock(request.getInt("lock"), request.getInt("clientId"));
				    		break;
				    	case "unlock":
				    		ret = l.unlock(request.getInt("lock"), request.getInt("clientId"));
				    		break;
				    	default:
				    		break;
				    		
				    }
				    ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
				    os.writeObject(ret);
				    is.close();
				    os.close();
				    clientSocket.close();
				    serverSocket.close();
				    l.out.println(ret);
				    
				    
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}

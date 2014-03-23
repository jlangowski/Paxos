package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

public class TestLearner {
	public static void main(String[] args) throws UnknownHostException, IOException, JSONException, ClassNotFoundException {
		String ip = "";
		try {
			 ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Socket s = new Socket(InetAddress.getByName(ip), 9003);
		ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
		JSONObject obj = new JSONObject();
		obj.put("action", "lock");
		obj.put("clientId", 1);
		obj.put("lock", 1);
		os.writeObject(obj.toString());
		ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		JSONObject res = new JSONObject( (String) is.readObject());
		System.out.println(res);
		s.close();
		os.close();
		is.close();
		
		
	}

}

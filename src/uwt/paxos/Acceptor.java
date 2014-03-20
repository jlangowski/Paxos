package uwt.paxos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class Acceptor {
		ArrayList<String> ips = new ArrayList<String>();
		PrintStream out;
		int proposal_id = 0;
		boolean promise_out = false;
		int lock = -1;
		public Acceptor() {
			try {
				out = new PrintStream(new File("AcceptorLog.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public JSONObject prepare(int pid, int lock) throws JSONException  {
			JSONObject response = new JSONObject();
			if (pid < proposal_id) {
				response.append("ack", false);//= "{\"ack\":false, \"proposalId\":" + proposal_id +"}";
				response.append("proposalId", proposal_id);
				
			} else {
				proposal_id = pid;
				promise_out = true;
				response.append("ack", false); // = "{\"ack\":true, \"proposalid\":" + proposal_id + ", \"lock\":" + lock + "}" ;
				response.append("proposalId", proposal_id);
				response.append("lock", lock);
			}
			out.println(response);
			return response;
		}
		
		public JSONObject propose(int pid, int lock) throws JSONException {
			JSONObject response = new JSONObject();
			if (pid < proposal_id || this.lock != lock) {
				response.append("ack", false);//= "{\"ack\":false, \"proposalId\":" + proposal_id +"}";
				response.append("proposalId", proposal_id);
			} else {
				response.append("ack", false); // = "{\"ack\":true, \"proposalid\":" + proposal_id + ", \"lock\":" + lock + "}" ;
				response.append("proposalId", proposal_id);
				response.append("lock", lock);
			}
			out.println(response);
			return response;
		}
		
		public JSONObject confirm(String action, int clientId, int pid, int lock) throws JSONException {
			JSONObject response = new JSONObject();
			if (pid < proposal_id || this.lock != lock) {
				response.append("ack", false);//= "{\"ack\":false, \"proposalId\":" + proposal_id +"}";
				response.append("proposalId", proposal_id);
			} else {
				promise_out = false;
				response.append("ack", true); // = "{\"ack\":true, \"proposalid\":" + proposal_id + ", \"lock\":" + lock + "}" ;
				response.append("proposalId", proposal_id);
				response.append("lock", lock);
				JSONObject unlock = new JSONObject();
				unlock.append("action", action);
				unlock.append("lock", lock);
				unlock.append("proposalId", proposal_id);
				unlock.append("clientId", clientId);
				for (String s: ips) {
					this.out.println("sending lock request to learner on ip" + s + " lock: " + lock);
					try {
						Socket soc = new Socket(s, 2003);
						ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
						out.writeObject(unlock);
						ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
						JSONObject res = (JSONObject) in.readObject();
						this.out.println("response from learner on ip" + s + " locked lock: " + res.getInt("lock"));
						out.close();
						soc.close();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
			
			
			Acceptor acc = new Acceptor();
			acc.ips.addAll(Arrays.asList(args));
			acc.ips.add(ip);
			System.out.println(acc.ips);
			while (true) {
				try  { 
					    ServerSocket serverSocket = new ServerSocket(2002);
					    Socket clientSocket = serverSocket.accept();
					    ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
					    JSONObject request = (JSONObject) is.readObject();
					    acc.out.println(request);
					    JSONObject ret = null;
					    switch (request.getString("action")) {
					    	case "prepare":
					    		ret = acc.prepare(request.getInt("proposalId"), request.getInt("lock"));
					    		break;
					    	case "propose":
					    		ret = acc.propose(request.getInt("proposalId"), request.getInt("lock"));
					    		break;
					    	case "confirm":
					    		ret = acc.confirm(request.getString("lockAction"), request.getInt("clientId"), request.getInt("proposalId"), request.getInt("lock"));
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
					    acc.out.println(ret);
					    
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}

}

package uwt.paxos;

import org.json.JSONException;
import org.json.JSONObject;

public class Acceptor {
		int proposal_id = 0;
		boolean promise_out = false;
		int lock = -1;
		
		public String prepare(JSONObject request) throws JSONException  {
			int pid = request.getInt("proposalId");
			String response;
			if (pid < proposal_id)
				response = "{ack:false, proposalId:" + proposal_id +"}";
			else {
				proposal_id = pid;
				promise_out = true;
				lock = request.getInt("lock");
				response = "{ack:true, proposalid:" + proposal_id + ", lock:" + lock + "}" ;
				
				
			}
			return response;
		}
		
		public String propose(JSONObject request) throws JSONException {
			int pid = request.getInt("proposalId");
			int lock = request.getInt("lock");
			String response;
			if (pid < proposal_id || this.lock != lock) {
				response = "{ack:false, proposalId:" + proposal_id +"}";
			} else {
				response = "{ack:true, proposalid:" + proposal_id + ", lock:" + lock + "}" ;
			}
			
			return response;
		}

}

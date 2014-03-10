package uwt.paxos;

public class Learner {
	Integer[] locks = new Integer[100];
	
	public String isLocked(int lock) {
		String response;
		if (locks[lock] != null) {
			response = "{\"locked\":true, \"clientId\":" + locks[lock]+"}";
		} else {
			response = "{\"locked\":false}";
		}
		return response;
	}
	
	public String lock(int lock, int clientId) {
		String response;
		if (locks[lock] != null) {
			response = "{\"locked\":false, \"clientId\":" + locks[lock]+"}";
		} else {
			locks[lock] = new Integer(clientId);
			response = "{\"locked\":true}";
		}
		return response;
	}
	
	public String unlock(int lock, int clientId) {
		String response;
		if (locks[lock] != clientId) {
			response = "{\"locked\":true, \"clientId\":" + locks[lock]+"}";
		} else {
			locks[lock] = null;
			response = "{\"locked\":false}";
		}
		return response;
	}
}

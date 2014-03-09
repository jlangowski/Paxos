package uwt.paxos;

public class Acceptor {
		int proposal_id = 0;
		boolean promise_out = false;
		int lock = -1;
		
		public boolean prepare() {
			return true;
		}
		
		public boolean propose() {
			return true;
		}

}

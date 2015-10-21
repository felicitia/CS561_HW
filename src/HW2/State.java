package HW2;

import java.util.ArrayList;
import java.util.List;

/**
 * the state is after applying the move of the node
 * @author felicitia
 *
 */
public class State {
	public String node = null;
	public int[] state1 = null;
	public int[] state2 = null;
	public int stone1;
	public int stone2;
	public int depth;
	public boolean continueMove;
	public int value;
	
	public State(){
		super();
		// TODO Auto-generated constructor stub
		continueMove = false;
	}
	public State(int N) {
		state1 = new int[N];
		state2 = new int[N];
		continueMove = false;
	}
	
}

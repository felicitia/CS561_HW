package HW2;

import java.util.ArrayList;
import java.util.Arrays;
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
	public State(int N, State state) {
		state1 = Arrays.copyOf(state.state1, N);
		state2 = Arrays.copyOf(state.state2, N);
		stone1 = state.stone1;
		stone2 = state.stone2;
		continueMove = false;
	}
	
}

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
	public State child = null;
	
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
	
	public static State copyState(int N, State state){
		State newState = new State();
		newState.continueMove = state.continueMove;
		newState.depth = state.depth;
		newState.node = state.node;
		newState.state1 = Arrays.copyOf(state.state1, N);
		newState.state2 = Arrays.copyOf(state.state2, N);
		newState.stone1 = state.stone1;
		newState.stone2 = state.stone2;
		newState.value = state.value;
		return newState;
	}
}

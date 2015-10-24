package HW2;

import java.util.Arrays;

public class ABState extends State{
	public int alpha;
	public int beta;
	
	public ABState(){
		super();
		// TODO Auto-generated constructor stub
		continueMove = false;
	}
	public ABState(int N, ABState state) {
		state1 = Arrays.copyOf(state.state1, N);
		state2 = Arrays.copyOf(state.state2, N);
		stone1 = state.stone1;
		stone2 = state.stone2;
		continueMove = false;
		alpha = state.alpha;
		beta = state.beta;
	}
}

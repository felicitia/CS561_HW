package HW2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mancala {

	static String input = "/Users/felicitia/Documents/semester_3/561/HW2/input_2.txt";
	static int taskNo;
	static int player;
	static int cutDepth;
	static PrintWriter stateWriter = null;
	static PrintWriter logWriter = null;
	static int N;
	static State initialState = null;
	static short A = 0;
	static short B = 1;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initialState = new State();
		initialState.depth = 0;
		initialState.node = "root";
		initialState.value = Integer.MIN_VALUE;
		readInput(input);
		if (taskNo != 1) {
			try {
				logWriter = new PrintWriter("traverse_log.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			stateWriter = new PrintWriter("next_state.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch (taskNo) {
		case 1:
			greedy();
			break;
		case 2:
			miniMax();
			break;
		case 3:
			alphaBeta();
			break;
		}
		if (taskNo != 1) {
			logWriter.close();
		}
		stateWriter.close();
	}

	public static void greedy() {

	}

	public static void printState(State state){
		System.out.println(state.node+","+state.depth+","+state.value);
	}
	
	public static int maxValue(State state) {
		
		if (state.depth == cutDepth) {
			if (!state.continueMove) {
				int eval = evaluation(state);
				state.value = eval;
				printState(state);
				return eval;
			}	
		}
		printState(state);
		int value = Integer.MIN_VALUE;
		if (1 == player) {
			// traverse B2, B3, ...
			boolean validMove = true;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
					validMove = false;
					continue;
				}
				validMove = true;
				State nextState = new State(N);
				nextState.node = "B" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
					nextState.value = Integer.MIN_VALUE;
				} else {
					nextState.depth = state.depth + 1;
					nextState.value = Integer.MAX_VALUE;
				}
				nextState.state1[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, B);
				if(state.continueMove){
					value = Math.max (value, maxValue(nextState));
				}else{
					value = Math.max(value, minValue(nextState));
				}
			}
			if(!validMove){
				endGame(state, B);
			}
		} else {
			// traverse A2, A3, ...
			boolean validMove = true;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
					validMove = false;
					continue;
				}
				validMove = true;
				State nextState = new State(N);
				nextState.node = "A" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
					nextState.value = Integer.MIN_VALUE;
				} else {
					nextState.depth = state.depth + 1;
					nextState.value = Integer.MAX_VALUE;
				}
				nextState.state2[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, A);
				if(state.continueMove){
					value = Math.max (value, maxValue(nextState));
				}else{
					value = Math.max(value, minValue(nextState));
				}
			}
			if(!validMove){
				endGame(state, A);
			}
		}
		state.value = value;
		printState(state);
		return value;
	}

	public static int endGame(State state, int AorB){
		int total = 0;
		if(AorB == A){
			for(int i=0; i<N; i++){
				total += state.state1[i];
			}
			state.stone1 += total;
			return evaluation(state);
		}else{
			for(int i=0; i<N; i++){
				total += state.state2[i];
			}
			state.stone2 += total;
			return evaluation(state);
		}
	}
	
	/**
	 * update the state, not include the chosen node which is already updated
	 * 
	 * @param state
	 * @param nextState
	 * @param number
	 * @param traverseIndex
	 */
	public static void updateNextState(State state, State nextState, int number,
			int traverseIndex, int AorB) {
		int idx;
		if (AorB == B) {
			for (int i = 0; i < number; i++) {
				idx = traverseIndex + i + 1;
				// check last step
				if (i == number - 1) {
					int tmpIdx = Math.floorMod(idx, (2 * (N + 1)));
					// another turn
					if (tmpIdx == N) {
						nextState.continueMove = true;
						nextState.stone1 = state.stone1 + 1;
						return;
					} else if (tmpIdx != (2 * (N + 1) - 1)) {
						// move all mancalas
						if (tmpIdx < N && state.state1[tmpIdx] == 0) {
							nextState.stone1 = state.stone1
									+ state.state2[tmpIdx] + 1;
							nextState.state1[tmpIdx] = 0;
							nextState.state2[tmpIdx] = 0;
							return;
						}
					}
				}
				if (idx > N - 1) {
					idx = Math.floorMod(idx, (2 * (N + 1)));
					if (idx == N) {
						nextState.stone1 = state.stone1 + 1;
					} else if (idx == (2 * (N + 1) - 1)) {
						nextState.stone2 = state.stone2 + 1;
					} else if (idx < N) {
						nextState.state1[idx] = state.state1[idx] + 1;
					} else {
						idx = 2 * N - idx;
						nextState.state2[idx]= state.state2[idx] + 1;
					}
				} else {
					nextState.state1[idx] = state.state1[idx] + 1;
				}
			}
		} else if (AorB == A) {
			for (int i = 0; i < number; i++) {
				idx = traverseIndex - 1 - i;
				// check last step
				if (i == number - 1) {
					int tmpIdx = Math.floorMod(idx, (2 * (N + 1)));
					// another turn
					if (tmpIdx == (2 * (N + 1) - 1)) {
						nextState.continueMove = true;
						nextState.stone2 = state.stone2 + 1;
						return;
					} else if (tmpIdx < N && state.state2[tmpIdx] == 0) {
						nextState.stone2 = state.stone2
								+ state.state1[tmpIdx] + 1;
						nextState.state1[tmpIdx] = 0;
						nextState.state2[tmpIdx] = 0;
						return;
					}
				}
				if (idx < 0) {
					idx = Math.floorMod(idx, (2 * (N + 1)));
					if (idx == N) {
						nextState.stone1 = state.stone1 + 1;
					} else if (idx == (2 * (N + 1) - 1)) {
						nextState.stone2 = state.stone2 + 1;
					} else if (idx < N) {
						nextState.state2[idx] = state.state2[idx] + 1;
					} else {
						idx = 2 * N - idx;
						nextState.state1[idx] = state.state1[idx] + 1;
					}
				} else {
					nextState.state2[idx] = state.state2[idx] + 1;
				}
			}
		} else {
			System.out.println("invalid input in updateState()..");
		}
	}

	public static int minValue(State state) {
		if (state.depth == cutDepth) {
			if (!state.continueMove) {
				int eval = evaluation(state);
				state.value = eval;
				printState(state);
				return eval;
			}
		}
		printState(state);
		int value = Integer.MAX_VALUE;
		if (2 == player) {
			// traverse B2, B3, ...
			boolean validMove = true;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
					validMove = false;
					continue;
				}
				validMove = true;
				State nextState = new State(N);
				nextState.node = "B" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
					nextState.value = Integer.MAX_VALUE;
				} else {
					nextState.depth = state.depth + 1;
					nextState.value = Integer.MIN_VALUE;
				}
				nextState.state1[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, B);
				if(state.continueMove){
					value = Math.min (value, minValue(nextState));
				}else{
					value = Math.min(value, maxValue(nextState));
				}
				if(!validMove){
					endGame(state, B);
				}
			}
		} else {
			// traverse A2, A3, ...
			boolean validMove = true;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
					validMove = false;
					continue;
				}
				validMove = true;
				State nextState = new State(N);
				nextState.node = "A" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
					nextState.value = Integer.MAX_VALUE;
				} else {
					nextState.depth = state.depth + 1;
					nextState.value = Integer.MIN_VALUE;
				}
				nextState.state2[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, A);
				if(state.continueMove){
					value = Math.min (value, minValue(nextState));
				}else{
					value = Math.min(value, maxValue(nextState));
				}
			}
			if(!validMove){
				endGame(state, A);
			}
		}
		state.value = value;
		printState(state);
		return value;
	}

	public static void miniMax() {
		maxValue(initialState);
	}

	public static void alphaBeta() {

	}

	public static int evaluation(State state) {
		if (1 == player) {
			return state.stone1 - state.stone2;
		}
		return state.stone2 - state.stone1;
	}

	public static void readInput(String input) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			taskNo = Integer.parseInt(br.readLine());
			player = Integer.parseInt(br.readLine());
			cutDepth = Integer.parseInt(br.readLine());
			String[] tmp = br.readLine().split(" ");
			N = tmp.length;
			initialState.state1 = new int[N];
			initialState.state2 = new int[N];
			for (int i=0; i<N; i++) {
				initialState.state2[i] = Integer.parseInt(tmp[i]);
			}
			tmp = br.readLine().split(" ");
			for (int i=0; i<N; i++) {
				initialState.state1[i] = Integer.parseInt(tmp[i]);
			}
			initialState.stone2 = Integer.parseInt(br.readLine());
			initialState.stone1 = Integer.parseInt(br.readLine());
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

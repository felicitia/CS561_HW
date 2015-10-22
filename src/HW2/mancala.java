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

	static String input = "/Users/felicitia/Documents/semester_3/561/HW2/input_4.txt";
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
				logWriter.println("Node,Depth,Value");
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

	public static void printState(State state) {
		String value = "" + state.value;
		if (state.value == Integer.MAX_VALUE) {
			value = "Infinity";
		}
		if (state.value == Integer.MIN_VALUE) {
			value = "-Infinity";
		}
		System.out.println(state.node + "," + state.depth + "," + value);
		logWriter.println(state.node + "," + state.depth + "," + value);
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
			int invalidMove = 0;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
					invalidMove++;
					continue;
				}
				State nextState = new State(N, state);
				nextState.node = "B" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
				} else {
					nextState.depth = state.depth + 1;
				}
				nextState.state1[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, B);
				if (nextState.continueMove) {
					nextState.value = Integer.MIN_VALUE;
					value = Math.max(value, maxValue(nextState));
				} else {
					nextState.value = Integer.MAX_VALUE;
					value = Math.max(value, minValue(nextState));
				}
				state.value = value;
				printState(state);
			}
			if (invalidMove == N) {
				return endGame(state, B);
			}
		} else {
			// traverse A2, A3, ...
			int invalidMove = 0;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
					invalidMove++;
					continue;
				}
				State nextState = new State(N, state);
				nextState.node = "A" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
				} else {
					nextState.depth = state.depth + 1;
				}
				nextState.state2[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, A);
				if (nextState.continueMove) {
					nextState.value = Integer.MIN_VALUE;
					value = Math.max(value, maxValue(nextState));
				} else {
					nextState.value = Integer.MAX_VALUE;
					value = Math.max(value, minValue(nextState));
				}
				state.value = value;
				printState(state);
			}
			if (invalidMove == N) {
				return endGame(state, A);
			}
		}
		return value;
	}

	public static int endGame(State state, int AorB) {
		int total = 0;
		if (AorB == A) {
			for (int i = 0; i < N; i++) {
				total += state.state1[i];
			}
			state.stone1 += total;
			return evaluation(state);
		} else {
			for (int i = 0; i < N; i++) {
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
	public static void updateNextState(State state, State nextState,
			int number, int traverseIndex, int AorB) {
		int idx;
		if (AorB == B) {
			for (int i = 1; i <= number; i++) {
				idx = traverseIndex + i;
				// check last step
				if (i == number) {
					int tmpIdx = Math.floorMod(idx, (2 * (N + 1)));
					// another turn
					if (tmpIdx == N) {
						nextState.continueMove = true;
						nextState.stone1++;
						return;
					} else if (tmpIdx != (2 * (N + 1) - 1)) {
						// move all mancalas
						if (tmpIdx < N && nextState.state1[tmpIdx] == 0) {
							nextState.stone1 += (nextState.state2[tmpIdx] + 1);
							nextState.state1[tmpIdx] = 0;
							nextState.state2[tmpIdx] = 0;
							return;
						}
					}
				}
				if (idx > N - 1) {
					idx = Math.floorMod(idx, (2 * (N + 1)));
					if (idx == N) {
						nextState.stone1++;
					}
					//opponent's mancala, skip 
					else if (idx == (2 * (N + 1) - 1)) {
						number++;
					} else if (idx < N) {
						nextState.state1[idx]++;
					} else {
						idx = 2 * N - idx;
						nextState.state2[idx]++;
					}
				} else {
					nextState.state1[idx]++;
				}
			}
		} else if (AorB == A) {
			for (int i = 1; i <= number; i++) {
				idx = traverseIndex - i;
				// check last step
				if (i == number) {
					int tmpIdx = Math.floorMod(idx, (2 * (N + 1)));
					// another turn
					if (tmpIdx == (2 * (N + 1) - 1)) {
						nextState.continueMove = true;
						nextState.stone2++;
						return;
					} else if (tmpIdx < N && nextState.state2[tmpIdx] == 0) {
						nextState.stone2 += (nextState.state1[tmpIdx] + 1);
						nextState.state1[tmpIdx] = 0;
						nextState.state2[tmpIdx] = 0;
						return;
					}
				}
				if (idx < 0) {
					idx = Math.floorMod(idx, (2 * (N + 1)));
					//oponent's mancala, skip
					if (idx == N) {
						number++;
					} else if (idx == (2 * (N + 1) - 1)) {
						nextState.stone2++;
					} else if (idx < N) {
						nextState.state2[idx]++;
					} else {
						idx = 2 * N - idx;
						nextState.state1[idx]++;
					}
				} else {
					nextState.state2[idx]++;
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
			int invalidMove = 0;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
					invalidMove++;
					continue;
				}
				State nextState = new State(N, state);
				nextState.node = "B" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
				} else {
					nextState.depth = state.depth + 1;
				}
				nextState.state1[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, B);
				if (nextState.continueMove) {
					nextState.value = Integer.MAX_VALUE;
					value = Math.min(value, minValue(nextState));
				} else {
					nextState.value = Integer.MIN_VALUE;
					value = Math.min(value, maxValue(nextState));
				}
				if (invalidMove == N) {
					return endGame(state, B);
				}
				state.value = value;
				printState(state);
			}
		} else {
			// traverse A2, A3, ...
			int invalidMove = 0;
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
					invalidMove++;
					continue;
				}
				State nextState = new State(N, state);
				nextState.node = "A" + (2 + traverseIndex);
				if (state.continueMove) {
					nextState.depth = state.depth;
				} else {
					nextState.depth = state.depth + 1;
				}
				nextState.state2[traverseIndex] = 0;
				updateNextState(state, nextState, number, traverseIndex, A);
				if (nextState.continueMove) {
					nextState.value = Integer.MAX_VALUE;
					value = Math.min(value, minValue(nextState));
				} else {
					nextState.value = Integer.MIN_VALUE;
					value = Math.min(value, maxValue(nextState));
				}
				state.value = value;
				printState(state);
			}
			if (invalidMove == N) {
				return endGame(state, A);
			}
		}
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
			for (int i = 0; i < N; i++) {
				initialState.state2[i] = Integer.parseInt(tmp[i]);
			}
			tmp = br.readLine().split(" ");
			for (int i = 0; i < N; i++) {
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

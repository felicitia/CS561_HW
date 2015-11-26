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

	// static String input =
	// "/Users/felicitia/Documents/semester_3/561/HW2/input_1.txt";
	static String input = "/Users/felicitia/Desktop/HW2_Test/case_p1/input11.txt";
	static int taskNo;
	static int player;
	static int cutDepth;
	static PrintWriter stateWriter = null;
	static PrintWriter logWriter = null;
	static int N;
	static State initialState = null;
	static ABState initialABState = null;
	static short A = 0;
	static short B = 1;
	static int lineNum = 0;
	static int rootMax = Integer.MIN_VALUE;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		args = new String[4];
//		args[0] = "/Users/felicitia/Desktop/HW2_Test/input/input_1.txt";
//		args[1] = ""+3;
//		args[2] = "next_input_1_3.txt";
//		args[3] = "log_input_1_3.txt";
//		taskNo = Integer.parseInt(args[1]);

		initialState = new State();
		initialABState = new ABState();
		initialState.depth = 0;
		initialABState.depth = 0;
		initialState.node = "root";
		initialABState.node = "root";
		initialState.value = Integer.MIN_VALUE;
		initialABState.value = Integer.MIN_VALUE;
		initialABState.alpha = Integer.MIN_VALUE;
		initialABState.beta = Integer.MAX_VALUE;
		readInput(input);
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
			try {
				logWriter = new PrintWriter("traverse_log.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logWriter.println("Node,Depth,Value");
			miniMax();
			break;
		case 3:
			try {
				logWriter = new PrintWriter("traverse_log.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logWriter.println("Node,Depth,Value,Alpha,Beta");
			alphaBeta();
			break;
		}
		if (taskNo != 1) {
			logWriter.close();
		}
		stateWriter.close();
//		System.out.println(args[1]+" is Done! :D");
	}

	public static void greedy() {
		cutDepth = 1;
		maxValue(initialState);
		printNextState();
	}

	public static void printABLog(ABState state) {
		lineNum++;
		String value = "" + state.value;
		String alpha = "" + state.alpha;
		String beta = "" + state.beta;
		if (state.value == Integer.MAX_VALUE) {
			value = "Infinity";
		}
		if (state.value == Integer.MIN_VALUE) {
			value = "-Infinity";
		}
		if (state.alpha == Integer.MAX_VALUE) {
			alpha = "Infinity";
		}
		if (state.alpha == Integer.MIN_VALUE) {
			alpha = "-Infinity";
		}
		if (state.beta == Integer.MAX_VALUE) {
			beta = "Infinity";
		}
		if (state.beta == Integer.MIN_VALUE) {
			beta = "-Infinity";
		}
		logWriter.println(state.node + "," + state.depth + "," + value + ","
				+ alpha + "," + beta);
	}

	public static void printMiniMaxLog(State state, String caller) {
		// ignore greedy
		if (1 == taskNo) {
			return;
		}
		lineNum++;

		String value = "" + state.value;
		if (state.value == Integer.MAX_VALUE) {
			value = "Infinity";
		}
		if (state.value == Integer.MIN_VALUE) {
			value = "-Infinity";
		}
		logWriter.println(state.node + "," + state.depth + "," + value);
//		if(lineNum >= 382 && lineNum <= 395){
//			System.out.println(state.node + "," + state.depth + "," + value + ","+state.continueMove);
//			printBoard(state, value);
//		}
	}

	public static void printBoard(State state, String value) {
		System.out.println(state.node + "," + state.depth + "," + value);
		System.out.println("continue?\t" + state.continueMove);
		System.out.println("state:");
		for (int tmp : state.state2) {
			System.out.print(tmp + "\t");
		}
		System.out.println();
		for (int tmp : state.state1) {
			System.out.print(tmp + "\t");
		}
		System.out.println();
		System.out.println("stone1 = " + state.stone1);
		System.out.println("stone2 = " + state.stone2);
	}

	public static int maxValue(State state) {
		if (whoEndGame(state).equals("A")) {
			return endGame(state, A);
		}
		if (whoEndGame(state).equals("B")) {
			return endGame(state, B);
		}
		if (state.depth == cutDepth) {
			if (!state.continueMove) {
				int eval = evaluation(state);
				state.value = eval;
				printMiniMaxLog(state, "MAX");
				// if(eval > rootMax){
				// updateNextMove(N, state, eval);
				// }
				return eval;
			}
		}
		printMiniMaxLog(state, "MAX");
		int value = Integer.MIN_VALUE;
		if (1 == player) {
			// traverse B2, B3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
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
					int tmp = maxValue(nextState);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.max(value, maxValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				} else {
					nextState.value = Integer.MAX_VALUE;
					int tmp = minValue(nextState);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.max(value, minValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				}
				state.value = value;
				printMiniMaxLog(state, "MAX");
				// if(value > rootMax){
				// updateNextMove(N, state, value);
				// }
				// if(state == initialState && rootMax < value){
				// rootMax = value;
				// }
			}
		} else {
			// traverse A2, A3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
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
					int tmp = maxValue(nextState);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.max(value, maxValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				} else {
					nextState.value = Integer.MAX_VALUE;
					int tmp = minValue(nextState);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.max(value, minValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				}
				state.value = value;
				printMiniMaxLog(state, "MAX");
				// if(value > rootMax){
				// updateNextMove(N, state, value);
				// }
				// if(state == initialState && rootMax < value){
				// rootMax = value;
				// }
			}
		}
		return value;
	}

	public static String whoEndGame(State state) {
		// check state 1
		int invalidMove = 0;
		for (int i = 0; i < N; i++) {
			if (state.state1[i] == 0) {
				invalidMove++;
			}
		}
		if (invalidMove == N) {
			return "B";
		}
		// check state 2
		invalidMove = 0;
		for (int i = 0; i < N; i++) {
			if (state.state2[i] == 0) {
				invalidMove++;
			}
		}
		if (invalidMove == N) {
			return "A";
		}
		return "None";
	}

	// public static void updateNextMove(int N, State nextState, int eval) {
	// System.out.println("rootMax = "+ rootMax);
	// System.out.println("next move? "+nextState.node+" "+nextState.depth);
	// if (1 == nextState.depth && !nextState.continueMove) {
	// System.out.println("next move: "+nextState.node+"\t"+nextState.value);
	// nextMove = State.copyState(N, nextState);
	// rootMax = eval;
	// }
	// }

	public static int endGame(State state, int AorB) {
		int total = 0;
		if (AorB == A) {
			for (int i = 0; i < N; i++) {
				total += state.state1[i];
				state.state1[i] = 0;
			}
			state.stone1 += total;
			state.value = evaluation(state);
			if (state instanceof ABState) {
				printABLog((ABState) state);
			} else if (state instanceof State) {
				printMiniMaxLog(state, "END");
			}
			return state.value;
		} else {
			for (int i = 0; i < N; i++) {
				total += state.state2[i];
				state.state2[i] = 0;
			}
			state.stone2 += total;
			state.value = evaluation(state);
			if (state instanceof ABState) {
				printABLog((ABState) state);
			} else if (state instanceof State) {
				printMiniMaxLog(state, "END");
			}
			return state.value;
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
					// opponent's mancala, skip
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
					// oponent's mancala, skip
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
		if (whoEndGame(state).equals("A")) {
			return endGame(state, A);
		}
		if (whoEndGame(state).equals("B")) {
			return endGame(state, B);
		}
		if (state.depth == cutDepth) {
			if (!state.continueMove) {
				int eval = evaluation(state);
				state.value = eval;
				printMiniMaxLog(state, "MIN");
				// if(eval > rootMax){
				// updateNextMove(N, state, eval);
				// }
				return eval;
			}
		}
		printMiniMaxLog(state, "MIN");
		int value = Integer.MAX_VALUE;
		if (2 == player) {
			// traverse B2, B3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
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
					int tmp = minValue(nextState);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.min(value, minValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				} else {
					nextState.value = Integer.MIN_VALUE;
					int tmp = maxValue(nextState);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.min(value, maxValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				}
				state.value = value;
				printMiniMaxLog(state, "MIN");
				// if(value > rootMax){
				// updateNextMove(N, state, value);
				// }
			}

		} else {
			// traverse A2, A3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
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
					int tmp = minValue(nextState);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.min(value, minValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				} else {
					nextState.value = Integer.MIN_VALUE;
					int tmp = maxValue(nextState);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
					// value = Math.min(value, maxValue(nextState));
					// if(value > rootMax){
					// updateNextMove(N, state, value);
					// }
				}
				state.value = value;
				printMiniMaxLog(state, "MIN");
				// if(value > rootMax){
				// updateNextMove(N, state, value);
				// }
			}

		}
		return value;
	}

	public static void miniMax() {
		maxValue(initialState);
		printNextState();
	}

	public static void alphaBeta() {
		maxValue(initialABState, Integer.MIN_VALUE, Integer.MAX_VALUE);
		printNextState();
	}

	public static void printNextState() {
		State nextMove = null;
		if (taskNo == 3) {
			nextMove = initialABState.child;
		} else {
			nextMove = initialState.child;
		}
		while (nextMove.depth == 1 && nextMove.continueMove) {
			nextMove = nextMove.child;
		}
		// print state 2
		for (int i = 0; i < N - 1; i++) {
			stateWriter.print(nextMove.state2[i] + " ");
		}
		stateWriter.println(nextMove.state2[N - 1]);
		// print state 1
		for (int i = 0; i < N - 1; i++) {
			stateWriter.print(nextMove.state1[i] + " ");
		}
		stateWriter.println(nextMove.state1[N - 1]);
		stateWriter.println(nextMove.stone2);
		stateWriter.println(nextMove.stone1);
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
//			br.readLine();//skip taskNo for now
			player = Integer.parseInt(br.readLine());
			cutDepth = Integer.parseInt(br.readLine());
			String[] tmp = br.readLine().split(" ");
			N = tmp.length;
			if (taskNo == 3) {
				initialABState.state1 = new int[N];
				initialABState.state2 = new int[N];
			} else {
				initialState.state1 = new int[N];
				initialState.state2 = new int[N];
			}
			for (int i = 0; i < N; i++) {
				if (taskNo == 3) {
					initialABState.state2[i] = Integer.parseInt(tmp[i]);
				} else {
					initialState.state2[i] = Integer.parseInt(tmp[i]);
				}
			}
			tmp = br.readLine().split(" ");
			for (int i = 0; i < N; i++) {
				if (taskNo == 3) {
					initialABState.state1[i] = Integer.parseInt(tmp[i]);
				} else {
					initialState.state1[i] = Integer.parseInt(tmp[i]);
				}
			}
			if (taskNo == 3) {
				initialABState.stone2 = Integer.parseInt(br.readLine());
				initialABState.stone1 = Integer.parseInt(br.readLine());
			} else {
				initialState.stone2 = Integer.parseInt(br.readLine());
				initialState.stone1 = Integer.parseInt(br.readLine());
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int maxValue(ABState state, int alpha, int beta) {
		if (whoEndGame(state).equals("A")) {
			return endGame(state, A);
		}
		if (whoEndGame(state).equals("B")) {
			return endGame(state, B);
		}
		if (state.depth == cutDepth) {
			if (!state.continueMove) {
				int eval = evaluation(state);
				state.value = eval;
				printABLog(state);
				return eval;
			}
		}
		printABLog(state);
		int value = Integer.MIN_VALUE;
		if (1 == player) {
			// traverse B2, B3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
					continue;
				}
				ABState nextState = new ABState(N, state);
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
					int tmp = maxValue(nextState, state.alpha, state.beta);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
					state.value = value;
					if (value >= state.beta) {
						printABLog(state);
						return value;
					}
					alpha = Math.max(state.alpha, value);
					state.alpha = alpha;
					printABLog(state);
				} else {
					nextState.value = Integer.MAX_VALUE;
					int tmp = minValue(nextState, state.alpha, state.beta);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
					state.value = value;
					if (value >= state.beta) {
						printABLog(state);
						return value;
					}
					alpha = Math.max(state.alpha, value);
					state.alpha = alpha;
					printABLog(state);
				}
			}
		} else {
			// traverse A2, A3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
					continue;
				}
				ABState nextState = new ABState(N, state);
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
					int tmp = maxValue(nextState, state.alpha, state.beta);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
				} else {
					nextState.value = Integer.MAX_VALUE;
					int tmp = minValue(nextState, state.alpha, state.beta);
					if (value < tmp) {
						value = tmp;
						state.child = nextState;
					}
				}
				state.value = value;
				if (value >= state.beta) {
					printABLog(state);
					return value;
				}
				alpha = Math.max(state.alpha, value);
				state.alpha = alpha;
				printABLog(state);
			}
		}
		// when knowing the max value of state
		state.beta = value;
		return value;
	}

	public static int minValue(ABState state, int alpha, int beta) {
		if (whoEndGame(state).equals("A")) {
			return endGame((ABState) state, A);
		}
		if (whoEndGame(state).equals("B")) {
			return endGame((ABState) state, B);
		}
		if (state.depth == cutDepth) {
			if (!state.continueMove) {
				int eval = evaluation(state);
				state.value = eval;
				printABLog(state);
				return eval;
			}
		}
		printABLog(state);
		int value = Integer.MAX_VALUE;
		if (2 == player) {
			// traverse B2, B3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state1[traverseIndex];
				if (0 == number) {
					continue;
				}
				ABState nextState = new ABState(N, state);
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
					int tmp = minValue(nextState, state.alpha, state.beta);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
				} else {
					nextState.value = Integer.MIN_VALUE;
					int tmp = maxValue(nextState, state.alpha, state.beta);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
				}
				state.value = value;
				if (value <= state.alpha) {
					printABLog(state);
					return value;
				}
				beta = Math.min(value, state.beta);
				state.beta = beta;
				printABLog(state);
			}
		} else {
			// traverse A2, A3, ...
			for (int traverseIndex = 0; traverseIndex < N; traverseIndex++) {
				int number = state.state2[traverseIndex];
				if (0 == number) {
					continue;
				}
				ABState nextState = new ABState(N, state);
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
					int tmp = minValue(nextState, state.alpha, state.beta);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
				} else {
					nextState.value = Integer.MIN_VALUE;
					int tmp = maxValue(nextState, state.alpha, state.beta);
					if (value > tmp) {
						value = tmp;
						state.child = nextState;
					}
				}
				state.value = value;
				if (value <= state.alpha) {
					printABLog(state);
					return value;
				}
				beta = Math.min(value, state.beta);
				state.beta = beta;
				printABLog(state);
			}
		}
		//when knowing the min value of state
		state.alpha = value;
		return value;
	}
}

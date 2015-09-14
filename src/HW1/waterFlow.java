package HW1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class waterFlow {
	private static int testNum;
	private static List<Task> taskList;
	private static String input = "/Users/felicitia/Documents/semester_3/561/HW1/sampleInput.txt";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readInput(input);
		for (Task task : taskList) {
			if (task.getAlgorithm().equals("BFS")) {
				BFS(task);
			} else if (task.getAlgorithm().equals("DFS")) {
				DFS(task);
			} else if (task.getAlgorithm().equals("UCS")) {
				UCS(task);
			} else {
				System.out.println("task invalid");
			}
		}
	}

	public static void DFS(Task task) {
		Node node = new Node();
		node.setState(task.getSource());
		recursiveDFS(node, task);
	}

	public static void recursiveDFS(Node node, Task task) {
		if (task.getDestList().contains(node.getState())) {
			System.out.println(node.getState());
			System.out.println((node.getCost() + task.getStartTime()) % 24);
			System.out.println();
			return;
		}
		List<String> children = new ArrayList<String>();
		for (Pipe pipe : task.getPipeList()) {
			if (pipe.getStart().equals(node.getState())) {
				children.add(pipe.getEnd());
			}
		}
		Collections.sort(children);
		for (String state : children) {
			Node child = new Node();
			child.setState(state);
			child.setCost(node.getCost() + 1);
			recursiveDFS(child, task);
		}
	}

	public static void UCS(Task task) {
		UCSNode node = new UCSNode();
		node.setState(task.getSource());
		List<String> destList = task.getDestList();
		List<Pipe> pipeList = task.getPipeList();
		Comparator<UCSNode> costComparator = new CostComparator();
		PriorityQueue<UCSNode> frontier = new PriorityQueue<UCSNode>(
				costComparator);
		PriorityQueue<UCSNode> explored = new PriorityQueue<>(costComparator);
		frontier.add(node);
		while (!frontier.isEmpty()) {
			node = frontier.poll();
			if (destList.contains(node.getState())) {
				System.out.println(node.getState());
				System.out.println((task.getStartTime() + node.getCost()) % 24);
				System.out.println();
				return;
			}
			explored.add(node);
			for (Pipe pipe : pipeList) {
				if (pipe.getStart().equals(node.getState())) {
					UCSNode child = new UCSNode();
					child.setState(pipe.getEnd());
					child.setCost(pipe.getLength() + node.getCost());
					child.setStartTimes(pipe.getStartTimes());
					child.setEndTimes(pipe.getEndTimes());
					child.setOffNum(pipe.getOffNum());
					if (!(containState(frontier, child.getState()) || containState(
							explored, child.getState()))) {
						if (!checkOff(child,
								task.getStartTime() + node.getCost())) {
							frontier.add(child);
						}
					} else if (!checkOff(child,
							task.getStartTime() + node.getCost())) {
						for (UCSNode tmp : frontier) {
							if (tmp.getState().equals(child.getState())
									&& tmp.getCost() > child.getCost()) {
								frontier.remove(tmp);
								frontier.add(child);
							}
						}
					}
				}
			}
		}
		System.out.println("None");
		System.out.println();
		return;
	}

	public static void BFS(Task task) {
		Node node = new Node();
		node.setState(task.getSource());
		List<String> destList = task.getDestList();
		List<Pipe> pipeList = task.getPipeList();
		if (destList.contains(node.getState())) {
			System.out.println(node.getState());
			System.out.println(task.getStartTime() % 24);
			System.out.println();
			return;
		}
		LinkedList<Node> frontier = new LinkedList<Node>();
		LinkedList<Node> explored = new LinkedList<Node>();
		frontier.add(node);
		List<String> children = new ArrayList<String>();
		while (!frontier.isEmpty()) {
			node = frontier.pop();
			explored.add(node);
			for (Pipe pipe : pipeList) {
				if (pipe.getStart().equals(node.getState())) {
					children.add(pipe.getEnd());
				}
			}
			Collections.sort(children); // uppercase will be before lowercase
			for (String child : children) {
				if (!(containState(frontier, child) || containState(explored,
						child))) {
					if (destList.contains(child)) {
						System.out.println(child);
						System.out.println((task.getStartTime()
								+ node.getCost() + 1) % 24);
						System.out.println();
						return;
					}
					Node currentNode = new Node();
					currentNode.setState(child);
					currentNode.setCost(node.getCost() + 1);
					frontier.addLast(currentNode);
				}
			}
			children.clear();
		}
		System.out.println("None");
		System.out.println();
		return;
	}

	/**
	 * check if at the time, the pipe is off or not
	 * 
	 * @param child
	 * @param time
	 * @return true if off
	 */
	public static boolean checkOff(UCSNode child, int time) {
		time %= 24;
		for (int i = 0; i < child.getOffNum(); i++) {
			if (child.getStartTimes()[i] <= time
					&& time <= child.getEndTimes()[i]) {
				return true;
			}
		}
		return false;
	}

	public static boolean containState(LinkedList<Node> list, String state) {
		for (Node node : list) {
			if (node.getState().equals(state)) {
				return true;
			}
		}
		return false;
	}

	public static boolean containState(PriorityQueue<UCSNode> list, String state) {
		for (Node node : list) {
			if (node.getState().equals(state)) {
				return true;
			}
		}
		return false;
	}

	public static void readInput(String input) {
		taskList = new ArrayList<Task>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line = br.readLine();
			testNum = Integer.parseInt(line);
			for (int i = 0; i < testNum; i++) {
				taskList.add(readTask(br, line));
				br.readLine();// ignore the empty line between different tasks
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Task readTask(BufferedReader br, String line) {
		Task task = new Task();
		try {
			task.setAlgorithm(br.readLine());
			task.setSource(br.readLine());
			line = br.readLine(); // destinations
			List<String> destList = new ArrayList<String>();
			String[] destTokens = line.split(" ");
			for (String dest : destTokens) {
				destList.add(dest);
			}
			task.setDestList(destList);
			br.readLine(); // ignore middle nodes
			int pipeNum = Integer.parseInt(br.readLine());
			task.setPipeNum(pipeNum);
			for (int i = 0; i < pipeNum; i++) {
				Pipe pipe = new Pipe();
				String[] pipeTokens = br.readLine().split(" ");
				pipe.setStart(pipeTokens[0]);
				pipe.setEnd(pipeTokens[1]);
				pipe.setLength(Integer.parseInt(pipeTokens[2]));
				int offNum = Integer.parseInt(pipeTokens[3]);
				pipe.setOffNum(offNum);
				int[] startTimes = new int[offNum];
				int[] endTimes = new int[offNum];
				for (int j = 0; j < offNum; j++) {
					String[] offTokens = pipeTokens[4 + j].split("-");
					int start = Integer.parseInt(offTokens[0]);
					int end = Integer.parseInt(offTokens[1]);
					startTimes[j] = start;
					endTimes[j] = end;
				}
				pipe.setStartTimes(startTimes);
				pipe.setEndTimes(endTimes);
				task.addPipe(pipe);
			}
			int startTime = Integer.parseInt(br.readLine());
			task.setStartTime(startTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}
}

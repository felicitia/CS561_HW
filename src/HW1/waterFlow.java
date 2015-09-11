package HW1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class waterFlow {
	private static int testNum;
	private static List<Task> taskList;
	private static String input = "/Users/felicitia/Documents/semester_3/561/HW1/sampleInput.txt";



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readInput(input);
		for(Task task: taskList){
			if(task.getAlgorithm().equals("BFS")){
				BFS(task);
			}
		}
	}

	public static void BFS(Task task) {
		Node node = new Node();
		node.setState(task.getSource());
		List<String> destList = task.getDestList();
		List<Pipe> pipeList = task.getPipeList();
		if (destList.contains(node.getState())) {
			System.out.println(node.getState());
			System.out.println(task.getStartTime());
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
				if (!(frontier.contains(child) || explored.contains(child))) {
					if (destList.contains(child)) {
						System.out.println(child);
						System.out.println(task.getStartTime()+ node.getCost() + 1);
						return;
					}
					Node currentNode = new Node();
					currentNode.setState(child);
					currentNode.setCost(node.getCost()+1);
					frontier.addLast(currentNode);
				}
			}
		}
		System.out.println("None");
		return;
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
					int start = (Integer.parseInt(offTokens[0])) % 24;
					int end = (Integer.parseInt(offTokens[1])) % 24;
					startTimes[j] = start;
					endTimes[j] = end;
				}
				pipe.setStartTimes(startTimes);
				pipe.setEndTimes(endTimes);
				task.addPipe(pipe);
			}
			int startTime = (Integer.parseInt(br.readLine())) % 24;
			task.setStartTime(startTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return task;
	}
}

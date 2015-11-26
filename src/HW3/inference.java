package HW3;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class inference {

	private static String input = "/Users/felicitia/Documents/semester_3/561/HW3/input_1.txt";
	private static List<Atom> queryList = null;
	private static Map<String, ArrayList<Atom>> factMap = null;
	private static Map<String, ArrayList<Rule>> ruleMap = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		queryList = new ArrayList<Atom>();
		factMap = new HashMap<String, ArrayList<Atom>>();
		ruleMap = new HashMap<String, ArrayList<Rule>>();
		readInput(input);
		printMap(factMap);
	}

	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
//	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	public static void readInput(String input) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(input));
			String line = br.readLine();
			int queryNum = Integer.parseInt(line);
			for (int i = 0; i < queryNum; i++) {
				queryList.add(string2Atom(br.readLine()));
			}
			int ruleNum = Integer.parseInt(br.readLine());
			for (int i = 0; i < ruleNum; i++) {
				readKB(br.readLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void readKB(String line) {
		String[] tokens = line.split(" => ");
		if (1 == tokens.length) {
			readFact(line);
		} else {
			readRule(tokens);
		}
	}

	public static void readFact(String line) {
		Atom atom = string2Atom(line);
		String predicate = atom.getPredicate();
		if(factMap.containsKey(predicate)){
			factMap.get(predicate).add(atom);
		}else{
			ArrayList<Atom> atomList = new ArrayList<Atom>();
			atomList.add(atom);
			factMap.put(predicate, atomList);
		}
	}

	public static void readRule(String[] tokens) {
		Rule rule = new Rule();
		rule.setRh(string2Atom(tokens[1]));
		String[] lhs = tokens[0].split(" \\^ ");
		for(String lh: lhs){
			rule.addLh(string2Atom(lh));
		}
		String predicate = rule.getRh().getPredicate();
		if(ruleMap.containsKey(predicate)){
			ruleMap.get(predicate).add(rule);
		}else{
			ArrayList<Rule> ruleList = new ArrayList<Rule>();
			ruleList.add(rule);
			ruleMap.put(predicate, ruleList);
		}
	}

	
	public static Atom string2Atom(String line) {
		// don't need last char, which is ')'
		line = line.substring(0, line.length() - 1);
		boolean bool = true;
		if (line.charAt(0) == '~') {
			bool = false;
			// don't need first char, which is '~'
			line = line.substring(1);
		}
		String[] bracketTokens = line.split("\\(");
		String predicate = bracketTokens[0];
		String[] commaTokens = bracketTokens[1].split(",");
		return new Atom(bool, predicate, Arrays.asList(commaTokens));
	}
}

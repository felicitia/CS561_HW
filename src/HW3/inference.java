package HW3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class inference {

	private static String input = "/Users/felicitia/Documents/semester_3/561/HW3/input.txt";
	private static ArrayList<Atom> queryList = null;
	private static HashMap<String, ArrayList<Atom>> factMap = null;
	private static HashMap<String, ArrayList<Rule>> ruleMap = null;
	private static final short ATOM = 0;
	private static final short VAR = 1;
	private static final short LIST = 2;
	private static final short CONST = 3;
	private static int nameCount = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		queryList = new ArrayList<Atom>();
		factMap = new HashMap<String, ArrayList<Atom>>();
		ruleMap = new HashMap<String, ArrayList<Rule>>();
		readInput(input);
		for (Atom query : queryList) {
			System.out.println(askKB(query));
		}
	}

	public static boolean askKB(Atom query) {
		HashMap<String, String> theta = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> results = BC_OR(query, theta);
		if(results == null){
			return false;
		}
		for(HashMap<String, String> result: results){
			if(result == null){
				return false;
			}
		}
		return true;
	}

	public static HashMap<String, String> unify(String x, String y,
			HashMap<String, String> theta) {
		if (theta == null) {
			return null;
		} else if (x.equals(y)) {
			return theta;
		} else if (typeOfString(x) == VAR) {
			return unifyVar(x, y, theta);
		} else if (typeOfString(y) == VAR) {
			return unifyVar(y, x, theta);
		} else if (typeOfString(x) == ATOM && typeOfString(y) == ATOM) {
			Atom atomX = string2Atom(x);
			Atom atomY = string2Atom(y);
			return unify(list2String(atomX.getArgs()),
					list2String(atomY.getArgs()),
					unify(atomX.getPredicate(), atomY.getPredicate(), theta));
		} else if (typeOfString(x) == LIST && typeOfString(y) == LIST) {
			return unify(getRestList(x), getRestList(y),
					unify(getFirstList(x), getFirstList(y), theta));
		}
		return null;
	}

	// return "a" in "a,b,c"
	public static String getFirstList(String list) {
		String[] commaTokens = list.split(",");
		return commaTokens[0];
	}

	// return "b,c" in "a,b,c"
	public static String getRestList(String list) {
		String[] commaTokens = list.split(",");
		StringBuilder str = new StringBuilder();
		for (int i = 1; i < commaTokens.length - 1; i++) {
			str.append(commaTokens[i] + ",");
		}
		str.append(commaTokens[commaTokens.length - 1]);
		return str.toString();
	}

	public static HashMap<String, String> unifyVar(String var, String x,
			HashMap<String, String> theta) {
		HashMap<String, String> newTheta = new HashMap<String, String>(theta);
		if (newTheta.containsKey(var)) {
			return unify(newTheta.get(var), x, newTheta);
		} else if (newTheta.containsKey(x)) {
			return unify(var, newTheta.get(x), newTheta);
		} else if (occurCheck(var, x, newTheta)) {
			return null;
		}
		newTheta.put(var, x);
		return newTheta;
	}

	public static String list2String(ArrayList<String> list) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < list.size() - 1; i++) {
			str.append(list.get(i) + ",");
		}
		str.append(list.get(list.size() - 1));
		return str.toString();
	}

	public static boolean occurCheck(String var, String x,
			HashMap<String, String> theta) {
		if (var.equals(x)) {
			return true;
		}
		// var: z, theta: {x/z}
		else if (theta.containsKey(x)) {
			return occurCheck(var, theta.get(x), theta);
		} else if (typeOfString(x) == ATOM) {
			Atom atom = string2Atom(x);
			for (String arg : atom.getArgs()) {
				if (occurCheck(var, arg, theta)) {
					return true;
				}
			}
		}
		return false;
	}

	public static short typeOfString(String x) {
		String[] bracketTokens = x.split("\\(");
		if (bracketTokens.length == 2) {
			return ATOM;
		}
		String[] commaTokens = x.split(",");
		if (commaTokens.length > 1) {
			return LIST;
		}
		if (Character.isUpperCase(x.charAt(0))) {
			return CONST;
		}
		if (Character.isLowerCase(x.charAt(0))) {
			return VAR;
		}
		return -1;
	}

	public static ArrayList<HashMap<String, String>> BC_OR(Atom goal,
			HashMap<String, String> theta) {
		ArrayList<HashMap<String, String>> thetas = new ArrayList<HashMap<String, String>>();
		ArrayList<Atom> facts = factMap.get(goal.getPredicate());
		ArrayList<Rule> rules = ruleMap.get(goal.getPredicate());
		if(facts!=null){
			for (Atom fact : facts) {
				// all args are constant, no need to standardize
				HashMap<String, String> tmp = unify(atom2String(fact), atom2String(goal), theta);
					thetas.add(tmp);
			}
		}
		if(rules!=null){
			for (Rule rule : rules) {
				Rule stanRule = standardize(rule, theta);
				HashMap<String, String> unified = unify(atom2String(stanRule.getRh()), atom2String(goal), theta);
				ArrayList<HashMap<String, String>> thetaAnds = BC_AND(stanRule.getLhs(), unified);
				if(thetaAnds == null){
					thetas.add(null);
				}else{
					for(HashMap<String, String> thetaAnd: thetaAnds){
						thetas.add(thetaAnd);
					}
				}
			}
		}		
		return thetas;
	}

	public static ArrayList<HashMap<String, String>> BC_AND(
			ArrayList<Atom> goals, HashMap<String, String> theta) {
		if (null == theta) {
			return null;
		}
		ArrayList<HashMap<String, String>> thetas = new ArrayList<HashMap<String, String>>();
		if (0 == goals.size()) {
			thetas.add(theta);
		} else {
			Atom first = goals.get(0);
			ArrayList<Atom> rest = new ArrayList<Atom>();
			if (goals.size() > 1) {
				rest = getRestGoals(goals);
			}
			ArrayList<HashMap<String, String>> thetaOrs = BC_OR(
					substitute(first, theta), theta);
			if(thetaOrs == null){
				return null;
			}else{
				for (HashMap<String, String> thetaOr : thetaOrs) {
					ArrayList<HashMap<String, String>> thetaAnds = BC_AND(rest,
							thetaOr);
					if(thetaAnds == null){
						thetas.add(null);
					}else{
						for (HashMap<String, String> thetaAnd : thetaAnds) {
							thetas.add(thetaAnd);
						}
					}	
				}
			}
		}
		return thetas;
	}

	public static ArrayList<Atom> getRestGoals(ArrayList<Atom> goals){
		ArrayList<Atom> rest = new ArrayList<Atom>();
		for(int i=1; i<goals.size(); i++){
			rest.add(goals.get(i));
		}
		return rest;
	}
	
	public static String atom2String(Atom atom){
		StringBuilder str = new StringBuilder();
		if(!atom.getBool()){
			str.append("~");
		}
		str.append(atom.getPredicate());
		str.append("(");
		str.append(list2String(atom.getArgs()));
		str.append(")");
		return str.toString();
	}
	
	public static Rule standardize(Rule rule, HashMap<String, String> theta) {
		HashMap<String, String> nameMap = new HashMap<String, String>();
		Atom rh = rule.getRh();
		ArrayList<Atom> lhs = rule.getLhs();
		for (Atom lh : lhs) {
			for (String arg : lh.getArgs()) {
				if (theta.containsKey(arg) || theta.containsValue(arg)) {
					if (!nameMap.containsKey(arg)) {
						nameMap.put(arg, "x" + (nameCount++));
					}
				}
			}
		}
		for (String arg : rh.getArgs()) {
			if (theta.containsKey(arg) || theta.containsValue(arg)) {
				if (!nameMap.containsKey(arg)) {
					nameMap.put(arg, "x" + (nameCount++));
				}
			}
		}
		if (nameMap.isEmpty()) {
			return rule;
		} else {
			Rule stanRule = new Rule(rule);
			Iterator it = nameMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				stanRule.changeArgName(pair.getKey().toString(), pair
						.getValue().toString());
				it.remove(); // avoids a ConcurrentModificationException
			}
			return stanRule;
		}
	}

	public static Atom substitute(Atom atom, HashMap<String, String> theta) {
		Atom substAtom = new Atom(atom);
		for (int i = 0; i < substAtom.getArgs().size(); i++) {
			if (theta.containsKey(substAtom.getArg(i))) {
				substAtom.setArg(i, theta.get(substAtom.getArg(i)));
			}
		}
		return substAtom;
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
		if (factMap.containsKey(predicate)) {
			factMap.get(predicate).add(atom);
		} else {
			ArrayList<Atom> atomList = new ArrayList<Atom>();
			atomList.add(atom);
			factMap.put(predicate, atomList);
		}
	}

	public static void readRule(String[] tokens) {
		Rule rule = new Rule();
		rule.setRh(string2Atom(tokens[1]));
		String[] lhs = tokens[0].split(" \\^ ");
		for (String lh : lhs) {
			rule.addLh(string2Atom(lh));
		}
		String predicate = rule.getRh().getPredicate();
		if (ruleMap.containsKey(predicate)) {
			ruleMap.get(predicate).add(rule);
		} else {
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
		ArrayList<String> args = new ArrayList<String>(
				Arrays.asList(commaTokens));
		return new Atom(bool, predicate, args);
	}
}

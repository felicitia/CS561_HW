package HW3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class inference {

	private static String input = "/Users/felicitia/Documents/semester_3/561/HW3/OliverTests/Input5.txt";
	private static  ArrayList<Atom> queryList = null;
	private static  HashMap<String, ArrayList<Atom>> factMap = null;
	private static  HashMap<String, ArrayList<Rule>> ruleMap = null;
	private static final short ATOM = 0;
	private static final short VAR = 1;
	private static final short LIST = 2;
	private static final short CONST = 3;
	private static int nameCount = 0;
	private static PrintWriter writer = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			writer = new PrintWriter("output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		queryList = new ArrayList<Atom>();
		factMap = new HashMap<String, ArrayList<Atom>>();
		ruleMap = new HashMap<String, ArrayList<Rule>>();
		HashMap<String, String> theta = new HashMap<String, String>();
		ArrayList<Atom> visitedGoals = new ArrayList<Atom>();
		readInput(input);
		boolean answer;
//		HashMap<String, ArrayList<Rule>> ruleMapCopy = new HashMap<String, ArrayList<Rule>>(ruleMap);
		for (int i = 0; i < queryList.size(); i++) {
//			ruleMap = new HashMap<String, ArrayList<Rule>>(ruleMapCopy);
			Atom query = queryList.get(i);
			answer = askKB(query, theta, visitedGoals);
			if (answer) {
				System.out.println("TRUE");
				writer.println("TRUE");
			} else {
				System.out.println("FALSE");
				writer.println("FALSE");
			}
			theta.clear();
			visitedGoals.clear();
			nameCount = 0;
		}
		writer.close();
	}

	public static boolean askKB(Atom query, HashMap<String, String> theta, ArrayList<Atom> visitedGoals) {
		ArrayList<HashMap<String, String>> results = BC_OR(query, theta,
				visitedGoals);
		if (results == null || results.isEmpty()) {
			return false;
		}
		for (HashMap<String, String> result : results) {
			if (result != null) {
				return true;
			}
		}
		return false;
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

	public static ArrayList<HashMap<String, String>> BC_OR(final Atom goal,
			HashMap<String, String> theta, ArrayList<Atom> visitedGoals) {
		if (visitedGoals.contains(goal)) {
			return null;
		}
		visitedGoals.add(goal);
		ArrayList<HashMap<String, String>> thetas = new ArrayList<HashMap<String, String>>();
		ArrayList<Atom> facts = factMap.get(goal.getPredicate());
		ArrayList<Rule> rules = ruleMap.get(goal.getPredicate());
		if (facts != null) {
			for (Atom fact : facts) {
				// all args are constant, no need to standardize
				if(fact.equals(goal)){
					//if goal == fact, then no need to look at rules
					thetas.add(theta);
					return thetas;
				}
				HashMap<String, String> tmp = unify(atom2String(fact),
						atom2String(goal), theta);
				if (tmp == null) {
					continue;
				}
				thetas.add(tmp);
			}
		}
		if (rules != null) {
			for (Rule rule : rules) {
				Rule stanRule = standardize(rule, theta, goal);
				HashMap<String, String> unified = unify(
						atom2String(stanRule.getRh()), atom2String(goal), theta);
				if (unified == null) {
					continue;
				}
				ArrayList<HashMap<String, String>> thetaAnds = BC_AND(
						stanRule.getLhs(), unified, new ArrayList<Atom>(
								visitedGoals));
				if (thetaAnds == null) {
					continue;
				} else {
					for (HashMap<String, String> thetaAnd : thetaAnds) {
						if (thetaAnd != null) {
							thetas.add(thetaAnd);
						}
					}
				}
			}
		}
		if (thetas.isEmpty()) {
			return null;
		}
		return thetas;
	}

	public static ArrayList<HashMap<String, String>> BC_AND(final
			ArrayList<Atom> goals, HashMap<String, String> theta,
			ArrayList<Atom> visitedGoals) {
		if (theta == null) {
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
					substitute(first, theta), theta, new ArrayList<Atom>(
							visitedGoals));
			if (thetaOrs == null) {
				return null;
			} else {
				for (HashMap<String, String> thetaOr : thetaOrs) {
					if (thetaOr == null) {
						continue;
					}
					ArrayList<HashMap<String, String>> thetaAnds = BC_AND(rest,
							thetaOr, new ArrayList<Atom>(visitedGoals));
					if (thetaAnds == null) {
						continue;
					} else {
						for (HashMap<String, String> thetaAnd : thetaAnds) {
							if(thetaAnd != null){
								thetas.add(thetaAnd);
							}
						}
					}
				}
			}
		}
		if (thetas.isEmpty()) {
			return null;
		}
		return thetas;
	}

	public static ArrayList<Atom> getRestGoals(final ArrayList<Atom> goals) {
		ArrayList<Atom> rest = new ArrayList<Atom>();
		for (int i = 1; i < goals.size(); i++) {
			rest.add(goals.get(i));
		}
		return rest;
	}

	public static String atom2String(final Atom atom) {
		StringBuilder str = new StringBuilder();
		str.append(atom.getPredicate());
		str.append("(");
		str.append(list2String(atom.getArgs()));
		str.append(")");
		return str.toString();
	}

	public static Rule standardize(final Rule rule, HashMap<String, String> theta,
			final Atom goal) {
		HashMap<String, String> nameMap = new HashMap<String, String>();
		Atom rh = rule.getRh();
		ArrayList<Atom> lhs = rule.getLhs();
		for (Atom lh : lhs) {
			for (String arg : lh.getArgs()) {
				if(typeOfString(arg)==VAR){
					if (theta.containsKey(arg) || theta.containsValue(arg)
							|| goal.getArgs().contains(arg)) {
						if (!nameMap.containsKey(arg)) {
							nameMap.put(arg, "x" + (nameCount++));
						}
					}
				}
			}
		}
		for (String arg : rh.getArgs()) {
			if(typeOfString(arg) == VAR){
				if (theta.containsKey(arg) || theta.containsValue(arg)
						|| goal.getArgs().contains(arg)) {
					if (!nameMap.containsKey(arg)) {
						nameMap.put(arg, "x" + (nameCount++));
					}
				}
			}
		}
		if (nameMap.isEmpty()) {
			return rule;
		} else {
			Rule stanRule = rule.copyRule();
			Iterator it = nameMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				stanRule.changeArgName(pair.getKey().toString(), pair
						.getValue().toString());

			}
			return stanRule;
		}
	}

	public static Atom substitute(final Atom atom, HashMap<String, String> theta) {
		Atom substAtom = atom.copyAtom();
		for (int i = 0; i < substAtom.getArgs().size(); i++) {
			if(typeOfString(substAtom.getArg(i)) == VAR){
				if (theta.containsKey(substAtom.getArg(i))) {
					substAtom.setArg(i, theta.get(substAtom.getArg(i)));
				}
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
		String[] bracketTokens = line.split("\\(");
		String predicate = bracketTokens[0];
		String[] commaTokens = bracketTokens[1].split(",");
		ArrayList<String> args = new ArrayList<String>(
				Arrays.asList(commaTokens));
		return new Atom(predicate, args);
	}
}

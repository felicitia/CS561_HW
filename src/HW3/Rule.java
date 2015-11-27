package HW3;
import java.util.ArrayList;


public class Rule {
	private Atom rh;
	private ArrayList<Atom> lhs = null;
	public Rule() {
		super();
		lhs = new ArrayList<Atom>();
	}
	
	public Rule(Rule newRule){
		super();
		rh = new Atom(newRule.getRh());
		lhs = new ArrayList<Atom>(newRule.getLhs());
	}
	
	public void setRh(Atom atom){
		rh = atom;
	}
	
	public void addLh(Atom atom){
		lhs.add(atom);
	}

	public Atom getRh() {
		return rh;
	}
	public ArrayList<Atom> getLhs(){
		return lhs;
	}
	
	public void changeArgName(String before, String after){
		ArrayList<String> args = rh.getArgs();
		for(int i=0; i<args.size(); i++){
			if(args.get(i).equals(before)){
				args.set(i, after);
			}
		}
		for(Atom lh: lhs){
			args = lh.getArgs();
			for(int i=0; i<args.size(); i++){
				if(args.get(i).equals(before)){
					args.set(i, after);
				}
			}
		}
	}
}

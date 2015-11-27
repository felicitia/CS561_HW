package HW3;
import java.util.ArrayList;


public class Atom {
	private String predicate = null;
	private ArrayList<String> args = null;
	
	public Atom(Atom newAtom){
		super();
		this.predicate = newAtom.getPredicate();
		this.args = new ArrayList<String>(newAtom.getArgs());
	}
	public Atom(String predicate, ArrayList<String> args) {
		super();
		this.predicate = predicate;
		this.args = new ArrayList<String>(args);
	}

	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public ArrayList<String> getArgs() {
		return args;
	}
	public void setArg(int idx, String value){
		args.set(idx, value);
	}
	public String getArg(int idx){
		return args.get(idx);
	}
}

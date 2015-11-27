package HW3;
import java.util.ArrayList;


public class Atom {
	private boolean bool;
	private String predicate = null;
	private ArrayList<String> args = null;
	
	public Atom(Atom newAtom){
		super();
		this.bool = newAtom.getBool();
		this.predicate = newAtom.getPredicate();
		this.args = new ArrayList<String>(newAtom.getArgs());
	}
	public Atom(boolean bool, String predicate, ArrayList<String> args) {
		super();
		this.bool = bool;
		this.predicate = predicate;
		this.args = new ArrayList<String>(args);
	}
	public boolean getBool() {
		return bool;
	}
	public void setBool(boolean bool) {
		this.bool = bool;
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

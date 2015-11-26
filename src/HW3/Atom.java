package HW3;
import java.util.ArrayList;
import java.util.List;


public class Atom {
	private boolean bool;
	private String predicate = null;
	private List<String> args = null;
	public Atom(boolean bool, String predicate, List<String> args) {
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
	public List<String> getArgs() {
		return args;
	}

}

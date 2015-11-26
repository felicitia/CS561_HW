package HW3;
import java.util.ArrayList;
import java.util.List;


public class Rule {
	private Atom rh;
	private List<Atom> lhs = null;
	public Rule() {
		super();
		lhs = new ArrayList<Atom>();
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
	
}

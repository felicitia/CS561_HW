package HW3;
import java.util.ArrayList;


public class Atom {
	private String predicate = null;
	private ArrayList<String> args = null;
	
	public Atom(){
		super();
		args = new ArrayList<String>();
	}
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

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(this == obj){
			return true;
		}
		if(! (obj instanceof Atom)){
			return false;
		}
		Atom that = (Atom)obj;
		if(!this.predicate.equals(that.getPredicate())){
			return false;
		}
		for(int i=0; i<this.args.size(); i++){
			if(!this.args.get(i).equals(that.getArg(i))){
				return false;
			}
		}
		return true;
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
	public Atom copyAtom(){
		Atom newAtom = new Atom();
		newAtom.setPredicate(this.getPredicate());
		for(int i=0; i<this.args.size(); i++){
			newAtom.getArgs().add(this.getArg(i));
		}
		return newAtom;
	}
}

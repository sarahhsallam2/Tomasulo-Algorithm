public class RegFileSlot {
	String name="";
	String Q="";
	double value;
	
	
	public RegFileSlot(String name, String q, double value) {
		
		this.name = name;
		Q = q;
		this.value = value;
		
	}
	
public RegFileSlot() {
		
		this.name = "";
		Q = "";
		this.value = 0;
		
	}
	
	
public String toString() {
		return " Name : "+ this.name
				+ " Q : "+this.Q 
				+" Value : "+this.value;
		       
	}
	

}

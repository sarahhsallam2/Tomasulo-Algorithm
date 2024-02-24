
public class StoreBuffer {
	int timer ;
	int busy;
	int address;
	double V;
	String Q="";
	String tag;
	RunningStatus status;
	int index; //Instruction index in simulator queue
	
	public StoreBuffer(int timer, int busy, int address, double v, String q,String tag) {
		this.timer=timer ;
		this.busy = busy;
		this.address = address;
		V = v;
		Q = q;
		this.tag=tag;
		this.status=RunningStatus.NotRunning;
		
	}
	
public StoreBuffer() {
		this.timer=0;
		this.busy = 0;
		this.address = 0;
		V = 0;
		Q = "";
		this.tag="";
		this.status=RunningStatus.NotRunning;
		this.index=0;
	}
	
	public String toString() {
		return  "Timer: "+ this.timer
				+" tag : "+ this.tag
				+" busy: "+ this.busy
				+" address: "+this.address
				+" Q:"+this.Q
				+" V:"+this.V
		        +" status: "+this.status;
	}
	
	

}

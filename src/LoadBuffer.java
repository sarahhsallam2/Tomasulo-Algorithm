
public class LoadBuffer {
	int timer ;
	int busy;
	int address;
	String tag;
	RunningStatus status;
	int index; //Instruction index in simulator queue
	
	public LoadBuffer(int timer ,int busy, int address,String tag) {
		this.timer= timer ;
		this.busy = busy;
		this.address = address;
		this.tag=tag;
		this.status=RunningStatus.NotRunning;
		
		
	}
	
	public LoadBuffer () {
		this.timer=0;
		this.busy = 0;
		this.address = 0;
		this.tag="";
		this.status=RunningStatus.NotRunning;
		this.index=0;
	}
	
	public String toString() {
		return  "Timer: "+ this.timer
				+", busy: "+ this.busy
				+ ", address: "+this.address 
				+ ", tag: " 	+ this.tag
		        +", status : "+this.status;
	}

}

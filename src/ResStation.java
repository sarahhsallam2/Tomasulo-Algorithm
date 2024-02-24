
public class ResStation {
	int timer;
	String tag;
	int busy;
	String op;
	double Vj;
	double Vk;
	String Qj;
	String Qk;
	RunningStatus status;
	int index; //Instruction index in simulator queue
	
	public ResStation(int timer, String tag, int busy, String op, double Vj, double Vk, String Qj, String Qk){
		this.timer=timer;
		this.tag=tag;
		this.busy= busy;
		this.op=op;
		this.Vj= Vj;
		this.Vk=Vk;
		this.Qj=Qj;
		this.Qk=Qk;
		this.status=RunningStatus.NotRunning;
	}
	
	public ResStation() {
		this.timer=0;
		this.tag="";
		this.busy= 0;
		this.op="";
		this.Vj=0;
		this.Vk=0;
		this.Qj="";
		this.Qk="";
		this.status=RunningStatus.NotRunning;
		this.index=0;
	}
	
	public String toString() {
		
		return "Timer: "+ this.timer+ ", Tag: "+ this.tag+", busy: "+this.busy+
				", op: "+this.op+ ", Vj: "+ this.Vj+ ", Vk: "+this.Vk+
				", Qj: "+this.Qj+ ", Qk: "+ this.Qk+
				", status: "+this.status;
		            
	}
	
	

}

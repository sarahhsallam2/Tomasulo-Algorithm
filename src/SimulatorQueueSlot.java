public class SimulatorQueueSlot {
	String instructionName;
	String destinationReg;
	String sourceReg1;
	String sourceReg2;
	int issue;
	int startExecute;
	int endExecute;
	int writeResult;
	public SimulatorQueueSlot(String instructionName, String destinationReg, String sourceReg1, String sourceReg2, int issue) {
		
		this.instructionName = instructionName;
		this.destinationReg = destinationReg;
		this.sourceReg1 = sourceReg1;
		this.sourceReg2 = sourceReg2;
		this.issue = issue;
		this.startExecute = 0;
		this.endExecute = 0;
		this.writeResult = 0;
	}
	
	public SimulatorQueueSlot() {
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		return "Instruction: "+this.instructionName+", Destination: "+ this.destinationReg+", Source register 1: "+
		this.sourceReg1+ ", Source Register 2: "+this.sourceReg2+ ", Issue at: "+ this.issue+", Start Execute at: "+
		this.startExecute+", End Execute at: "+ this.endExecute+ ", Write Result at: "+this.writeResult;
		
	}
	
	
	

}

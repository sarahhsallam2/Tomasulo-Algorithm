
public class Instruction {
	String type;
	String destinationReg;
	String sourceReg1;
	String sourceReg2;
	String MemoryBranchReg;
	int address;
	int immediate;
	
	// ADD/ SUB/ MUL/ DIV/ DADD/ DSUB
	public Instruction(String type, String destinationReg, String sourceReg1, String sourceReg2) {  
		
		this.type = type;
		this.destinationReg = destinationReg;
		this.sourceReg1 = sourceReg1;
		this.sourceReg2 = sourceReg2;
	}
	
    
    // BNEZ/ LD/ SD 
    public Instruction(String type, String MemoryBranchReg, int address) {
		
		this.type = type;
		this.MemoryBranchReg = MemoryBranchReg;
		this.address = address;
	}
    
    @Override
	public String toString() {
		return "Instruction [type=" + type + ", destinationReg=" + destinationReg + ", sourceReg1=" + sourceReg1
				+ ", sourceReg2=" + sourceReg2 + ", MemoryBranchReg=" + MemoryBranchReg + ", address=" + address
				+ ", immediate=" + immediate + "]";
	}


	// ADDI/ SUBI
     public Instruction(String type, String destinationReg,String sourceReg1, int immediate) {
		
		this.type = type;
		this.destinationReg = destinationReg;
		this.sourceReg1=sourceReg1;
		this.immediate=immediate;
		
	}

}

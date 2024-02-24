import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Simulator {
	int clock;
	LoadBuffer[] loadBuffers;
	StoreBuffer[] storeBuffers;
	ResStation[] AddSub; // Add/Sub Reservation Station
	ResStation[] MulDiv; // Mul/Div Reservation Station
	RegFileSlot[] RegisterFile;
	double[] dataMemory; // Data Cache
	ArrayList<SimulatorQueueSlot> SimulatorQueue; // A Queue to store results for demonstration
	ArrayList<String> instructionMemory;
	int noOfInstructions = 0;
	Instruction[] InstructionQueue;
	int pc; // Store the address of the next instruction to be issued
	Hashtable<String, Integer> latencies = new Hashtable<>(); //  A Hashtable that store the latency of each instruction
	boolean branchIsExecuting = false; // If a branch is executing , then don't issue until the result occurs
	ArrayList<String> GUIPrinting= new ArrayList<String>();
	String temp="";

	// Printing
	String RESET = "\u001B[0m";
	String RED = "\u001B[31m";
	String GREEN = "\u001B[32m";
	String YELLOW = "\u001B[33m";
	String BLUE = "\u001B[30m";
	String MAGENTA = "\u001B[35m";
	String CYAN = "\u001B[36m";
	public Simulator(int addLatency, int mulLatency, int divLatency, int ldLatency, int sdLatency,int subLatency,int loadBufferSize, int storeBufferSize, int addSubSize, int mulDivSize
) {
		clock = 1;
		pc = 0;
		instructionMemory = new ArrayList();
		SimulatorQueue = new ArrayList();
		RegisterFile = new RegFileSlot[64]; // R[0 - 31], F[32 - 63]
		dataMemory = new double[100];
		intializeRegisterFile();
		readfile();
	   initializeSizes(loadBufferSize, storeBufferSize, addSubSize, mulDivSize);
       initializeLatencies(addLatency, mulLatency, divLatency, ldLatency, sdLatency,subLatency);

	    //initializeSizes();
	//takeSize();
	//	intializeLatencies();
		fillDataMemory();
		fillRegFile();
		run();
	}

	public void initializeLatencies(int addLatency, int mulLatency, int divLatency, int ldLatency, int sdLatency,int subLatency) {
	    latencies.put("ADD", addLatency);
	    latencies.put("SUB", subLatency); // Assuming same latency for ADD and SUB
	    latencies.put("MUL", mulLatency);
	    latencies.put("DIV", divLatency);
	    latencies.put("LD", ldLatency);
	    latencies.put("SD", sdLatency);
	    latencies.put("DADD", addLatency);
	    latencies.put("DSUB", subLatency);

	    // Fixed latencies
	    latencies.put("ADDI", 1);
	    latencies.put("BNEZ", 1);
	    latencies.put("SUBI", 1);
	   // latencies.put("DADD", 1);
	  //  latencies.put("DSUB", 1);
	}

	public void intializeRegisterFile() {
		for (int i = 0; i < RegisterFile.length; i++) {
			if (i < 32) {
				RegisterFile[i] = new RegFileSlot("R" + i, "0", 0.0);
			} else {
				RegisterFile[i] = new RegFileSlot("F" + (i - 32), "0", 0.0);
			}

		}
	}

	public void readfile() {
		String filePath = "assembly.txt";
		String line;
		try {

			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				instructionMemory.add(line);
				noOfInstructions++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		extractInstructions();

	}

	public void extractInstructions() {

		InstructionQueue = new Instruction[noOfInstructions];

		for (int i = 0; i < noOfInstructions; i++) {
			String[] instructionParts = instructionMemory.get(i).split(" ");

			if (instructionParts[0].equalsIgnoreCase("ADD") || instructionParts[0].equalsIgnoreCase("SUB")
					|| instructionParts[0].equalsIgnoreCase("MUL") || instructionParts[0].equalsIgnoreCase("DIV")
					|| instructionParts[0].equalsIgnoreCase("DADD") || instructionParts[0].equalsIgnoreCase("DSUB")) {

				InstructionQueue[i] = new Instruction(instructionParts[0].toUpperCase(), instructionParts[1], instructionParts[2], instructionParts[3]);

			} else if (instructionParts[0].equalsIgnoreCase("LD") || instructionParts[0].equalsIgnoreCase("SD")
					|| instructionParts[0].equalsIgnoreCase("BNEZ")) {

				int address = Integer.parseInt(instructionParts[2]);
				InstructionQueue[i] = new Instruction(instructionParts[0].toUpperCase(), instructionParts[1], address);

			}
			else if (instructionParts[0].equalsIgnoreCase("ADDI") || instructionParts[0].equalsIgnoreCase("SUBI")) {
// 4/12 Testing 
				int immediate = Integer.parseInt(instructionParts[3]);
				InstructionQueue[i] = new Instruction(instructionParts[0].toUpperCase(), instructionParts[1], instructionParts[2], immediate);

			}
		}

	}
	public void initializeSizes(int loadBufferSize, int storeBufferSize, int addSubSize, int mulDivSize) {
	    loadBuffers = new LoadBuffer[loadBufferSize];
	    for (int i = 0; i < loadBufferSize; i++) {
	        loadBuffers[i] = new LoadBuffer(); // Initialize each element separately
	    }

	    storeBuffers = new StoreBuffer[storeBufferSize];
	    for (int i = 0; i < storeBufferSize; i++) {
	        storeBuffers[i] = new StoreBuffer(); // Initialize each element separately
	    }

	    AddSub = new ResStation[addSubSize];
	    for (int i = 0; i < addSubSize; i++) {
	        AddSub[i] = new ResStation(); // Initialize each element separately
	        System.out.print("here");
	    }

	    MulDiv = new ResStation[mulDivSize];
	    for (int i = 0; i < mulDivSize; i++) {
	        MulDiv[i] = new ResStation(); // Initialize each element separately
	    }
	}

    
	public void takeSize() {
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the load buffer size: ");
		int n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		loadBuffers = new LoadBuffer[n];
		Arrays.fill(loadBuffers, new LoadBuffer());

		System.out.println("Enter the store buffer size: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		storeBuffers = new StoreBuffer[n];
		Arrays.fill(storeBuffers, new StoreBuffer());

		System.out.println("Enter the Add/Sub reservation station size: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		AddSub = new ResStation[n];
		Arrays.fill(AddSub, new ResStation());

		System.out.println("Enter the Mul/Div reservation station size: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		MulDiv = new ResStation[n];
		Arrays.fill(MulDiv, new ResStation());
	}

	public void intializeLatencies() {
		latencies.put("ADDI", 1);
		latencies.put("BNEZ", 1);
		latencies.put("SUBI", 1);
		latencies.put("DADD", 1);
		latencies.put("DSUB", 1);

		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the ADD/SUB latency: ");
		int n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		latencies.put("ADD", n);
		latencies.put("SUB", n);

		System.out.println("Enter the MUL latency: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		latencies.put("MUL", n);

		System.out.println("Enter the DIV latency: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		latencies.put("DIV", n);
		// take LD and SD latencies from the user    3/12
		System.out.println("Enter the LD latency: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		latencies.put("LD", n);

		System.out.println("Enter the SD latency: ");
		n = 0;
		if (sc.hasNextInt()) {
			n = sc.nextInt();
		}
		latencies.put("SD", n);
	}
	
	public void run() {
		int counter = 0;
		while (continueExecution() || pc < noOfInstructions) {
			temp="";
			System.out.println();
			System.out.println(CYAN + "----------------------------------------------------------------" + RESET);
			System.out.println(GREEN + "| Clock: " + clock + RESET);
			System.out.println(YELLOW + "| isBranch: " + branchIsExecuting + RESET);
			System.out.println(MAGENTA + "| PC: " + pc + RESET);
			System.out.println(CYAN + "----------------------------------------------------------------" + RESET);
			temp=temp+'\n' + "----------------------------------------------------------------" 
					+'\n' + "| Clock: " + clock +'\n' + "| isBranch: " + branchIsExecuting 
					+'\n' + "| PC: " + pc +'\n' + "----------------------------------------------------------------" ;
			
			if ( pc < noOfInstructions && !branchIsExecuting) {
				issue();
			}
			
			execute();
			writeback();
			print();
			GUIPrinting.add(temp);
			clock++;

			// testing
//			counter++;
//			if (counter == 5) {
//				break;
//			}
		}
		System.out.println(GUIPrinting);
	
	}

	// Initialize the register file with some values for testing purposes
	public void fillRegFile() {
		// size of reg file is 30 (R :0-->31 , F:32-->63)
		RegisterFile[0].value = 80; // R0
		RegisterFile[1].value = 2; // R1
		RegisterFile[2].value = 2; // R2
		RegisterFile[5].value = 30; // R5
		RegisterFile[10].value = 0; // R10
		RegisterFile[30].value = 40; // R30
		RegisterFile[32].value = 0; // F0
		RegisterFile[33].value = 1; // F1
		RegisterFile[34].value = 2; // F2
		RegisterFile[35].value = 0; // F3
		RegisterFile[37].value = 9.0; // F5
		RegisterFile[41].value = 2; // F9
		RegisterFile[42].value = 19.5; // F10
		RegisterFile[52].value = 1.99; // F20
		RegisterFile[53].value = 19.5; // F21
		RegisterFile[57].value = 10.5; // F25
		RegisterFile[61].value = 5.5; // F29
		RegisterFile[62].value = 19.5; // F30
		RegisterFile[63].value = 17.5; // F31
	}

	// Initialize the data memory with some values for testing purposes
	public void fillDataMemory() {
		// size of data memory =100
		dataMemory[0] = 12;
		dataMemory[4] = 80;
		dataMemory[10] = 5;
		dataMemory[20] = 22.5;
		dataMemory[30] = 60;
		dataMemory[40] = 60.2;
		dataMemory[50] = 44;
	}

	// Step 1: Add to the reservation station
	// Step 2: Add to the simulation queue
	public void issue() {
		Instruction currInst = InstructionQueue[pc]; // current instruction to be issued if possible
		String type = currInst.type;
		SimulatorQueueSlot newSlot = new SimulatorQueueSlot();
		boolean incrementPC = false;
		int SimQueueindex;

		if (type.equals("ADD") || type.equals("SUB") || type.equals("DADD") || type.equals("DSUB")
				|| type.equals("BNEZ") || type.equals("ADDI") || type.equals("SUBI")) {

			for (int i = 0; i < AddSub.length; i++) {
				if (AddSub[i].busy == 0) {
					if (type.equals("ADD") || type.equals("SUB") || type.equals("DADD") || type.equals("DSUB")) {

						newSlot = new SimulatorQueueSlot(currInst.type, currInst.destinationReg, currInst.sourceReg1,
								currInst.sourceReg2, clock);
						// add to Add/Sub reservation station
						AddSub[i] = AddToAddSubRS(currInst.sourceReg1, currInst.sourceReg2, type, i);

					} else if (type.equals("ADDI") || type.equals("SUBI")) {
						
						newSlot = new SimulatorQueueSlot(currInst.type, currInst.destinationReg, currInst.sourceReg1,
								currInst.immediate + "", clock);
						AddSub[i] = AddToAddSubImmRS(currInst.sourceReg1, currInst.immediate, type, i);

					} else if (type.equals("BNEZ")) {

						newSlot = new SimulatorQueueSlot(currInst.type, currInst.MemoryBranchReg, "-",
								currInst.address + "", clock);
						AddSub[i] = AddToAddSubImmRS(currInst.MemoryBranchReg, currInst.address, type, i);
					}

					SimulatorQueue.add(newSlot);
					SimQueueindex = SimulatorQueue.indexOf(newSlot); // get index of simulation for instruction
					AddSub[i].index = SimQueueindex; // set index in reservation station to connect both records

					// update tag in register file
					// if not branch 3/12
					if (!type.equals("BNEZ")) {	
					  int destRegindex = mapToRegisterFile(newSlot.destinationReg);
					  RegisterFile[destRegindex].Q = AddSub[i].tag;
					}
					
					incrementPC = true;
					break;
				}

			}

		}

		else if (currInst.type.equals("MUL") || currInst.type.equals("DIV")) {

			for (int i = 0; i < MulDiv.length; i++) {
				if (MulDiv[i].busy == 0) {

					// add new record in multiply and division reservation station
					MulDiv[i] = AddToMulDivRS(currInst.sourceReg1, currInst.sourceReg2, type, i);

					// add new slot inside simulation queue and gets it's index
					newSlot = new SimulatorQueueSlot(currInst.type, currInst.destinationReg, currInst.sourceReg1,
							currInst.sourceReg2, clock);
					SimulatorQueue.add(newSlot);
					SimQueueindex = SimulatorQueue.indexOf(newSlot); // get index of simulation for instruction
					MulDiv[i].index = SimQueueindex; // set index in reservation station to connect both records

					// update tag in register file
					System.out.println("DES REG "+ newSlot.destinationReg);
					int destRegindex = mapToRegisterFile(newSlot.destinationReg);
					System.out.println("DES REG index "+ destRegindex);
					RegisterFile[destRegindex].Q = MulDiv[i].tag;

					incrementPC = true;

					break;

				}

			}

		}

		else if (currInst.type.equals("LD")) {
			for (int i = 0; i < loadBuffers.length; i++) {
				if (loadBuffers[i].busy == 0) {

					// add new record inside load buffer
					LoadBuffer newLoadBufferSlot = new LoadBuffer(latencies.get("LD"),1, currInst.address, "L" + (i + 1));
					loadBuffers[i] = (newLoadBufferSlot);

					// add new slot inside simulation queue and gets it's index
					newSlot = new SimulatorQueueSlot(currInst.type, currInst.MemoryBranchReg, "-",
							currInst.address + "", clock);
					SimulatorQueue.add(newSlot);
					SimQueueindex = SimulatorQueue.indexOf(newSlot); // get index of simulation for instruction
					loadBuffers[i].index = SimQueueindex; // set index in buffer to connect both records

					// update tag in register file
					int destRegindex = mapToRegisterFile(newSlot.destinationReg);
					RegisterFile[destRegindex].Q = loadBuffers[i].tag;

					incrementPC = true;

					break;

				}
			}
		}

		else if (currInst.type.equals("SD")) {
			for (int i = 0; i < storeBuffers.length; i++) {
				if (storeBuffers[i].busy == 0) {

					// add new record inside store buffer
					storeBuffers[i] = AddToStoreBuffer(currInst.MemoryBranchReg, currInst.address, type, i);

					// add new slot inside simulation queue and gets it's index
					newSlot = new SimulatorQueueSlot(currInst.type, currInst.MemoryBranchReg, "-",
							currInst.address + "", clock);
					SimulatorQueue.add(newSlot);
					SimQueueindex = SimulatorQueue.indexOf(newSlot); // get index of simulation for instruction
					storeBuffers[i].index = SimQueueindex; // set index in buffer to connect both records

					incrementPC = true;

					break;

				}

			}

		}
		for (ResStation slot : AddSub) {
		   // handling the branch stall till the result occurs 6/12
			if(slot.op.equals("BNEZ")) {
				branchIsExecuting=true;
				//incrementPC=false;
			}
		}


		// To avoid index out of bound
		if (incrementPC) {
			pc++;
		}
	}

	public int checkDataHazard(String sourceReg1, String sourceReg2) {

		// get index of source register 1 and source register 2
		int sourceReg1index = mapToRegisterFile(sourceReg1);
		int sourceReg2index = mapToRegisterFile(sourceReg2);

		// both values found(Vj,Vk)
		if (RegisterFile[sourceReg1index].Q.equals("0") && RegisterFile[sourceReg2index].Q.equals("0")) {
			return 0;
		}
		// both values found(Qj,Vk)
		else if (!RegisterFile[sourceReg1index].Q.equals("0") && RegisterFile[sourceReg2index].Q.equals("0")) {
			return 1;
		}
		// both values found(Vj,Qk)
		else if (RegisterFile[sourceReg1index].Q.equals("0") && !RegisterFile[sourceReg2index].Q.equals("0")) {
			return 2;
		}
		// both values found(Qj,Qk)
		else {// if
				// (!RegisterFile[sourceReg1index].Q.equals("0")&&!RegisterFile[sourceReg2index].Q.equals("0"))
			return 3;
		}

	}

	public int checkDataHazardImm(String sourceReg1) {

		// get index of source register
		int sourceReg1index = mapToRegisterFile(sourceReg1);

		// value is found
		if (RegisterFile[sourceReg1index].Q.equals("0")) {
			return 0;
		}
		// value is not found
		else {// if (!RegisterFile[sourceReg1index].Q.equals("1")) {
			return 1;
		}

	}

// i = index where i found the first empty place in buffer
	public ResStation AddToAddSubRS(String sourceReg1, String sourceReg2, String type, int i) {

		int dataHazards = checkDataHazard(sourceReg1, sourceReg2);
		int sourceReg1index = mapToRegisterFile(sourceReg1); // Get index of source register 1
		int sourceReg2index = mapToRegisterFile(sourceReg2); // Get index of source register 2
		ResStation newAddSubRS = null;

		if (dataHazards == 0) {
			double Vj = RegisterFile[sourceReg1index].value;
			double Vk = RegisterFile[sourceReg2index].value;
			newAddSubRS = new ResStation(latencies.get(type), "A" + (i + 1), 1, type, Vj, Vk, "0", "0");
		}
		if (dataHazards == 1) {
			String Qj = RegisterFile[sourceReg1index].Q;
			double Vk = RegisterFile[sourceReg2index].value;
			newAddSubRS = new ResStation(latencies.get(type), "A" + (i + 1), 1, type, 0, Vk, Qj, "0");
		}
		if (dataHazards == 2) {
			double Vj = RegisterFile[sourceReg1index].value;
			String Qk = RegisterFile[sourceReg2index].Q;
			newAddSubRS = new ResStation(latencies.get(type), "A" + (i + 1), 1, type, Vj, 0, "0", Qk);
		}
		if (dataHazards == 3) {
			String Qj = RegisterFile[sourceReg1index].Q;
			String Qk = RegisterFile[sourceReg2index].Q;
			newAddSubRS = new ResStation(latencies.get(type), "A" + (i + 1), 1, type, 0, 0, Qj, Qk);
		}
		return newAddSubRS;
	}

	public ResStation AddToMulDivRS(String sourceReg1, String sourceReg2, String type, int i) {

		int dataHazards = checkDataHazard(sourceReg1, sourceReg2);
		int sourceReg1index = mapToRegisterFile(sourceReg1);
		int sourceReg2index = mapToRegisterFile(sourceReg2);
		ResStation newMulDivRS = null;

		if (dataHazards == 0) {
			double Vj = RegisterFile[sourceReg1index].value;
			double Vk = RegisterFile[sourceReg2index].value;
			newMulDivRS = new ResStation(latencies.get(type), "M" + (i + 1), 1, type, Vj, Vk, "0", "0");
		}
		if (dataHazards == 1) {
			String Qj = RegisterFile[sourceReg1index].Q;
			double Vk = RegisterFile[sourceReg2index].value;
			newMulDivRS = new ResStation(latencies.get(type), "M" + (i + 1), 1, type, 0, Vk, Qj, "0");
		}
		if (dataHazards == 2) {
			double Vj = RegisterFile[sourceReg1index].value;
			String Qk = RegisterFile[sourceReg2index].Q;
			newMulDivRS = new ResStation(latencies.get(type), "M" + (i + 1), 1, type, Vj, 0, "0", Qk);
		}
		if (dataHazards == 3) {
			String Qj = RegisterFile[sourceReg1index].Q;
			String Qk = RegisterFile[sourceReg2index].Q;
			newMulDivRS = new ResStation(latencies.get(type), "M" + (i + 1), 1, type, 0, 0, Qj, Qk);
		}
		return newMulDivRS;
	}

	// used for both ADDI , SUBI , BNEZ cause they are similar I only pass :
	// ADDI , SUBI --> sourceReg1,Immediate
	// BNEZ --> MemoryBranchReg,address

	public ResStation AddToAddSubImmRS(String sourceReg1, int immediate, String type, int i) {

		int dataHazards = checkDataHazardImm(sourceReg1);
		int sourceReg1index = mapToRegisterFile(sourceReg1);
		int Vk = immediate;

		ResStation newAddSubRS = null;

		if (dataHazards == 0) {
			double Vj = RegisterFile[sourceReg1index].value;
			newAddSubRS = new ResStation(latencies.get(type), "A" + (i + 1), 1, type, Vj, Vk, "0", "0");
		}
		if (dataHazards == 1) {
			String Qj = RegisterFile[sourceReg1index].Q;
			newAddSubRS = new ResStation(latencies.get(type), "A" + (i + 1), 1, type, 0, Vk, Qj, "0");
		}

		return newAddSubRS;
	}

	public StoreBuffer AddToStoreBuffer(String sourceReg1, int address, String type, int i) {

		int dataHazards = checkDataHazardImm(sourceReg1);
		int sourceReg1index = mapToRegisterFile(sourceReg1);

		StoreBuffer newStoreBuffer = null;

		if (dataHazards == 0) {
			double Vj = RegisterFile[sourceReg1index].value;
			newStoreBuffer = new StoreBuffer(latencies.get("SD"),1, address, Vj, "0", "S" + (i + 1));
		}
		if (dataHazards == 1) {
			String Qj = RegisterFile[sourceReg1index].Q;
			newStoreBuffer = new StoreBuffer(latencies.get("SD"),1, address, 0, Qj, "S" + (i + 1));
		}

		return newStoreBuffer;
	}

	public int mapToRegisterFile(String register) {

		char typeReg = register.charAt(0);
		String numberOfReg= register.substring(1);
		if (typeReg == 'R') {
			return Integer.parseInt("" + numberOfReg);
		}
      
        return Integer.parseInt("" + numberOfReg) + 32;
	}

	public void execute() {
		for (int i = 0; i < AddSub.length; i++) {
			ResStation current = AddSub[i];
			if (current.busy == 1) {
				if (latencies.get(current.op) > 1) {
					if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					} else if (current.timer == 0) {
						current.status = RunningStatus.WriteResult;
					} else if (current.timer == 1) {
						current.status = RunningStatus.Finished;
						current.timer--;
					} else if (current.status == RunningStatus.Running) {
						current.timer--;
					}
					else if (current.status == RunningStatus.Issued) {
						// if this instruction both source reg are ready and it didnot start exec yet
						if (current.Qj.equals("0") && current.Qk.equals("0")) {
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get(current.op) - 1;
							current.status = RunningStatus.Running;
							current.timer--;
						}
					}
				} else { // if this operation only takes 1 clock cycle it will never appear running
							// (start at clock=5 and end at clock=5)
					if (current.status == RunningStatus.Finished) {
						current.status = RunningStatus.WriteResult;
						// handling the branch stall till the result occurs 3/12
//						if(current.op.equals("BNEZ")) {
//							branchIsExecuting=false;
//						}
					}
					else if (current.status == RunningStatus.Issued) {
						// branch handling 3/12
						if(current.op.equals("BNEZ")) {
							// 4/12 testing 
							if (current.Qj.equals("0")) {
								// set start execution time = current clock cycle
								SimulatorQueue.get(current.index).startExecute = clock;
								// set end execution time = current clock cycle + latency of this type of
								// instruction
								SimulatorQueue.get(current.index).endExecute = clock + latencies.get(current.op) - 1;
								current.status = RunningStatus.Finished;
								current.timer--;
							}
						}
						// if this instruction both source reg are ready and it didnot start exec yet
						else {
							if (current.Qj.equals("0") && current.Qk.equals("0")) {			
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get(current.op) - 1;
							current.status = RunningStatus.Finished;
							current.timer--;
							}
						}
					}
					else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}
				}
			}
			// 3/12
			else
				current.status = RunningStatus.NotRunning;
		}

		for (int i = 0; i < MulDiv.length; i++) {
			ResStation current = MulDiv[i];
			if (current.busy == 1) {

				if (latencies.get(current.op) > 1) {
					if (current.timer == 0) {
						current.status = RunningStatus.WriteResult;
					} else if (current.timer == 1) {
						current.status = RunningStatus.Finished;
						current.timer--;
					} else if (current.status == RunningStatus.Running) {
						current.timer--;
					} else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}

					else if (current.status == RunningStatus.Issued) {
						// if this instruction both source reg are ready and it didnot start exec yet
						if (current.Qj.equals("0") && current.Qk.equals("0")) {
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get(current.op) - 1;
							current.status = RunningStatus.Running;
							current.timer--;
						}
					}

				} else { // if this operation only takes 1 clock cycle it will never appear running
							// (start at clock=5 and end at clock=5)

					if (current.status == RunningStatus.Finished) {
						current.status = RunningStatus.WriteResult;
					} else if (current.status == RunningStatus.Issued) {
						// if this instruction both source reg are ready and it didnot start exec yet
						if (current.Qj.equals("0") && current.Qk.equals("0")) {
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get(current.op) - 1;
							current.status = RunningStatus.Finished;
							current.timer--;
						}

					} else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}
				}
			}
			// 3/12 TODO 
			else
				current.status = RunningStatus.NotRunning;
		}

		for (int i = 0; i < loadBuffers.length; i++) {
			LoadBuffer current = loadBuffers[i];
			if (current.busy == 1) {
				// Timer is added to the LoadBuffer class 3/12
				if (latencies.get("LD") > 1) {
					if (current.timer == 0) {
						current.status = RunningStatus.WriteResult;
					} else if (current.timer == 1) {
						current.status = RunningStatus.Finished;
						current.timer--;
					} else if (current.status == RunningStatus.Running) {
						current.timer--;
					} else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}

					else if (current.status == RunningStatus.Issued) {
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get("LD") - 1;
							current.status = RunningStatus.Running;
							current.timer--;
						}

				} else { // if this operation only takes 1 clock cycle it will never appear running
							// (start at clock=5 and end at clock=5)
					if (current.status == RunningStatus.Finished) {
						current.status = RunningStatus.WriteResult;
					} else if (current.status == RunningStatus.Issued) {
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get("LD") - 1;
							current.status = RunningStatus.Finished;
							current.timer--;
						
					} else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}
				}
			}
			// 3/12 TODO
			else
				current.status = RunningStatus.NotRunning;
		}

		for (int i = 0; i < storeBuffers.length; i++) {
			StoreBuffer current = storeBuffers[i];
			if (current.busy == 1) {
				// Timer is added to the StoreBuffer class 3/12
				if (latencies.get("SD") > 1) {
					if (current.timer == 0) {
						current.status = RunningStatus.WriteResult;
					} else if (current.timer == 1) {
						current.status = RunningStatus.Finished;
						current.timer--;
					} else if (current.status == RunningStatus.Running) {
						current.timer--;
					} else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}

					else if (current.status == RunningStatus.Issued) {
						if (current.Q.equals("0")) {
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get("SD") - 1;
							current.status = RunningStatus.Running;
							current.timer--;
						}
						}

				} else { // if this operation only takes 1 clock cycle it will never appear running
							// (start at clock=5 and end at clock=5)
					if (current.status == RunningStatus.Finished) {
						current.status = RunningStatus.WriteResult;
					} else if (current.status == RunningStatus.Issued) {
						if (current.Q.equals("0")) {
							// set start execution time = current clock cycle
							SimulatorQueue.get(current.index).startExecute = clock;
							// set end execution time = current clock cycle + latency of this type of
							// instruction
							SimulatorQueue.get(current.index).endExecute = clock + latencies.get("LD") - 1;
							current.status = RunningStatus.Finished;
							current.timer--;
						}
						
					} else if (current.status == RunningStatus.NotRunning) {
						current.status = RunningStatus.Issued;
					}
				}
			}
			// 3/12
			else
				current.status = RunningStatus.NotRunning;
		}
	}

	// Steps:
	// 1. Traverse the ADD/SUB, MUL/DIV reservation stations and traverse the LOAD buffers and STORE buffers (DONE)
	// 2. Store the Objects (ResStation, LoadBuffer, StoreBuffer) instances in a Queue that have "RunningStatus" -> WriteResult (DONE)
	// 3. Start the Write back process for the first instruction in the Queue: (DONE)
		// |-> a. Calculate the result value from the instruction (DONE)
		// |-> b. Check the type of the instruction (ADD/SUB, MUL/DIV, LOAD, STORE) (DONE)
		// |-> c. Clear (ADD/SUB, MUL/DIV, LOAD, STORE) (DONE)
		// |-> d. Traverse over ADD/SUB, MUL/DIV reservation stations and traverse the LOAD buffers and STORE buffers:
			// |-> If instruction stored in reservation stations or in LOAD buffers and STORE buffers depends on the current instruction that write back (DONE)
			// |-> 1. Update simulation queue (DONE)
			// |-> 2. Update the register file (DONE)
			// |-> 3. Update the reservation station and the load buffers (DONE)
	public void writeback() {
		ArrayList<Object> listOfInstructions = new ArrayList<>();

		// Traverse over the ADD/SUB reservation stations
		for (int i = 0; i < AddSub.length; i++) {
			ResStation slot = AddSub[i];
			// Check the running status of the current instruction in the ADD/SUB reservation station
			if (slot.status == RunningStatus.WriteResult && slot.busy == 1) {
				// Add the current instruction to "lisOfInstructions" if its status is "WriteResult"
				listOfInstructions.add(slot);
			}
		}

		// Traverse over MUL/DIV reservation stations
		for (int i = 0; i < MulDiv.length; i++) {
			ResStation slot = MulDiv[i];
			// Check the running status of the current instruction in the MUL/DIV reservation station
			if (slot.status == RunningStatus.WriteResult && slot.busy == 1) {
				// Add the current instruction to "listOfInstructions" if its status is "WriteResult"
				listOfInstructions.add(slot);
			}
		}

		// Traverse over the LOAD buffers
		for (int i = 0; i < loadBuffers.length; i++) {
			LoadBuffer slot = loadBuffers[i];
			// Check the running status of the current instruction in the LOAD buffer
			if (slot.status == RunningStatus.WriteResult && slot.busy == 1) {
				// Add the current instruction to "listOfInstructions" if its status is "WriteResult"
				listOfInstructions.add(slot);
			}
		}

		// Traverse over the STORE buffers
		for (int i = 0; i < storeBuffers.length; i++) {
			StoreBuffer slot = storeBuffers[i];
			// Check the running status of the current instruction in the STORE buffer
			if (slot.status == RunningStatus.WriteResult && slot.busy == 1) {
				// Add the current instruction to "listOfInstructions" if its status is "WriteResult"
				listOfInstructions.add(slot);
			}
		}

		// Trivial Case: If there exist no instruction in reservation stations and buffers that need to write back
		if (listOfInstructions.isEmpty()) {
			System.out.println(RED + "      Write Back Stage      " + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Published Tag: " + "none" + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Publish Result: " + "none" + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			return;
		}

		Object instructionToWriteBack = null;
		int minSimulatorQueueIndex = -1; // index of the first instruction that need to write back in the Simulator queue

		// Traverse over the Simulator Queue
		for (Object obj : listOfInstructions) {
			Class<?> clazz = obj.getClass();

			// Current slot is related to ADD/SUB or MUL/DIV reservation stations
			if (clazz == ResStation.class) {
				ResStation slot = (ResStation)obj;
				if (minSimulatorQueueIndex == -1 || slot.index < minSimulatorQueueIndex) {
					minSimulatorQueueIndex = slot.index;
					instructionToWriteBack = slot;
				}
			}

			// Current slot is related to LOAD buffer
			if (clazz == LoadBuffer.class) {
				LoadBuffer slot = (LoadBuffer)obj;
				if (minSimulatorQueueIndex == -1 || slot.index < minSimulatorQueueIndex) {
					minSimulatorQueueIndex = slot.index;
					instructionToWriteBack = slot;
				}
			}

			// Current slot is related to STORE buffer
			if (clazz == StoreBuffer.class) {
				StoreBuffer slot = (StoreBuffer)obj;
				if (minSimulatorQueueIndex == -1 || slot.index < minSimulatorQueueIndex) {
					minSimulatorQueueIndex = slot.index;
					instructionToWriteBack = slot;
				}
			}
		}

		// Get the value that would be stored in the destination register
		double result = calculateResult(instructionToWriteBack);

		// Update the Simulator Queue
		SimulatorQueueSlot simulatorQueueSlot = SimulatorQueue.get(minSimulatorQueueIndex);
		simulatorQueueSlot.writeResult = clock;

		// Clear the slot from ADD/SUB, MUL/DIV reservation stations, LOAD or STORE buffers
		if (instructionToWriteBack.getClass().equals(ResStation.class)) {
			String tag = ((ResStation) instructionToWriteBack).tag;
			String op = ((ResStation) instructionToWriteBack).op;
			((ResStation) instructionToWriteBack).timer = 0;
			((ResStation) instructionToWriteBack).tag = "";
			((ResStation) instructionToWriteBack).busy = 0;
			((ResStation) instructionToWriteBack).op = "";
			((ResStation) instructionToWriteBack).Vj = 0;
			((ResStation) instructionToWriteBack).Vk = 0;
			((ResStation) instructionToWriteBack).Qj = "";
			((ResStation) instructionToWriteBack).Qk = "";
			((ResStation) instructionToWriteBack).status =RunningStatus.NotRunning ;
			// BNEZ instruction do not write back
			if (op.equals("BNEZ")) {
				branchIsExecuting = false;
				System.out.println(RED + "      Write Back Stage      " + RESET);
				System.out.println(RED + "----------------------------" + RESET);
				System.out.println(RED + "| Published Tag: " + tag + RESET);
				System.out.println(RED + "----------------------------" + RESET);
				System.out.println(RED + "| Publish Result: " + "none" + RESET);
				System.out.println(RED + "----------------------------" + RESET);
				return;
			}
			System.out.println(RED + "      Write Back Stage      " + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Published Tag: " + tag + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Publish Result: " + result + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			// Publish the result
			publishResult(tag, result);
		} else if (instructionToWriteBack.getClass().equals(LoadBuffer.class)) {
			String tag = ((LoadBuffer) instructionToWriteBack).tag;
			((LoadBuffer) instructionToWriteBack).timer = 0;
			((LoadBuffer) instructionToWriteBack).tag = "";
			((LoadBuffer) instructionToWriteBack).busy = 0;
			((LoadBuffer) instructionToWriteBack).address = 0;
			((LoadBuffer) instructionToWriteBack).status = RunningStatus.NotRunning;
			((LoadBuffer) instructionToWriteBack).index = 0;
			System.out.println(RED + "      Write Back Stage      " + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Published Tag: " + tag + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Publish Result: " + result + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			// Publish the result
			publishResult(tag, result);
		} else if (instructionToWriteBack.getClass().equals(StoreBuffer.class)) {
			String tag = ((StoreBuffer) instructionToWriteBack).tag;
			((StoreBuffer) instructionToWriteBack).timer = 0;
			((StoreBuffer) instructionToWriteBack).tag = "";
			((StoreBuffer) instructionToWriteBack).busy = 0;
			((StoreBuffer) instructionToWriteBack).address = 0;
			((StoreBuffer) instructionToWriteBack).status = RunningStatus.NotRunning;
			((StoreBuffer) instructionToWriteBack).index = 0;
			((StoreBuffer) instructionToWriteBack).V = 0;
			((StoreBuffer) instructionToWriteBack).Q = "";
			System.out.println(RED + "      Write Back Stage      " + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Published Tag: " + tag + RESET);
			System.out.println(RED + "----------------------------" + RESET);
			System.out.println(RED + "| Publish Result: " + "none" + RESET);
			System.out.println(RED + "----------------------------" + RESET);
		}
	}

	public void publishResult(String tag, double data) {
		// Traverse the ADD/SUB reservation stations
        for (ResStation slot : AddSub) {
            if (slot.Qk.equals(tag)) {
                slot.Vk = data;
                slot.Qk = "0";
            }
            if (slot.Qj.equals(tag)) {
                slot.Vj = data;
                slot.Qj = "0";
            }
        }

		// Traverse the MUL/DIV reservation stations
        for (ResStation slot : MulDiv) {
            if (slot.Qk.equals(tag)) {
                slot.Vk = data;
                slot.Qk = "0";
            }
            if (slot.Qj.equals(tag)) {
                slot.Vj = data;
                slot.Qj = "0";
            }
        }

		// Note: We do not traverse load buffers as load does not depend on anything in our project

		// Traverse the STORE buffer
        for (StoreBuffer slot : storeBuffers) {
            if (slot.Q.equals(tag)) {
                slot.V = data;
				slot.Q = "0";
            }
        }

		// Traverse the Register file
		for (RegFileSlot slot : RegisterFile) {
			if (slot.Q.equals(tag)) {
				slot.value = data;
				slot.Q = "0";
			}
		}

	}

	public double calculateResult(Object slot) {
		Class<?> clazz = slot.getClass();

		if (clazz == ResStation.class) { // ADD, SUB, MUL, DIV, ADDI, SUBI, DADD, DSUB, BNEZ
			if (((ResStation) slot).op.equals("ADD")) {
				return ((ResStation) slot).Vj + ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("SUB")) {
				return ((ResStation) slot).Vj - ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("MUL")) {
				return ((ResStation) slot).Vj * ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("DIV")) {
				return ((ResStation) slot).Vj / ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("ADDI")) {
				return ((ResStation) slot).Vj + ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("SUBI")) {
				return ((ResStation) slot).Vj - ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("DADD")) {
				return ((ResStation) slot).Vj + ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("DSUB")) {
				return ((ResStation) slot).Vj - ((ResStation) slot).Vk;
			}
			else if (((ResStation) slot).op.equals("BNEZ")) {
				if (((ResStation) slot).Vj != 0) {
					pc = (int)((ResStation) slot).Vk - 1;
				}

				return -1;
			}
		}
		else if (clazz == LoadBuffer.class) { // Load instruction
			String tag = ((LoadBuffer) slot).tag;

			// Get the destination register name
			String destinationRegisterName = getRegisterName(tag);

			if (destinationRegisterName.equals("")) {
				return dataMemory[((LoadBuffer) slot).address];
			}

			// Get the destination register index
			int registerIndex = mapToRegisterFile(destinationRegisterName);

            // Get the data from the data cache and load the data in the destination register
			RegisterFile[registerIndex].value = dataMemory[((LoadBuffer) slot).address];
			RegisterFile[registerIndex].Q = "0";

			return dataMemory[((LoadBuffer) slot).address];
		}
		else if (clazz == StoreBuffer.class) { // Store instruction
			// Store the data in the data cache
			dataMemory[((StoreBuffer) slot).address] = ((StoreBuffer) slot).V;

			return ((StoreBuffer) slot).V;
		}

		return -1;
	}

	public String getRegisterName(String tag) {
		String destinationRegisterName = "";

		// Get the destination register name linked to the tag
		for (int i = 0; i < RegisterFile.length; i++) {
			RegFileSlot registerSlot = RegisterFile[i];
			if (registerSlot.Q.equals(tag)) {
				destinationRegisterName = registerSlot.name;
			}
		}

		return destinationRegisterName;
	}

	// We continue execution as long as there is a reservation station or buffer equal to 1
	public boolean continueExecution() {
		boolean continueExecute = false;
		// Traverse Add/Sub reservation stations
        for (ResStation station : AddSub) {
            if (station.busy == 1) {
                continueExecute = true;
            }
        }

		// Traverse Mul/Div reservation stations
        for (ResStation resStation : MulDiv) {
            if (resStation.busy == 1) {
                continueExecute = true;
            }
        }

		// Traverse Load buffers
        for (LoadBuffer loadBuffer : loadBuffers) {
            if (loadBuffer.busy == 1) {
                continueExecute = true;
            }
        }

		// Traverse Store buffers
        for (StoreBuffer storeBuffer : storeBuffers) {
            if (storeBuffer.busy == 1) {
                continueExecute = true;
            }
        }

		return continueExecute;
	}

	//Prints
	public String PrintInstructionQueue() {
		String s = "";
		for (int i = 0; i < InstructionQueue.length; i++) {
			s = s + "" + " " + i + " " + InstructionQueue[i] + " ,  " + "\n";
		}
		return s;
	}

	public String PrintSimulatorQueue() {
		String s = "";
		for (int i = 0; i < SimulatorQueue.size(); i++) {
			s = s + "" + " " + i + " " + SimulatorQueue.get(i) + " ,  " + "\n";
		}
		return s;
	}

	public String PrintAddResStation() {
		String s = "";
		for (int i = 0; i < AddSub.length; i++) {
			s = s + "" + " " + i + " " + AddSub[i] + " ,  " + "\n";
		}
		return s;
	}

	public String PrintMultResStation() {
		String s = "";
		for (int i = 0; i < MulDiv.length; i++) {
			s = s + "" + " " + i + " " + MulDiv[i] + " ,  " + "\n";
		}
		return s;
	}

	public String PrintLoadBuffer() {
		String s = "";
		for (int i = 0; i < loadBuffers.length; i++) {
			s = s + "" + " " + i + " " + loadBuffers[i] + " ,  " + "\n";
		}
		return s;
	}

	public String PrintStoreBuffer() {
		String s = "";
		for (int i = 0; i < storeBuffers.length; i++) {
			s = s + "" + " " + i + " " + storeBuffers[i] + " ,  " + "\n";
		}
		return s;
	}

	public String PrintRegFile() {
		String s = "";
		for (int i = 0; i < RegisterFile.length; i++) {
			s = s + "" + " " + i + " " + RegisterFile[i] + " ,  " + "\n";
		}
		return s;
	}

	public String PrintDataMemory() {
		String s = "";
		for (int i = 0; i < dataMemory.length; i++) {
			s = s + "" + " " + i + " " + dataMemory[i] + " ,  " + "\n";
		}
		return s;
	}
	public void clockPrintHelper(int clock) {
	//	for(int i =0;i<clockPrinting.length)
	}
	public void print() {
		String InstructionQueue = PrintInstructionQueue();
		String SimulatorQueue = PrintSimulatorQueue();
		String AddRS = PrintAddResStation();
		String MultRS = PrintMultResStation();
		String LoadBuffer = PrintLoadBuffer();
		String StoreBuffer = PrintStoreBuffer();
		String RegisterFile = PrintRegFile();
		String DataMemory = PrintDataMemory();
		
		// System.out.println("Instruction queue: " + "\n" + InstructionQueue);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Simulator queue: " + "\n" + SimulatorQueue);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Add Resv Station: " + "\n" + AddRS);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Mult Resv Station: " + "\n" + MultRS);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Load Buffer: " + "\n" + LoadBuffer);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Store Buffer: " + "\n" + StoreBuffer);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Register File: " + "\n" + RegisterFile);
		System.out.println("|----------------------------------------------------------------|");
		System.out.println("Data Memory: " + "\n" + DataMemory);
		temp=temp+'\n'+"|----------------------------------------------------------------|"+'\n'
				+"\n"+"Simulator queue: " + "\n" + SimulatorQueue+"\n"+"|----------------------------------------------------------------|"
				+"\n"+"Add Resv Station: " + "\n" + AddRS+"\n"+"|----------------------------------------------------------------|"
				+ "\n"+"Mult Resv Station: " + "\n" + MultRS+"\n"+"|----------------------------------------------------------------|"
				+ "\n"+"Load Buffer: " + "\n" + LoadBuffer+"\n"+ "|----------------------------------------------------------------|"
				+"\n"+"Store Buffer: " + "\n" + StoreBuffer+"\n"+"|----------------------------------------------------------------|"
				+ "\n"+"Register File: " + "\n" + RegisterFile+"|----------------------------------------------------------------|" 
	         	+ "\n"+"Data Memory " + "\n" + DataMemory+"|----------------------------------------------------------------|" ;
        
	}

	public static void main(String[] args) {

		//Simulator simulator = new Simulator();

//		String InstructionQueue = simulator.PrintInstructionQueue();
//		String SimulatorQueue = simulator.PrintSimulatorQueue();
//		String AddRS = simulator.PrintAddResStation();
//		String MultRS = simulator.PrintMultResStation();
//		String LoadBuffer = simulator.PrintLoadBuffer();
//		String StoreBuffer = simulator.PrintStoreBuffer();
//		String RegisterFile = simulator.PrintRegFile();
//		String DataMemory = simulator.PrintDataMemory();
//
//		System.out.println("Instruction queue: " + "\n" + InstructionQueue);
//		System.out.println("Simulator queue: " + "\n" + SimulatorQueue);
//		System.out.println("Add Resv Station: " + "\n" + AddRS);
//		System.out.println("Mult Resv Station: " + "\n" + MultRS);
//		System.out.println("Load Buffer: " + "\n" + LoadBuffer);
//		System.out.println("Store Buffer: " + "\n" + StoreBuffer);
//		System.out.println("Register File: " + "\n" + RegisterFile);
//		System.out.println("Data Memory: " + "\n" + DataMemory);

	}

}




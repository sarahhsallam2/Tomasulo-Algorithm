In our Simulator class constructor, we started by initializing our clock by 1, pc by 0, RegisterFile by size of 64, where first 32 addresses are for integers, and the second 32 addresses are for floating points numbers, dataMemory by size of 100. In addition, we call readfile() which read the assembly.txt and put instructions in instructionMemory, and in readfile we call extractInstructions() which loop over instructionMemory and fill the InstructionQueue. We also intializeRegisteFile and fillRegFile with some pre-loaded values, and initializeSizes of loadBuffers, storeBuffers, AddSub, MulDiv with the size that we take as inputs.
And the most important thing in the constructor that we call run() which loop until pc < noOfInstructions or a boolean that is returned from a method called continueExecution() which loop over reservation stations and buffers and check the busy of every slot in each if there is a busy = 1 it returns true otherwise it returns false which is used if there is a loop or branch cause we can not keep track with pc only. In the loop every iteration we call issue(), that taking into consideration is not called unless
pc< noOfinstructions and there is no branch as we do not predict , execute(), writeback(), and increment the clock.
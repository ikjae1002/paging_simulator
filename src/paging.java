import java.util.LinkedList;
import java.util.Scanner;

public class paging {
	int machinesize;
	int pagesize;
	int processsize;
	int jobmix;
	int referenceNumber;
	int replacementAlgo;
	int numOfFrames;
	int quantum = 3;
	int time;
	int fid;
	Scanner random;
	String algo;
	
	LinkedList<frame> frames = new LinkedList<frame>();
	LinkedList<process> processes = new LinkedList<process>();
	
	public class process{
		int size;
		int word;
		int numPageFault;
		int numEvict;
		float residencyTime;
		int ID;
		int ref;
		jobprobability job;
		boolean isFinished;
		
		public process(int ID, jobprobability job){
			this.size = processsize;
			this.ID = ID;
			this.ref = referenceNumber;
			this.word = (111 * ID) % processsize;
			this.numPageFault = 0;
			this.numEvict = 0;
			this.residencyTime = 0;
			this.job = job;
			this.isFinished = false;
		}
		
		int getWord(){
			return word;
		}
		
		void decrementRef(){
			ref--;
			if(ref == 0){
				isFinished = true;
			}
		}
		
		boolean finished(){
			return this.isFinished;
		}
		
		double getA(){
			return job.A;
		}
		
		double getB(){
			return job.B;
		}
		
		double getC(){
			return job.C;
		}
	}
	
	public class frame{
		int frameID;
		int procID;
		int starting;
		int pageNum;
		int frameTime;
		
		public frame(int ID, int pID, int time, int page){
			this.frameID = ID;
			starting = time;
			procID = pID;
			pageNum = page;
		}
		
		public frame(frame m){
			this.frameID = m.frameID;
			this.starting = m.starting;
			this.procID = m.procID;
			this.pageNum = m.pageNum;
		}
		
		boolean isHit(process pro, int pageNum){
			if(this.procID == pro.ID && this.pageNum == pageNum){
				return true;
			}
			return false;
		}
		
		void update(int ID, int pID, int time){
			this.frameID = ID;
			this.procID = pID;
			this.starting = time;
		}
	}
	
	public paging(String[] args, Scanner rando){
		machinesize = Integer.parseInt(args[0]);
		pagesize = Integer.parseInt(args[1]);
		processsize = Integer.parseInt(args[2]);
		jobmix = Integer.parseInt(args[3]);
		referenceNumber = Integer.parseInt(args[4]);
		random = rando;
		algo = args[5];
		
		// if jobmix 1, 1 process
		switch(jobmix) {
		case 1: 
			process pro = new process(1, new jobprobability(1,0,0,0));
			processes.add(pro);
			process temp = processes.peek();
			System.out.println("Initial temp: "+temp.ref);
			break;
		
		case 2:
			for(int i = 1; i <=4; i++){
				pro = new process(i, new jobprobability(1,0,0,0));
				processes.add(pro);
			}
			break;
			
		case 3:
			for(int i = 1; i <=4; i++){
				pro = new process(i, new jobprobability(0,0,0,1));
			}
			break;
			
		case 4:
			pro = new process(1, new jobprobability(0.75,0.25,0,0));
			processes.add(pro);
			pro = new process(2, new jobprobability(0.75,0,0.25,0));
			processes.add(pro);
			pro = new process(3, new jobprobability(0.75,0.125,0.125,0));
			processes.add(pro);
			pro = new process(4, new jobprobability(0.5,0.125,0.125,0.25));
			processes.add(pro);
			
			
		}
		
		this.fid = this.numOfFrames = machinesize / pagesize;
		
		calculate();
		printOut();
	}
	
	public int getNextWord(process pro, int randNum){
		double quotient = randNum / (Integer.MAX_VALUE + 1d);
		
		if(quotient < pro.getA()){
			return (pro.word + 1) % processsize;
		}else if(quotient < pro.getA() + pro.getB()){
			return (pro.word - 5 + processsize) % processsize;
		}else if(quotient < pro.getA() + pro.getB() + pro.getC()){
			return (pro.word + 4) % processsize;
		}else{
			int rand = random.nextInt();
			return random.nextInt() % processsize;
		}
	}
	
	public boolean isFinished(){
		for(int i = 0; i < processes.size(); i++){
			if(!processes.get(i).isFinished){
				return false;
			}
		}
		return true;
	}
	
	public void calculate(){
		int word;
		int page;
		int randNum;
		frame f = null;
		process p = null;
		
		
		while(!isFinished()){
			boolean run = false;
			for (int j = 0; j < processes.size(); j++){
				process temp = processes.peek();
				if(!temp.isFinished){
					p = temp;
					run = true;
				}processes.poll();
				processes.add(temp);
				if(run){
					break;
				}
			}
			word = p.word;
			System.out.print("--Quantum-- Proc ID: " + p.ID +" ");
			System.out.println("page faults: " + p.numPageFault);
			
			boolean cont = true;
				
			for(int i = 0; i < quantum; i++){
				time++;
				page = word / pagesize;
				randNum = random.nextInt();
				System.out.printf("%d references word %d (page %d) at time %d: ", 
						p.ID, p.word, (p.word / this.pagesize), this.time);
				for(frame temp: frames){
					System.out.print("Frame ID, page: " + temp.procID + temp.pageNum + " Proc ID, page: " + p.ID + page);
					if(temp.isHit(p, page) && cont == true){
						System.out.printf("Hit in frame %d\n", temp.frameID);
						if(!this.algo.equals("random")){
							frames.remove(temp);
							frames.add(temp);
						}
						cont = false;
					}
				}System.out.println("Frame Size, framenumbers: " + frames.size() + numOfFrames);
				if(frames.size() !=  numOfFrames && cont == true){
					frame temp = new frame(--fid, p.ID, time, page);
					frames.add(temp);
					cont = false;
					if(algo.equals("random")){
						LinkedList<frame> ftemp = new LinkedList<frame>();
						while(!frames.isEmpty()){
							int smallest = Integer.MAX_VALUE;
							for(frame ind: frames){
								if(ind.frameID < smallest){
									smallest = ind.frameID;	
								}
							}for(int ind = 0; ind < frames.size(); ind++){
								if(frames.get(ind).frameID == smallest){
									ftemp.addLast(frames.get(ind));
									frames.remove(ind);
								}
							}
						}frames = ftemp;
					}
					System.out.printf("Fault, using free frame %d.\n", temp.frameID);
				}
				if(cont){
					System.out.println("here in eviction");
					int evictPID = 0;
					frame temp = null;
					if(algo.equals("fifo")||algo.equals("lru")){
						frame fr = frames.poll();
						temp = new frame(fr);
						System.out.println("----Process ID: "+temp.procID);
						evictPID = fr.procID;
						fr.update(fr.frameID,p.ID, time);
						System.out.println("----Process ID: "+temp.procID);
						frames.add(fr);
					}else if(algo.equals("random")){
						randNum = random.nextInt();
						int randIndex = (randNum + numOfFrames) % numOfFrames;
						System.out.println();
						System.out.println(this.frames.size());
						for (frame fa: this.frames) {
							System.out.println(fa.frameID);
						}
						System.out.printf("%d uses random number: %d\n", p.ID, randNum);
						System.out.printf("(%d + %d) mod %d\n", randNum, this.numOfFrames, this.numOfFrames);
						System.out.println(randIndex);
						frame fr = frames.remove(randIndex);
						temp = new frame(fr);
						evictPID = fr.procID;
						fr.update(fr.frameID, p.ID, time);
						frames.add(fr);
					}
					System.out.printf("Fault, evicting page %d of %d from frame %d.\n", 
							page, temp.procID, temp.frameID);
					int elapsedTime = time - temp.starting;
					temp.frameTime = time;
					process evicted = null;
					for(process pros : processes){
						if(pros.ID == evictPID){
							evicted = pros;
						}
					}
					System.out.println("-evicted ID-: " + evicted.ID);
					System.out.println("elapsed: " + elapsedTime);
					evicted.residencyTime += elapsedTime;
					evicted.numEvict++;
					evicted.numPageFault++;
				}
				cont = true;
				word = getNextWord(p, randNum);
				p.decrementRef();
				System.out.println("Remaining refs: "+p.ref);
				System.out.println("Residency Time: "+p.residencyTime);
				p.word = word;
				if(p.ref == 0){
					break;
				}
				System.out.printf("%d uses random number: %d\n", p.ID, randNum);
			}if(isFinished()){
				p.numPageFault++;
			}
			
		}
	}
	
	public void printOut(){
		System.out.println("-------------------------------------\nThe machine size is " + machinesize + ".");
		System.out.println("The page size is " + pagesize + ".");
		System.out.println("The process size is " + processsize + ".");
		System.out.println("The job mix number is " + jobmix + ".");
		System.out.println("The number of references per process is " + referenceNumber + ".");
		System.out.println("The replacement algorithm is " + algo + ".\n");
		
		int totalFaults = 0, totalEvictions = 0, avgResProcCount = 0;
		double totalAvgResTime = 0;
		
		for (process p: processes) {
			System.out.println(p.residencyTime);
			//System.out.println(p.numEvictions);
			if (p.numEvict != 0) {
				totalAvgResTime += p.residencyTime;
				p.residencyTime = p.residencyTime / p.numEvict;
				totalEvictions += p.numEvict;
				avgResProcCount++;
			}
			totalFaults += p.numPageFault;
			
			if (p.numEvict == 0) {
				System.out.printf("Process %d had %d faults.\n", p.ID, p.numPageFault);
				System.out.println("With no evictions, the average residence is undefined.");
				continue;
			}
			System.out.printf("Process %d had %d faults and %f average residency.\n", p.ID,
					p.numPageFault, p.residencyTime);
		}
		
		totalAvgResTime = totalAvgResTime / totalEvictions;
		System.out.printf("The total number of faults is %d.", totalFaults);
		if (totalEvictions == 0) {
			System.out.println("\nWith no evictions, the overall average residence is undefined.");
		}
		else {
			System.out.printf("The overall average residency is %f.\n", totalAvgResTime);
		}
	}
}

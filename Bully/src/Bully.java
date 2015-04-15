import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;


public class Bully implements Runnable {

	public static int TIMEOUT=1000;
	bProcess me;
	bProcess coordinator;
	HashSet<bProcess> procGroup;
	boolean electionActive;
	
	Bully(String localFile, int myIndex) throws Exception{
		procGroup=new HashSet<bProcess>();
		loadFromFile(localFile,myIndex);
		electionActive=false;
		System.out.println("Loaded " + procGroup.size() + " processes..");
		System.out.println("I_AM: " + me);
		new Thread(this).start(); //listening others msg
		takeElection();
	}
	
	void loadFromFile(String fileName,int myIndex) throws FileNotFoundException,IOException{
		BufferedReader br=new BufferedReader(new FileReader(fileName));
		String line;
		String[] parts;
		int i=0;
		
		while((line=br.readLine())!=null){
			parts=line.split(" ");
			if(parts.length>=4){
				int pid=Integer.parseInt(parts[0]);
				String procName=parts[1];
				int prio=Integer.parseInt(parts[2]);
				String site=parts[3];
				bProcess current=new bProcess(pid,procName,prio,site);
				procGroup.add(current);
				i++;
				if(myIndex==i){ //this entry is own
					me=current;
				}
			}
		}
		br.close();
	}
	
	public void takeElection(){
		Iterator<bProcess> it=procGroup.iterator();
		bProcess b;
		Boolean iamWinner=true;
		String recvd;
		System.out.println("ELECTION started..");
		electionActive=true;
		while(it.hasNext()){
			b=it.next();
			if(b.priority>me.priority){
				recvd=b.send(me.pid,"ELECTION");
				
				System.out.println(b + " : " +recvd);
				
				if(recvd.equals("NO_REPLY")){
					//No reply, process is inactive
				}else if(recvd.equals("ALIVE")){
					iamWinner=false;
					break; //No meaning in sending others
				}
			}
		}//while
		
		if(iamWinner==true){
			setCoordinator(me);
			announce("COORDINATOR");
		}
	}
	
	public void setCoordinator(bProcess c){
		if(c!=null){
			coordinator=c;
			System.out.println("(New coordinator process " + coordinator + ")");
			electionActive=false;
		}
	}
	
	void announce(String msg){
		Iterator<bProcess> it=procGroup.iterator();
		bProcess b;
		while(it.hasNext()){
			b=it.next();
			if(b.pid!=me.pid){
				b.send(me.pid,msg);
			}
		}//while
	}
	
	bProcess locatebProcess(int pid){
		Iterator<bProcess> it=procGroup.iterator();
		bProcess b;
		while(it.hasNext()){
			b=it.next();
			if(b.pid==pid)
				return b;
		}//while
		
		return null;
	}
	
	public void pollCoordinator(){
		String recvd="NO_REPLY";
		System.out.println("(This process " + me  + " want to enter critical section)");
		if(electionActive==true){
			System.out.println("Currently Election is being held. Wait for some time.");
			return;
		}
		
		if(coordinator!=null){
			recvd=coordinator.send(me.pid,"CRTC");
			System.out.println(coordinator + " : " + recvd);
		}
		
		if(recvd.equals("NO_REPLY")){
			//No Coordinator
			System.out.println("(Coordinator is DOWN, electing a new cordinator)");
			takeElection();
		}
	}
	
	public void run(){
		String msg;
		bProcess sender;
		Socket s;
		try{
			ServerSocket srv=new ServerSocket(me.getPort());
			System.out.println("Listening to other process started..");
			while(true){
				s=srv.accept();
				DataInputStream is=new DataInputStream(s.getInputStream());
				DataOutputStream os=new DataOutputStream(s.getOutputStream());
				
				int pid=is.readInt();
				sender=locatebProcess(pid);
				msg=is.readUTF();
				
				System.out.println(sender + " : " + msg);
				
				if(msg.equals("ELECTION")){
					os.writeUTF("ALIVE");
					takeElection();
				}else if(msg.equals("COORDINATOR")){
					setCoordinator(sender);
					os.writeUTF("OK");
				}else if(msg.equals("CRTC")){
					if(coordinator.pid==me.pid){
						os.writeUTF("Critical section entry Allowed");
					}else{
						os.writeUTF("I am NOT Coordinator");
					}
				}else{
					os.writeUTF("UNKNOWN COMMAND");
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//srv.close();
	}
	
	public static void main(String args[]) throws Exception{
		if(args.length<2){
			System.out.println("Syntax : java Bully <ProcessList.txt> <Index>");
			return;
		}
		
		Bully b=new Bully(args[0],Integer.parseInt(args[1]));
		new CommandListener(b);
	}
}

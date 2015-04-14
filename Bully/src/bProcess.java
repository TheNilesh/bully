import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


public class bProcess {
	int pid;
	String procName;
	int priority;
	String site;
	int port;
	
	public bProcess(int pid, String procName, int prio, String site) {
		this.pid=pid;
		this.procName=procName;
		this.priority=prio;
		this.site=site.split(":")[0];
		this.port=Integer.parseInt(site.split(":")[1]);
	}
	
	public String toString(){
		return procName;
	}
	
	public int getPort(){
		return port;
	}
	
	public String send(int senderPid,String msg){
		Socket s=new Socket();
		String recvd="NO_REPLY";
		try {
			s.connect(new InetSocketAddress(site,port),Bully.TIMEOUT);
		
			DataInputStream is=new DataInputStream(s.getInputStream());
			DataOutputStream os=new DataOutputStream(s.getOutputStream());
			
			os.writeInt(senderPid);
			os.writeUTF(msg);
			
			recvd=is.readUTF();
			s.close();
		} catch (IOException e) {
			//e.printStackTrace();
			//System.out.println("Process " + pid + " is inactive");
		}
		return recvd;
	}
}

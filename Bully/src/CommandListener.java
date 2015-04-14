import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CommandListener implements Runnable {
	Bully b;
	
	CommandListener(Bully b){
		this.b=b;
		new Thread(this).start();
	}
	
	public void run(){
		 BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	      String cmd = null;
	      try {
	    	  System.out.println("Command line interface active..");
	    	 while(true){
	    		 cmd = br.readLine();
	    		 if(cmd.equalsIgnoreCase("CRTC")){
	    			 b.pollCoordinator();
	    		 }else{
	    			 System.out.println("Response:" + cmd.toUpperCase());
	    		 }
	    	 }
	      } catch (IOException ioe) {
	         System.out.println("IO error");
	      }
	     
	}
}

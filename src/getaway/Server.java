package getaway;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	private static int uniqueId;
	private ArrayList<ClientThread> clientList;
	private ServerGUI serverGUI;
	private SimpleDateFormat dateFormat;
	private int port;
	private boolean keepGoing;
	
	public Server(int port) {
	    this(port, null);
	}
	public Server(int port, ServerGUI serverGUI) {
	    this.serverGUI = serverGUI;
	    this.port = port;
	    dateFormat = new SimpleDateFormat("HH:mm:ss");
	    clientList = new ArrayList<ClientThread>();
	}
	public void start() {
	    keepGoing = true;
	    try
	    {
	        ServerSocket serverSocket = new ServerSocket(port);
	        while(keepGoing)
	        {
	            display("Server waiting for Clients on port " + port + ".");
	            Socket socket = serverSocket.accept();
	            if(!keepGoing)
	                break;
	            ClientThread t = new ClientThread(socket);
	            clientList.add(t);                                  
	            t.start();
	        }
	        try {
	            serverSocket.close();
	            for(int i = 0; i < clientList.size(); ++i) {
	                ClientThread clientThread = clientList.get(i);
	                try {
	                clientThread.sInput.close();
	                clientThread.sOutput.close();
	                clientThread.socket.close();
	                }
	                catch(IOException ioE) {
	                }
	            }
	        }
	        catch(Exception e) {
	            display("Exception closing the server and clients: " + e);
	        }
	    }
	    catch (IOException e) {
	        String msg = dateFormat.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
	        display(msg);
	    }
	}      
	
	@SuppressWarnings("resource")
	protected void stop() {
	    keepGoing = false;
	
	    try {
	        new Socket("localhost", port);
	    }
	    catch(Exception e) {
	    }
	}
	
	private void display(String msg) {
	    String time = dateFormat.format(new Date()) + " " + msg;
	    serverGUI.appendEvent(time + "\n");
	}
	
	private synchronized void broadcast(String message) {
	    String time = dateFormat.format(new Date());
	    String messageLf = time + " " + message + "\n";
	    serverGUI.appendRoom(messageLf);    
	
	    for(int i = clientList.size(); --i >= 0;) {
	        ClientThread ct = clientList.get(i);
	        if(!ct.writeMsg(messageLf)) {
	        	clientList.remove(i);
	            display("Disconnected Client " + ct.username + " removed from list.");
	        }
	    }
	}
	
	private synchronized void remove(int id) {
	    for(int i = 0; i < clientList.size(); ++i) {
	        ClientThread ct = clientList.get(i);
	        if(ct.id == id) {
	        	clientList.remove(i);
	            return;
	        }
	    }
	}
	 
	public static void main(String[] args) {
	    int portNumber = 1500;
	    Server server = new Server(portNumber);
	    server.start();
	}
	
	class ClientThread extends Thread {
	    Socket socket;
	    ObjectInputStream sInput;
	    ObjectOutputStream sOutput;
	    int id;
	    String username;
	    Message chatMessage;
	    String date;
	
	    ClientThread(Socket socket) {
	        id = ++uniqueId;
	        this.socket = socket;	        
	        try
	        {
	            sOutput = new ObjectOutputStream(socket.getOutputStream());
	            sInput  = new ObjectInputStream(socket.getInputStream());
	            username = (String) sInput.readObject();
	            display(username + " just connected.");
	        }
	        catch (IOException e) {
	            display("Exception creating new Input/output Streams: " + e);
	            return;
	        }
	        catch (ClassNotFoundException e) {
	        }
	        date = new Date().toString() + "\n";
	    }
	
	    public void run() {
	        boolean keepGoing = true;
	        while(keepGoing) {
	            try {
	                chatMessage = (Message) sInput.readObject();
	            }
	            catch (IOException e) {
	                display(username + " Exception reading Streams: " + e);
	                break;             
	            }
	            catch(ClassNotFoundException e2) {
	                break;
	            }
	            String message = chatMessage.getMessage();
	            switch(chatMessage.getType()) {
	
	            case Message.CHATMESSAGE:
	                broadcast(username + ": " + message);
	                break;
	            case Message.LOGOUT:
	                display(username + " disconnected with a LOGOUT message.");
	                keepGoing = false;
	                break;
	            case Message.WHOISIN:
	                writeMsg("List of the users connected at " + dateFormat.format(new Date()) + "\n");
	                for(int i = 0; i < clientList.size(); ++i) {
	                    ClientThread ct = clientList.get(i);
	                    writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
	                }
	                break;
	            }
	        }
	        remove(id);
	        close();
	    }
	
	    private void close() {
	        try {
	            if(sOutput != null) sOutput.close();
	        }
	        catch(Exception e) {}
	        try {
	            if(sInput != null) sInput.close();
	        }
	        catch(Exception e) {};
	        try {
	            if(socket != null) socket.close();
	        }
	        catch (Exception e) {}
	    }
	
	
	     private boolean writeMsg(String msg) {
	         if(!socket.isConnected()) {
	             close();
	             return false;
	         }
	         try {
	             sOutput.writeObject(msg);
	         }
	         catch(IOException e) {
	
	             display("Error sending message to " + username);
	             display(e.toString());
	         }
	         return true;
	     }
	
	 }
}
package getaway;

import java.net.*;
import java.io.*;

public class Client {
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
    private Socket socket;
    private ClientGUI clientGUI;
    private String server, username;
    private int port;

    Client(String server, int port, String username) {
        this(server, port, username, null);
    }
    
    Client(String server, int port, String username, ClientGUI clientGUI) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.clientGUI = clientGUI;
    }
     
    public boolean start() {
        try {
            socket = new Socket(server, port);
        }
        catch(Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }
        
        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);
        
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }
        new ListenFromServer().start();
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        return true;
    }
 
    private void display(String msg) {
        clientGUI.append(msg + "\n");  
    }

    void sendMessage(Message msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            display("Exception writing to server: " + e);
        }
    }
 
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {} 
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {} 
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {} 
         
        if(clientGUI != null)
            clientGUI.connectionFailed();
             
    }

     
    class ListenFromServer extends Thread {
        public void run() {
            while(true) {
                try {
                    String msg = (String) sInput.readObject();
                    if(clientGUI == null) {
                        System.out.println(msg);
                        System.out.print("> ");
                    }
                    else {
                        clientGUI.append(msg);
                    }
                }
                catch(IOException e) {
                    display("Server has close the connection: " + e);
                    if(clientGUI != null)
                        clientGUI.connectionFailed();
                    break;
                }
                catch(ClassNotFoundException e2) {
                }
            }
        }
    }
}


package getaway;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class ClientGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField textField;
	private JTextField tfServer, tfPort;
	private JButton btnLogin, btnLogout, btnWhoIsOnline, btnCreateGame, btnJoinGame, btnPrivateChat;
	private JTextArea textArea;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;
	
	 ClientGUI(String host, int port) {
	
     super("Chat Client");
	 defaultPort = port;
	 defaultHost = host;
	  
	 JPanel northPanel = new JPanel(new GridLayout(3,1));
	 JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
	 tfServer = new JTextField(host);
	 tfPort = new JTextField("" + port);
	 tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
	
	 serverAndPort.add(new JLabel("Server Address:  "));
	 serverAndPort.add(tfServer);
	 serverAndPort.add(new JLabel("Port Number:  "));
	 serverAndPort.add(tfPort);
	 serverAndPort.add(new JLabel(""));
	 northPanel.add(serverAndPort);
	
	 label = new JLabel("Enter your username below", SwingConstants.CENTER);
	 northPanel.add(label);
	 textField = new JTextField("Anonymous");
	 textField.setBackground(Color.WHITE);
	 northPanel.add(textField);
	 getContentPane().add(northPanel, BorderLayout.NORTH);
	
	 textArea = new JTextArea("Welcome to the Chat room\n", 80, 80);
	 JPanel centerPanel = new JPanel(new GridLayout(1,1));
	 centerPanel.add(new JScrollPane(textArea));
	 textArea.setEditable(false);
	 getContentPane().add(centerPanel, BorderLayout.CENTER);
	
	 btnLogin = new JButton("Login");
	 btnLogin.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 onClickLogin();
		 }
	 });
	 btnLogout = new JButton("Logout");	 
	 btnLogout.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 onClickLogout();
		 }
	 });
	 btnLogout.setEnabled(false); 
	 
	 btnWhoIsOnline = new JButton("Who is in");
     btnWhoIsOnline.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 onClickWhoIsOnline();
		 }
	 });
     btnWhoIsOnline.setEnabled(false);  
     
     btnCreateGame = new JButton("Create Game");
     btnCreateGame.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 onClickCreateGame();
		 }
	 });
     btnCreateGame.setEnabled(false);
     
     btnJoinGame = new JButton("Join Game");
     btnJoinGame.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 onClickJoinGame();
		 }
	 });
     btnJoinGame.setEnabled(false);
     
     btnPrivateChat = new JButton("Private Chat");
     btnPrivateChat.addActionListener(new ActionListener() {
		 public void actionPerformed(ActionEvent e) {
			 onClickPrivateChat();
		 }
	 });
     btnPrivateChat.setEnabled(false);

     JPanel southPanel = new JPanel();
     southPanel.setLayout(new GridLayout(0, 5, 0, 0));
     southPanel.add(btnLogin);
     southPanel.add(btnLogout);
     southPanel.add(btnWhoIsOnline);
     southPanel.add(btnCreateGame);
     southPanel.add(btnJoinGame);
     southPanel.add(btnPrivateChat);
     getContentPane().add(southPanel, BorderLayout.SOUTH);
     
     setDefaultCloseOperation(EXIT_ON_CLOSE);
     setSize(510, 602);
     setVisible(true);
     textField.requestFocus();
	
	 }
	

	void append(String str) {
	     textArea.append(str);
	     textArea.setCaretPosition(textArea.getText().length() - 1);
	 }
	 void connectionFailed() {
	     btnLogin.setEnabled(true);
	     btnLogout.setEnabled(false);
	     btnWhoIsOnline.setEnabled(false);
	     label.setText("Enter your username below");
		 textField.setText("Anonymous");
		 tfPort.setText("" + defaultPort);
	     tfServer.setText(defaultHost);
	     tfServer.setEditable(false);
	     tfPort.setEditable(false);
	     textField.removeActionListener(this);
	     connected = false;
	 }
	 private void onClickLogin() {
	     String username = textField.getText().trim();
	     if(username.length() == 0)
	         return;
	     String server = tfServer.getText().trim();
	     if(server.length() == 0)
	         return;
	     String portNumber = tfPort.getText().trim();
	     if(portNumber.length() == 0)
	         return;
	     int port = 0;
	     try {
	         port = Integer.parseInt(portNumber);
	     }
	     catch(Exception en) {
	         return;   
	     }
	
	     client = new Client(server, port, username, this);
	     if(!client.start())
	         return;
	     textField.setText("");
	     label.setText("Enter your message below");
         connected = true;
	          
         btnLogin.setEnabled(false);
         btnLogout.setEnabled(true);
         btnWhoIsOnline.setEnabled(true);
         btnJoinGame.setEnabled(true);
         btnCreateGame.setEnabled(true);
         tfServer.setEditable(false);
         tfPort.setEditable(false);
         textField.addActionListener(this);
	 }
	 
	 private void onClickLogout() {
		 client.sendMessage(new Message(Message.LOGOUT, ""));		 
	 }
	 
	 private void onClickWhoIsOnline () {
		  client.sendMessage(new Message(Message.WHOISIN, ""));              
	 }
	 
	 private void onClickJoinGame() {
		 
	 }
	 
	 private void onClickCreateGame() {
		 new GameRoom();
	 }
	 
	 private void onClickPrivateChat () {
		 
	 }
	 
	 public void actionPerformed(ActionEvent e) {
		 if(connected) {
		     client.sendMessage(new Message(Message.CHATMESSAGE, textField.getText()));            
		     textField.setText("");
		     return;
		 }
	 }
	
	 public static void main(String[] args) {
	     new ClientGUI("localhost", 1500);
	 }

}
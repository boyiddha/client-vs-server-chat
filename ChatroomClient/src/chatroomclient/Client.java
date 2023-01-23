package chatroomclient;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Client extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;
    
    //constructor
    public Client(String host){
        super("Client User!");
        serverIP = host;
        userText = new JTextField("Write Here....");
        userText.setEditable(false);
        userText.setFont(new Font("Verdana", Font.PLAIN, 20));
        userText.setBorder(new LineBorder(Color.RED, 2));
        userText.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
            }        
        );
        add(userText,BorderLayout.SOUTH);
        chatWindow = new JTextArea();
        Font font = new Font("Verdana", Font.PLAIN, 20);
        chatWindow.setFont(font);
        chatWindow.setForeground(Color.BLACK);
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(400,500);
        setVisible(true);
        setLocation(550,50);
    }
    
    //connect to server
    public void startRunning(){
        try{
           connectToServer();
           setupStreams();
           whileChatting();
        }catch(EOFException eofException){
            showMessage("\n Client terminated connection");
        }catch(IOException ioException){
            ioException.printStackTrace();
        }finally{
            closeCrap();
        }
    }
    
    //connect to Server
    private void connectToServer() throws IOException{
        showMessage("Attempting connection....\n");
        //client send request at ip=127.0.0.1 in tcp port no. 568
        connection = new Socket(InetAddress.getByName(serverIP),678);//create new Socket("127.0.0.1",678);//Localhost ip , TCP port(0 to 65535)
        showMessage("Connected to :" +connection.getInetAddress().getHostName());
    }
    
    //set up streams to send and receive message
    private void setupStreams() throws IOException{
        //create I/O stream for communication to the server
        output = new ObjectOutputStream(connection.getOutputStream());
        // sends output to the socket
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        // takes input from the server socket
        showMessage("\n your streams are now good to go!\n");
    }
    
    //while chatting with server
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();//receive data from server
                showMessage("\n" +message);
            }catch(ClassNotFoundException  classNotFoundException){
                showMessage("\n I don't know that object type");
            }
        }while(!message.equals("SERVER -> END"));
    }
    
    //Close the streams and sockets
    private void closeCrap(){
        showMessage("\n closing down....");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();//close socket
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    // send message to server
    private void sendMessage(String message){
        try{
            output.writeObject("CLIENT -> " +message);//send data to the server
            output.flush();
            showMessage("\nCLIENT - " +message);
        }catch(IOException ioException){
            chatWindow.append("\n something messed up sending message host!");
        }
    }
    
    //change/update chatWindow
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    chatWindow.append(m);
                }
            }
        );
    }
    
    // gives user permission to type crap into the text box
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    userText.setEditable(tof);
                }
            }
        );
    }
}

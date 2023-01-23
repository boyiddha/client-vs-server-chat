package chatroomserver;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Server extends JFrame{
    
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;
    private Socket connection;
    //The Socket class is used to communicate client and server. Through this class, 
    //we can read and write message. The ServerSocket class is used at server-side.
    //The accept() method of ServerSocket classestablishes connection and waits for the client

    // constructor
    public Server(){
        super("  SERVER  ");
        userText = new JTextField();
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
        add(new JScrollPane(chatWindow),BorderLayout.CENTER);
        setSize(400,500);
        setVisible(true);
        setLocation(50,50);
        
    }
    
    //set up and run the server
    public void startRunning(){
        try{
            server = new ServerSocket(678); //open the server socket at port 568
            //Server application makes a ServerSocket on a specific port which is 568.
            //This starts our Server listening for client requests coming in for port 568.
            //Then Server makes a new Socket to communicate with the client.=> connection = server.accept();
            while(true){
                try{
                    //connect and have conversation
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }catch(EOFException eofException){
                    showMessage("\nServer ended the connection");
                }finally{
                    closeCrap();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
    
    // wait for connection , then display connection information
    private void waitForConnection()throws IOException{
        showMessage("Waiting for someone to connection....\n");
        //Server makes a new Socket to communicate with the client.
        // accept (server) connection which already create
        connection = server.accept();//wait for the client request   
        showMessage("Now connected to "+connection.getInetAddress().getHostName());
    }
    
    // get stream to send and receive data
    private void setupStreams()throws IOException{
        //create I/O stream for communication to the client
        output = new ObjectOutputStream(connection.getOutputStream());
        // sends output to the socket
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        // takes input from the client socket
        showMessage("\nStream are now setup!\n");
    }
    
    //during the chat conversation
    private void whileChatting()throws IOException{
        String message = "You are now connected !";
        sendMessage(message);
        ableToType(true);
        do{
            //have a conversation
            try{
                message = (String) input.readObject();//receive data from client
                showMessage("\n" + message);
            }catch(ClassNotFoundException classNotFoundException){
                showMessage("\n ERROR!");
            }
        }while(!message.equals("CLIENT -> END"));
    }
     
    //close streams and sockets after you are done chatting
    private void closeCrap(){
        showMessage("\n Closing connection.....\n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();//close socket
        }catch(IOException ioException){
            ioException.printStackTrace();
        }
    } 
    
    //send a message to client
    private void sendMessage(String message){
        try{
            output.writeObject("SERVER -> " +message);//send data to client
            output.flush();
            showMessage("\nSERVER - " +message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR : This message is not send ! Try again ");
        }
    }
    
    //updates chatWindow
    private void showMessage(final String text){
        SwingUtilities.invokeLater(
            new Runnable(){
                public void run(){
                    chatWindow.append(text);
                }
            }        
        );
    }
    
    ///let the user type stuff into their box or means giving the user permission to type
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



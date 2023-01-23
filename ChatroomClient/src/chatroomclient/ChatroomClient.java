package chatroomclient;

import javax.swing.JFrame;
public class ChatroomClient {

    public static void main(String[] args) {
       Client liton;
       liton =  new Client ("127.0.0.1");// client in socket programming must know two things (i) ip address of server & (ii) port number
       liton.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       liton.startRunning();        
    }
    
}

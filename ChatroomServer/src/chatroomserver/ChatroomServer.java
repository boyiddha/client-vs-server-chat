
package chatroomserver;

import javax.swing.JFrame;
public class ChatroomServer {

  
    public static void main(String[] args) {
        Server myPC = new Server();
        myPC.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myPC.startRunning();
    }
    
}

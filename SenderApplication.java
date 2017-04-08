import java.util.*;

/**
 * A class which represents the sender's application. the sendMessage will be called at random times.
 */
public class SenderApplication
{
    private SenderTransport st; //transport layer used
    private ArrayList<String> messages; //all messages the application will send
    private int index; //how many messages has the application sent so far
    private Timeline tl; //the timeline associated with the simulation
    
    /**
     * The sender application simply controls SenderTransport, telling it when to send a new message
    */
    public SenderApplication(ArrayList<String> messages, NetworkLayer nl)
    {
        st = new SenderTransport(nl);
        this.messages=messages;
        index=0;    
        
        
    }
    
    public SenderTransport getSenderTransport()
    {
        return st;
    }
    
    
    public void sendMessage()
    {
        if(NetworkSimulator.DEBUG > 0){
            System.out.println(" Sender Application sending new message");
        }
        //The index <= messages.size()-1 is used to make sure that the program does not access an out of bounds index in the ArrayList
        if(st.checkWindow(index) && index <= messages.size()-1){ //Only send messages if the current message being sent is within the window
            System.out.println("Current index: " + index);
            st.sendMessage(new Message(messages.get(index++))); //Send the next available message
        }
        else{
            if(NetworkSimulator.DEBUG > 0){
                System.out.println("Unable to send a message due to window size constraint");
            }
        }
    }
    

    
    

}

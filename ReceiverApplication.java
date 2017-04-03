
/**
 * A class which represents the receiver's application. It simply prints out the message received from the tranport layer.
 */
public class ReceiverApplication
{
    public void receiveMessage(Message msg)
    {
        if(Config.debug > 0){
            System.out.println("from receiver:" + msg.getMessage());
        }
    }

}


import java.util.ArrayList;
/**
 * A class which represents the receiver transport layer.
 * Should bundle messages received from the network simulation into packets that are sent via the network layer
 */
public class SenderTransport
{
    private NetworkLayer nl;
    private Timeline tl;
    private int n; //used for window size 
    private boolean usingTCP;
    private int seqNum = 0;
    private int ackNum = 0;

    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();

    }
    
    public void initialize()
    {
        //Set timeline used to the timeline located in the network Layer being accessed
        setTimeLine(nl.tl); 
    }

    public void sendMessage(Message msg)
    {
        if(Config.debug > 0){
            System.out.println("  Sender transport is now sending message w/ text: " + msg.getMessage());
        }
        if(usingTCP){
            
        }
        else{
            //Remember that the constructor for the packet is (message, seqnum, acknum, checksum)
            
            Packet pkt = new Packet(msg, seqNum, ackNum, 0); // Checksum needs to be implemented.
            seqNum++;
            ackNum++;
            nl.sendPacket(pkt, 1);
            
        }
    }

    public void receiveMessage(Packet pkt)
    {
        if(Config.debug > 0){
            System.out.println("  Sender Transport is now receiving packet w/ msg text: " + pkt.getMessage().getMessage());
        }
    }

    public void timerExpired()
    { 
    }

    public void setTimeLine(Timeline tl)
    {
        this.tl=tl;
    }

    public void setWindowSize(int n)
    {
        this.n=n;
    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}

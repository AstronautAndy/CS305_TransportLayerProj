
/**
 * A class which represents the receiver transport layer
 */
public class ReceiverTransport
{
    private ReceiverApplication ra;
    private NetworkLayer nl;
    private boolean usingTCP;
    private int seqNum;
    private int ackNum;

    public ReceiverTransport(NetworkLayer nl){
        ra = new ReceiverApplication();
        this.nl=nl;
        initialize();
    }

    public void initialize()
    {
        seqNum = 0;
        ackNum = 0;
    }
    
    /*
     * This function is used to receive pkts from the sending side of the application. Does "stuff" with the 
     */
    public void receiveMessage(Packet pkt)
    {
        if(NetworkSimulator.DEBUG > 0){
            System.out.println("   Receiver Transport receiving packet with message: " + pkt.getMessage().getMessage());
        }
        
        // Send message to receiver application.
        ra.receiveMessage(pkt.getMessage());
        
        // Send packet with ack back to the sender.
        Packet ackPkt = new Packet(pkt.getMessage(), pkt.getSeqnum(), pkt.getAcknum(), 0);
        // Check for corruption.
        if (pkt.isCorrupt()) {
            System.out.println(" Packet received from sender was corrupted.");
        } else {
            System.out.println(" Packet received from sender was not corrupted.");
            ackPkt.setChecksum();
        }
        seqNum++;
        ackNum++;
        nl.sendPacket(ackPkt, 0);
        
        
    }

    public void setProtocol(int n)
    {
        if(n>0)
            usingTCP=true;
        else
            usingTCP=false;
    }

}

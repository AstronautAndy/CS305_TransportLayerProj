
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
    private int expected;
    private int mostRecentAck; //Used to denote the seq number of the most recently ack'd

    public ReceiverTransport(NetworkLayer nl){
        ra = new ReceiverApplication();
        this.nl=nl;
        initialize();
    }

    public void initialize()
    {
        seqNum = 0;
        ackNum = 0;
        mostRecentAck = 0;
        expected = 0;
    }
    
    /*
     * This function is used to receive pkts from the sending side of the application. Does "stuff" with the 
     * If the GBN protocol recives n packets that have seq numbers greater than the most recently ack'd port, then send n packets through the most recently ack's port
     * TCP simply needs to send acks to packets as it receives them
     */
    public void receiveMessage(Packet pkt)
    {
        if(NetworkSimulator.DEBUG > 0){
            System.out.println("   Receiver Transport receiving packet with message: " + pkt.getMessage().getMessage() + " and seqNum: " +  pkt.getSeqnum());
            System.out.println("Expected: " + expected);
        }
        // Send message to receiver application.
        ra.receiveMessage(pkt.getMessage());
        //Packet ackPkt = new Packet(pkt.getMessage(), pkt.getSeqnum(), pkt.getAcknum(), 0); //Default code is to create a standard ack packet
        Packet ackPkt;
        //This block of code is used to change how the program responds to an out of order packet from the sender
        if(usingTCP){
            
            if(mostRecentAck+1 != pkt.getSeqnum() ){ //If we receive an out of order packet from the sender
                
            }else{//Otherwise, we receive an in order packet
                
            }
            ackPkt = new Packet(pkt.getMessage(), pkt.getSeqnum(), pkt.getAcknum(), 0);
        }else{ //Using GBN
            if( expected < pkt.getSeqnum() ){ //Out of order case
                System.out.println("Out of order packet");
                if(expected == 0){ //Initial case
                    ackPkt = new Packet(pkt.getMessage(), 0, 0, 0);
                }else{ //General case
                    ackPkt = new Packet(pkt.getMessage(), expected-1, expected-1, 0);
                }
            }else{ //Correct case
                ackPkt = new Packet(pkt.getMessage(), pkt.getSeqnum(), pkt.getAcknum(), 0); //Default case
                expected++;
            }
        }
        
        // Check for corruption.
        if (pkt.isCorrupt()) {
            System.out.println(" Packet received from sender was corrupted.");
        } else { //Not corrupt
            System.out.println(" Packet received from sender was not corrupted.");
            ackPkt.setChecksum();
        }
        
        //These two don't seem to do anything at the moment
        seqNum++;
        ackNum++;
        // Send packet with ack back to the sender.
        System.out.println("Receiver Sending ack packet with ack: " + ackPkt.getAcknum());
        mostRecentAck = ackPkt.getAcknum();
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


import java.util.ArrayList;
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
    private int senderLastAck;
    ArrayList<Integer> receivedPackets;

    public ReceiverTransport(NetworkLayer nl){
        ra = new ReceiverApplication();
        this.nl=nl;
        initialize();
    }

    public void initialize()
    {
        seqNum = 0;
        ackNum = 0;
        expected = 0;
        senderLastAck = 0;
        receivedPackets = new ArrayList<Integer>();
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
        // Update the packet last acked on sender's end
        if (pkt.getAcknum() > senderLastAck) {
            senderLastAck = pkt.getAcknum();
        }
        //This block of code is used to change how the program responds to an out of order packet from the sender
        if( expected < pkt.getSeqnum() ){ //Out of order case
            System.out.println("Out of order packet");
            if (usingTCP) { 
                // Buffer packets
                receivedPackets.add(pkt.getSeqnum());
            }
            if(expected == 0){ //Initial case
                ackPkt = new Packet(pkt.getMessage(), -1, -1, 0);
            }else{ //General case
                ackPkt = new Packet(pkt.getMessage(), senderLastAck, senderLastAck, 0);
            }
        }else{ //Correct case
            if (usingTCP) {
                // Check to see if the next packet was buffered already.
                for (int i = 0; i < receivedPackets.size(); i++) {
                    if (receivedPackets.get(i).intValue() == expected+1) {
                        expected++;
                        receivedPackets.remove(i);
                        i = 0;
                    }
                }
                ackPkt = new Packet(pkt.getMessage(), expected, expected, 0);
                expected++;
            } else {
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
        
        // Send packet with ack back to the sender.
        if(NetworkSimulator.DEBUG > 0){
            System.out.println("Receiver Sending ack packet with ack: " + ackPkt.getAcknum());
        }
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

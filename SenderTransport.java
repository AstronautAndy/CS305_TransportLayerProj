
import java.util.ArrayList;
/**
 * A class which represents the receiver transport layer.
 * Should bundle messages received from the network simulation into packets that are sent via the network layer
 */
public class SenderTransport
{
    private NetworkLayer nl;
    private Timeline tl;
    int base;
    int current;
    private int n; //used for window size 
    private boolean usingTCP;
    private int seqNum;
    private int ackNum;
    private int expectedAck;
    ArrayList<Packet> transBuffer;

    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();

    }
    
    public void initialize()
    {
        //Set timeline used to the timeline located in the network Layer being accessed
        setTimeLine(nl.tl);
        base=0;
        current=0;
        seqNum = 0;
        ackNum = 0;
        expectedAck = 0;
        transBuffer = new ArrayList<Packet>();
    }

    public void sendMessage(Message msg)
    {
        if(NetworkSimulator.DEBUG > 0){
            System.out.println("  Sender transport is now sending message w/ text: " + msg.getMessage());
        }
        
        //Remember that the constructor for the packet is (message, seqnum, acknum, checksum)
        Packet newPkt = new Packet(msg, seqNum,ackNum,0); //Need to figure out a more appropriate checksum and ack #
        
        //Remember that the constructor for the packet is (message, seqnum, acknum, checksum)
        // Create the packet with appropriate parameters.
        Packet pkt = new Packet(msg, seqNum, ackNum, 0);
        if(NetworkSimulator.DEBUG > 2){
            System.out.println("Packet Sent with seqNum: " + seqNum + " ackNum: " + ackNum);
        }
        // Set the checksum before sending the packet.
        pkt.setChecksum();
        nl.sendPacket(newPkt, 0); //remember that the second parameter is "to"
        // Add the packet to the buffer in case of loss.
        transBuffer.add(pkt);
        //Update all values
        seqNum++;
        ackNum++;
        current++; 
        expectedAck = base; //Set the expected ack number to the updated base number (must be the start of the window)
        // Begin timer if it isn't already on.
        tl.startTimer(60);
        nl.sendPacket(pkt, 1);
    }
    
    /**
     * When the GBN protocol receives an ACK message, it should be either for the base or for some base < n 
     */
    public void receiveMessage(Packet pkt)
    {
        if(NetworkSimulator.DEBUG > 0){
            System.out.println("  Sender Transport is now receiving packet w/ msg text: " + pkt.getMessage().getMessage());
            System.out.println("  Sender Transport is Expecting ACK: " + expectedAck);
        }
        
        // Check to make sure the expected packet is what we received.
        if (pkt.getAcknum() == expectedAck || pkt.getAcknum() > expectedAck) {
            //If you;ve received an out of order ack, do the following:
            if(pkt.getAcknum() > expectedAck){ expectedAck = pkt.getAcknum()+1; base = expectedAck; } //If you receive an ack message out of order, update the base
            
            System.out.println(" Received expected packet.");
            // Remove packet from buffer.
            transBuffer.remove(0);
            // Kill the timer if we receive an ack for the expected packet.
            tl.stopTimer();
            expectedAck++;
                if(usingTCP){
                    
                }else{ //Using GBN
                    System.out.println("Current base: " + base);
                    base++;
                }
        } else {
            System.out.println(" Received unexpected packet.");
        }
        
        // Verify integrity of the data.
        if (pkt.isCorrupt()) {
            System.out.println(" Packet received from receiver was corrupted.");
        } else {
            System.out.println(" Packet received from receiver was not corrupted.");
        }
    }

    public void timerExpired()
    { 
        // Timer expired so handle retransmission.
        // Restart timer.
        if(usingTCP){
            //TCP resends only the packet (identified by sequence #) that has gone without a received ACK
        }else{ //Using GBN
            //GBN resend all packets
        }
        tl.startTimer(60);
        // Resend the first packet from the buffer.
        nl.sendPacket(transBuffer.get(0), 1);
    }
    
    public boolean checkWindow(int i){
        int range = base + n;
        System.out.println("base+n " + range);
        if(i <= base+n){ //If the index of the message you want to send is greater than or equal to the base + window size
            return true; 
        }
        else{
            return false;
        }
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

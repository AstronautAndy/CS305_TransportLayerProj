
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
    private int dupAcks;
    ArrayList<Packet> windowBuffer;
    ArrayList<Packet> overflowBuffer;

    public SenderTransport(NetworkLayer nl){
        this.nl=nl;
        initialize();

    }
    
    public void initialize()
    {
        //Set timeline used to the timeline located in the network Layer being accessed
        setTimeLine(nl.tl);
        seqNum = 0;
        ackNum = 0;
        expectedAck = 0;
        dupAcks = 0;
        windowBuffer = new ArrayList<Packet>();
        overflowBuffer = new ArrayList<Packet>();
    }

    public void sendMessage(Message msg)
    {
        if(NetworkSimulator.DEBUG > 0){
            System.out.println("  Sender transport is now sending message w/ text: " + msg.getMessage());
        }
        
        // Sending a packet from the application layer.
    
        // Remember that the constructor for the packet is (message, seqnum, acknum, checksum)
        // Create the packet with appropriate parameters.
        Packet pkt = new Packet(msg, seqNum, expectedAck-1, 0);
        // Set the checksum before sending the packet.
        pkt.setChecksum();
        
        // Making sure we are within window size
        int range = expectedAck + n - 1;
        System.out.println("expectedAck + n -1: " + range);
        
        if(seqNum <= range && seqNum >= range + 1 - n){
            // Within window so can send packet
            
            // Add the packet to the buffer in case of loss.
            windowBuffer.add(pkt);
            
            if(NetworkSimulator.DEBUG > 2){
                System.out.println("Packet Sent with seqNum: " + seqNum + " ackNum: " + ackNum);
            }
            
            // Begin timer if it isn't already on.
            tl.startTimer(60);
            nl.sendPacket(pkt, 1);
        }
        else{
            // Not in the window
            
            if(NetworkSimulator.DEBUG > 0){
                System.out.println("Unable to send a message due to window size constraint");
            }
            
            overflowBuffer.add(pkt);
        }
        
        // Update all values
        seqNum++;
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
            
            if(pkt.getAcknum() > expectedAck){
                //If you've received an out of order ack:
                System.out.println(" Received cumulative ack packet.");
                
                // Remove packets from buffer.
                for (int i = expectedAck; i <= pkt.getAcknum(); i++) {
                    if (windowBuffer.size() > 0) {
                        windowBuffer.remove(0);
                    }
                }
                
                //If you receive an ack message out of order, update the expected ack number
                expectedAck = pkt.getAcknum();
                
                // Kill the timer if we receive an ack.
                tl.stopTimer();
                if (windowBuffer.size() > 0) {
                    tl.startTimer(60);
                }
                
            } else {
                System.out.println(" Received expected packet.");
                
                // Remove packets from buffer.
                windowBuffer.remove(0);
                
                // Kill the timer if we receive an ack.
                tl.stopTimer();
                if (windowBuffer.size() > 0) {
                    tl.startTimer(60);
                }
            }
            
            System.out.println("Current expected ack: " + expectedAck);
            expectedAck++;
            
            // Check to see if there are packets in overflow to be sent
            for (int i = windowBuffer.size(); i < n; i++) {
                if (overflowBuffer.size() > 0) {
                    // Begin timer if it isn't already on.
                    tl.startTimer(60);
                    // Sending a packet from overflow.
                    System.out.println("  packet: " + overflowBuffer.get(0).getMessage().getMessage());
                    nl.sendPacket(overflowBuffer.get(0), 1);
                    windowBuffer.add(overflowBuffer.get(0));
                    overflowBuffer.remove(0);
                }
            }
            
            dupAcks = 0;
            
        } else {
            System.out.println(" Received duplicate ack.");
            if (usingTCP) {
                dupAcks++;
                if (dupAcks % 3 == 0 && dupAcks > 0) {
                    // Fast retransmit
                    System.out.println(" Fast retransmit.");
                    nl.sendPacket(windowBuffer.get(0), 1);
                }
            } 
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
            tl.startTimer(60);
            System.out.println("  packet: " + windowBuffer.get(0).getMessage().getMessage());
            nl.sendPacket(windowBuffer.get(0), 1);
        }else{ //Using GBN
            //GBN resend all packets
            for (int i = 0; i < windowBuffer.size(); i++) {
                tl.startTimer(60);
                System.out.println("  packet: " + windowBuffer.get(i).getMessage().getMessage());
                nl.sendPacket(windowBuffer.get(i), 1);
            }
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

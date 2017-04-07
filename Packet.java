import java.util.*;

/**
 * A class which represents a packet
 */
public class Packet
{
    
    private Message msg; //the enclosed message
    private int seqnum; //packets seq. number
    private int acknum; //packet ack. number
    private int checksum; //packet checksum

    Random ran; //random number generator

    public Packet(Message msg, int seqnum, int acknum, int checksum)
    {
        this.msg=msg;
        this.seqnum=seqnum;
        this.acknum=acknum;
        this.checksum=checksum;
        this.ran=new Random();
    }

    public int getAcknum()
    {
        return acknum;
    }
    
    public int getSeqnum()
    {
        return seqnum;
    }

    public Message getMessage()
    {
        return msg;
    }
    
    public void setChecksum()
    {
        // Add seq plus ack to begin.
        checksum = seqnum + acknum;
        // Add each character in the message to complete the checksum.
        for (int i = 0; i < msg.getMessage().length(); i++) {
            checksum += msg.getMessage().charAt(i);
        }
    }
    
    public boolean isCorrupt()
    {
        // Same method of computing the checksum as setChecksum this will just store it in a temp int.
        int tempChecksum = seqnum + acknum;
        for (int i = 0; i < msg.getMessage().length(); i++) {
            tempChecksum += msg.getMessage().charAt(i);
        }
        // Compare the checksum of the current data with the checksum calculated at the beginning.
        if (checksum == tempChecksum) {
            // Equal. No data was corrupted.
            return false;
        } else {
            // Not equal. Some data was corrupted.
            return true;
        }
    }
    
    /**
     * This method curropts the packet the follwing way:
     * curropt the message with a 75% chance
     * curropt the seqnum with 12.5% chance
     * curropt the ackum with 12.5% chance
     */
    public void corrupt()
    {
        if(ran.nextDouble()<0.75)
        {this.msg.corruptMessage();}
        else if(ran.nextDouble()<0.875)
        {this.seqnum=this.seqnum+1;}
        else
        {this.acknum=this.acknum+1;}

    }
    

}

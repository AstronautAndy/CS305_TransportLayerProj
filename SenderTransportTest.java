

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * The test class SenderTransportTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class SenderTransportTest
{
    Timeline tl;
    ArrayList<String> testMessages;
    NetworkLayer nl;
    SenderTransport st;
    ReceiverTransport rt;
    Message m; 
    Packet ack;
    /**
     * Default constructor for test class SenderTransportTest
     */
    public SenderTransportTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        float lp = (float) 0.1; float cp = (float) 0.1;
        testMessages = new ArrayList<String>();
        testMessages.add("One");
        testMessages.add("Two");
        testMessages.add("Three");
        testMessages.add("Four");
        m = new Message("test");
        tl = new Timeline(0,testMessages.size()); //Create a test timeline with time of 0 and 5 messages to work with
        nl = new NetworkLayer(lp,cp,tl);
        //rt = new ReceiverTransport(nl);
        st = new SenderTransport(nl);
        st.setProtocol(0); //Start with GBN
        st.setTimeLine(tl);
        st.setWindowSize(3);
        
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
    }
    
    /**
     * Send message
     * Receive Ack
     * Should increment base
     * Base is working as expected
     */
    @Test
    public void testBaseIncrement(){
        ack = new Packet(m,0,1,0); //ack for the first message from the sender
        st.sendMessage(m); //Send a message
        st.receiveMessage(ack); //Receive the ack message. Should increment the base
        System.out.println("Base: " + st.base);
        //st.sendMessage(m); //Send m again. Check the base number
        
        
    }
    
    @Test
    public void testWindowUpdate(){
        
    }
}

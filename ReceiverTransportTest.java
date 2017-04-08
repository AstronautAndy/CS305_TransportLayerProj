

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * The test class ReceiverTransportTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class ReceiverTransportTest
{
    Timeline tl;
    Packet p1; Packet p2; Packet p3; Packet p4; Packet p5; Packet p6;
    ArrayList<String> testMessages;
    NetworkLayer nl;
    Message m;
    ReceiverTransport rt;
    /**
     * Default constructor for test class ReceiverTransportTest
     */
    public ReceiverTransportTest()
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
        m = new Message("test");
        p1 = new Packet(m,0,0,0);
        p2 = new Packet(m,1,1,0);
        p3 = new Packet(m,2,2,0);
        p4 = new Packet(m,3,3,0);
        p5 = new Packet(m,4,4,0);
        p6 = new Packet(m,5,5,0);
        
        tl = new Timeline(0,5); //Create a test timeline with time of 0 and 5 messages to work with
        nl = new NetworkLayer(lp,cp,tl);
        rt = new ReceiverTransport(nl);
        rt.initialize();
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
    
    @Test
    public void testAccumulateAcks(){
        rt.receiveMessage(p1);
        rt.receiveMessage(p2);
        rt.receiveMessage(p3);
    }
    
    @Test
    public void outOfOrder(){
        rt.receiveMessage(p2);
        rt.receiveMessage(p1);
        rt.receiveMessage(p3);
    }
    
    @Test
    public void eventualOutOfOrder(){
        rt.receiveMessage(p1);
        rt.receiveMessage(p2);
        rt.receiveMessage(p3);
        rt.receiveMessage(p6);// Should be sending acks with acknum 3
        rt.receiveMessage(p5);
        rt.receiveMessage(p4);
    }
}

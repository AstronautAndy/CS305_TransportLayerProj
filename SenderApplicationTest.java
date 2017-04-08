

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * The test class SenderApplicationTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class SenderApplicationTest
{
    Timeline tl;
    ArrayList<String> testMessages;
    NetworkLayer nl;
    SenderApplication sa;
    /**
     * Default constructor for test class SenderApplicationTest
     */
    public SenderApplicationTest()
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
        tl = new Timeline(0,testMessages.size()); //Create a test timeline with time of 0 and 5 messages to work with
        nl = new NetworkLayer(lp,cp,tl);
        sa = new SenderApplication(testMessages, nl);
        sa.getSenderTransport().setWindowSize(3); //Set the window size to a test value
        sa.getSenderTransport().setProtocol(0); //Set to GBN (For now)
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
    public void testCheckWindow(){
       for(int i=0; i<5; i++){     
           sa.sendMessage();
       }
    }
}

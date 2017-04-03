
/**
 * Used to store static data that we want the program to be able to access in any class without messy 
 * constructor nonsense.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Config
{
    // instance variables - replace the example below with your own
    static int debug;

    /**
     * Constructor for objects of class Config
     */
    public Config()
    {
        
    }

    static void setDebug(int d){
        debug = d;
    }
}

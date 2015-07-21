package eu.reply.hackathon.wadro.drone;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;



public class TakeOffAndLand
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
    	 IARDrone drone = null;
    	    try
    	    {
    	        drone = new ARDrone();
    	        
    	        drone.reset();
    	        drone.start();
    	        
    	        int speed = 90; // percentage of max speed
    	        
    	        CommandManager cmd = drone.getCommandManager();
    	        
    	        cmd.takeOff().doFor(4000);
    	        		



    	        cmd.forward(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        cmd.spinRight(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        
    	        cmd.forward(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        cmd.spinRight(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        
    	        cmd.forward(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        cmd.spinRight(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        
    	        cmd.forward(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	        cmd.spinRight(speed).doFor(600);
    	        cmd.hover().doFor(500);
    	       
    	        cmd.landing();
    	    }
    	    catch (Exception exc)
    		{
    			exc.printStackTrace();
    		}
    		finally
    		{
    			if (drone != null)
    				drone.stop();
    			System.exit(0);
    		}
    }
}

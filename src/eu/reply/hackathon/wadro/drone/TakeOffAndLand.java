package eu.reply.hackathon.wadro.drone;
import org.opencv.core.RotatedRect;

import de.yadrone.apps.controlcenter.CCFrame;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
//connect to the drone WI-FI telnet 192.168.1.1
//killall udhcpd; iwconfig ath0 mode managed essid WADRO; ifconfig ath0 192.168.1.1 netmask 255.255.255.0 up;


public class TakeOffAndLand
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ARDrone drone = null;
		CommandManager cmd = null;
		try
		{
			drone = new ARDrone();

			drone.reset();
			drone.start();

			// new CCFrame(drone);

			int speed = 90; // percentage of max speed

			cmd = drone.getCommandManager();

			cmd.takeOff().doFor(4000);


			try{
				cmd.wait(500);
			}
			catch(Exception e){
				System.out.println("ERROR");
			}
			rotate90Left(cmd);
			//cmd.hover().doFor(500);


			/* cmd.forward(speed).doFor(600);
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
    	        cmd.hover().doFor(500);*/

			cmd.landing();
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		finally
		{
			if (drone != null){
				cmd.landing();
				cmd.stop();
			}
			System.exit(0);
		}
	}
	public static void rotate90Left(CommandManager cmm){
		cmm.spinLeft(90).doFor(750);
	}
}


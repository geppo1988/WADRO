package eu.reply.hackathon.wadro.drone;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
//connect to the drone WI-FI telnet 192.168.1.1
//killall udhcpd; iwconfig ath0 mode managed essid WADRO; ifconfig ath0 192.168.1.1 netmask 255.255.255.0 up;
import de.yadrone.base.command.LEDAnimation;


public class FLightManager
{
	final int speed = 60; // percentage of max speed

	public static void main( String args[]){
		FLightManager ex = new FLightManager(new ARDrone());
		ex.animateLEDs();
		ex.takeOff(4000);
		//ex.goLeft(800);
		//ex.goRight(800);
		ex.spinLeft90();
		ex.spinRight90();
		ex.land();
	}

	private IARDrone drone;

	public FLightManager(IARDrone drone)
	{
		this.drone = drone;
	}

	public void animateLEDs()
	{
		drone.getCommandManager().setLedsAnimation(LEDAnimation.BLINK_ORANGE, 3, 1);
	}
	
	public void takeOff(int millis)
	{
		drone.getCommandManager().takeOff();
		drone.getCommandManager().waitFor(millis);
	}
	public void land()
	{
		drone.getCommandManager().landing();
	}
	
	public void goRight(int millis){
		drone.getCommandManager().goRight(speed).doFor(millis);
		drone.getCommandManager().hover().doFor(200);
	}
	
	public void goLeft(int millis){
		drone.getCommandManager().goLeft(speed).doFor(millis);
		drone.getCommandManager().hover().doFor(200);
	}
	public void spinLeft90(){
		drone.getCommandManager().spinLeft(speed).doFor(650);
	}
	public void spinRight90(){
		drone.getCommandManager().spinRight(speed).doFor(650);
	}
	
	public void leftRightForwardBackward()
	{
		final CommandManager cmd = drone.getCommandManager();
			
		cmd.takeOff().doFor(2500);
		
		cmd.goLeft(speed).doFor(100);
		cmd.hover().doFor(200);
		
		cmd.goRight(speed).doFor(100);
		cmd.hover().doFor(200);
		
		cmd.forward(speed).doFor(200);
		cmd.hover().doFor(100);
		
		cmd.backward(speed).doFor(200);
		cmd.hover().doFor(200);
		
		cmd.landing();
		
		// alternative: asynchronous call
//		cmd.takeOff();
//		cmd.schedule(5000, new Runnable() { // schedule to be executed in 5 secs
//			public void run()
//			{
//				cmd.goLeft(speed);
//				// [...]
//			}			
//		});
	}
}


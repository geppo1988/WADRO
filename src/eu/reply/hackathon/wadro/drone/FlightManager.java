package eu.reply.hackathon.wadro.drone;

import org.apache.log4j.BasicConfigurator;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
//connect to the drone WI-FI telnet 192.168.1.1
//killall udhcpd; iwconfig ath0 mode managed essid WADRO; ifconfig ath0 192.168.1.1 netmask 255.255.255.0 up;
import de.yadrone.base.command.LEDAnimation;


public class FlightManager
{
	final int speed = 50; // percentage of max speed
	final int hover = 1000; // percentage of max speed

	public static void main( String args[]){
		BasicConfigurator.configure();

		ARDrone drone = new ARDrone();
		drone.reset();

		drone.start();
		new VideoListener(drone);
		
/*		FLightManager ex = new FLightManager(drone);
		ex.animateLEDs();
		
		ex.takeOff(5000);
		ex.goFoward(500);

		ex.spinLeft90();
		
		ex.goFoward(1700);

		ex.spinLeft90();

		ex.land();
		drone.stop();*/
	}

	private IARDrone drone;

	public FlightManager(IARDrone drone)
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
	
	public void goFoward(int millis){
		drone.getCommandManager().forward(speed).doFor(millis);
		drone.getCommandManager().hover().doFor(millis);
	}
	public void goBack(int millis){
		drone.getCommandManager().backward(speed).doFor(millis);
		drone.getCommandManager().hover().doFor(millis);
	}
	
	public void goRight(int millis){
		drone.getCommandManager().goRight(speed).doFor(millis);
		drone.getCommandManager().hover().doFor(millis);
	}
	
	public void goLeft(int millis){
		drone.getCommandManager().goLeft(speed).doFor(millis);
		drone.getCommandManager().hover().doFor(millis);
	}
	public void spinLeft90(){
		drone.getCommandManager().spinLeft(speed).doFor(1750);
		drone.getCommandManager().hover().doFor(1750);
	}
	public void spinRight90(){
		drone.getCommandManager().spinRight(speed).doFor(1750);
		drone.getCommandManager().hover().doFor(1750);
	}
	
	
}


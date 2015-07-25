package eu.reply.hackathon.wadro.image;

import com.codeminders.ardrone.ARDrone;

public class javaDrone {
	private static final long CONNECT_TIMEOUT = 3000;

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        ARDrone drone;
        try
        {
            // Create ARDrone object,
            // connect to drone and initialize it.
            drone = new ARDrone();
            drone.connect();
            drone.clearEmergencySignal();

            // Wait until drone is ready
            drone.waitForReady(CONNECT_TIMEOUT);

            // do TRIM operation
            drone.trim();

            // Take off
            System.err.println("Taking off");
            drone.takeOff();

            // Fly a little :)
            Thread.sleep(1000);

            // Land
            System.err.println("Landing");
            drone.land();

            // Give it some time to land
            Thread.sleep(1000);
            
            // Disconnect from the done
            drone.disconnect();

        } catch(Throwable e)
        {
            e.printStackTrace();
        }
    }
}

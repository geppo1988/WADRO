package eu.reply.hackathon.wadro.drone;

import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.BatteryListener;

public class WadroDroneListener {

	public static void addBatteryListener(IARDrone drone){
		drone.getNavDataManager().addBatteryListener(new BatteryListener() {
			
			@Override
			public void voltageChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void batteryLevelChanged(int battLevel) {
				// TODO Auto-generated method stub
				System.out.println("Battery level "+ battLevel +"%");
			}
		});
		
	}
}

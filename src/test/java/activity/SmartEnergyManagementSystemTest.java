package activity;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.SmartEnergyManagementSystem.DeviceSchedule;
import activity.SmartEnergyManagementSystem.EnergyManagementResult;

import static org.junit.Assert.*;

public class SmartEnergyManagementSystemTest {

private SmartEnergyManagementSystem energySystem;

    private Map<String, Integer> devicePriorities;
    private List<DeviceSchedule> scheduledDevices;

    @Before
    public void initialize() {
        energySystem = new SmartEnergyManagementSystem();
        devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);
        devicePriorities.put("Cooling", 1);
        devicePriorities.put("Lights", 2);
        devicePriorities.put("Appliances", 3);
        devicePriorities.put("Security", 1);
        devicePriorities.put("Refrigerator", 1);

        scheduledDevices = new ArrayList<>();
    }

    @Test
    public void testEnergySavingWithNightModeAndCoolingAndScheduledDevices() {
        double currentPrice = 0.25;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 23, 30);
        double[] desiredTemperatureRange = {20.0, 24.0};
        double currentTemperature = 26.0;
        double energyUsageLimit = 30.0;
        double totalEnergyUsedToday = 12.0;

        scheduledDevices.add(new DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 1, 23, 30)));
        scheduledDevices.add(new DeviceSchedule("TV", LocalDateTime.of(2024, 10, 1, 23, 45)));

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, currentTemperature, desiredTemperatureRange,
                                                                  energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        // Check status of each device
        assertTrue(result.deviceStatus.get("Cooling"));
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
        assertTrue(result.deviceStatus.get("Security"));
        assertTrue(result.deviceStatus.get("Refrigerator"));
        assertTrue(result.deviceStatus.get("Oven"));
        assertNull(result.deviceStatus.get("TV"));

        assertTrue(result.energySavingMode);
        assertTrue(result.temperatureRegulationActive);
        assertEquals(totalEnergyUsedToday, result.totalEnergyUsed, 0.005);
    }

    @Test
    public void testLowTemperatureWithExceedingUsage() {
        double currentPrice = 0.05;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 11, 23);
        double[] desiredTemperatureRange = {20.0, 24.0};
        double currentTemperature = 15.0;
        double energyUsageLimit = 30.0;
        double totalEnergyUsedToday = 31.0;

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, currentTemperature, desiredTemperatureRange,
                                                                  energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

        // Check status of each device
        assertTrue(result.deviceStatus.get("Heating"));
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
        assertTrue(result.deviceStatus.get("Security"));
        assertTrue(result.deviceStatus.get("Refrigerator"));

        assertFalse(result.energySavingMode);
        assertTrue(result.temperatureRegulationActive);
        // Turn two devices off
        assertEquals(totalEnergyUsedToday - 2, result.totalEnergyUsed, 0.005);
    }

    // @Test(timeout=1000)
    // public void testNightModeAndExceedingUsageWithoutAvailableDevices() {
    //     double currentPrice = 0.05;
    //     double priceThreshold = 0.20;
    //     LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 4, 20);
    //     double[] desiredTemperatureRange = {20.0, 24.0};
    //     double currentTemperature = 22.0;
    //     double energyUsageLimit = 30.0;
    //     double totalEnergyUsedToday = 35.0;

    //     EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
    //                                                               currentTime, currentTemperature, desiredTemperatureRange,
    //                                                               energyUsageLimit, totalEnergyUsedToday, scheduledDevices);

    //     // Check status of each device
    //     assertFalse(result.deviceStatus.get("Heating"));
    //     assertFalse(result.deviceStatus.get("Cooling"));
    //     assertFalse(result.deviceStatus.get("Lights"));
    //     assertFalse(result.deviceStatus.get("Appliances"));
    //     assertFalse(result.deviceStatus.get("Security"));
    //     assertFalse(result.deviceStatus.get("Refrigerator"));

    //     assertFalse(result.energySavingMode);
    //     assertFalse(result.temperatureRegulationActive);
    //     // Turn two devices off (Security & Refrigerator)
    //     assertEquals(totalEnergyUsedToday - 2, result.totalEnergyUsed, 0.005);
    // }
}

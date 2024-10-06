package activity;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
// import java.util.Arrays;
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
    public void testEnergySavingMode() {
        double currentPrice = 0.25;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 14, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertTrue(result.energySavingMode);
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
    }

    @Test
    public void testNightMode() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 00, 30);
        double[] desiredTemperatureRange = {20.0, 24.0};

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertTrue(result.deviceStatus.get("Security"));
        assertTrue(result.deviceStatus.get("Refrigerator"));
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
    }

    @Test
    public void testTemperatureRegulationHeating() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 14, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 18.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertTrue(result.temperatureRegulationActive);
        assertTrue(result.deviceStatus.get("Heating"));
        assertFalse(result.deviceStatus.get("Cooling"));
    }

    @Test
    public void testTemperatureRegulationCooling() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 14, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 26.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertTrue(result.temperatureRegulationActive);
        assertFalse(result.deviceStatus.get("Heating"));
        assertTrue(result.deviceStatus.get("Cooling"));
    }

    @Test
    public void testTemperatureRegulationOff() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 14, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertFalse(result.temperatureRegulationActive);
        assertFalse(result.deviceStatus.get("Heating"));
        assertFalse(result.deviceStatus.get("Cooling"));
    }

    @Test
    public void testEnergyUsageLimit() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 14, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 30.0, scheduledDevices);

        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
    }

    @Test
    public void testEnergyUsageLimit2() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 14, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        devicePriorities.put("Heating", 3);
        devicePriorities.put("Cooling", 3);
        devicePriorities.put("Lights", 2);
        devicePriorities.put("Appliances", 3);
        devicePriorities.put("Security", 3);
        devicePriorities.put("Refrigerator", 3);

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice + 0.06, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 30.0, scheduledDevices);

        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
    }

    @Test
    public void testScheduledDevices() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 18, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        scheduledDevices.add(new DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 1, 18, 0)));

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertTrue(result.deviceStatus.get("Oven"));
    }

    @Test
    public void testScheduledDevices2() {
        double currentPrice = 0.15;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 18, 0);
        double[] desiredTemperatureRange = {20.0, 24.0};

        scheduledDevices.add(new DeviceSchedule("Oven", LocalDateTime.of(2023, 10, 1, 18, 0)));

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);

        assertNull(result.deviceStatus.get("Oven"));
    }

    // Testa se condicao 5(Scheduled device) se sobrepoe a 1 e 2 (Energy saving e night mode)
    @Test
    public void testCombinationOfFactors() {
        double currentPrice = 0.25;
        double priceThreshold = 0.20;
        LocalDateTime currentTime = LocalDateTime.of(2024, 10, 1, 23, 30);
        double[] desiredTemperatureRange = {20.0, 24.0};

        scheduledDevices.add(new DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 1, 23, 30)));

        EnergyManagementResult result = energySystem.manageEnergy(currentPrice, priceThreshold, devicePriorities,
                                                                  currentTime, 22.0, desiredTemperatureRange,
                                                                  30.0, 25.0, scheduledDevices);


        assertTrue(result.energySavingMode);
        assertTrue(result.deviceStatus.get("Security"));
        assertTrue(result.deviceStatus.get("Refrigerator"));

        assertTrue(result.deviceStatus.get("Oven"));
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
    }

}

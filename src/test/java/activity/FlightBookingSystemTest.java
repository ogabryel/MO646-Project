package activity;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import activity.FlightBookingSystem.BookingResult;

public class FlightBookingSystemTest {

    private FlightBookingSystem flightBookingSystem;

    @Before
    public void initialize() {
        flightBookingSystem = new FlightBookingSystem();
    }

    @Test
    public void testNoAvailableSeats() {
        int passengers = 3;
        int availableSeats = passengers - 1;
        LocalDateTime bookingTime = LocalDateTime.now();
        LocalDateTime departureTime = bookingTime.plusDays(10);
        int rewardPointsAvailable = 0;
        double currentPrice = 1000.0;
        int previousSales = 100;
        boolean isCancellation = false;

        BookingResult result = flightBookingSystem.bookFlight(passengers, bookingTime, availableSeats,
                                                              currentPrice, previousSales, isCancellation,
                                                              departureTime, rewardPointsAvailable);
        assertFalse(result.confirmation);
        assertEquals(0, result.refundAmount, 0.005);
        assertEquals(0, result.totalPrice, 0.005);
        assertFalse(result.pointsUsed);
    }

    @Test
    public void testApplyAllFees(){
        int passengers = 5;
        int availableSeats = 100;
        LocalDateTime bookingTime = LocalDateTime.now();
        LocalDateTime departureTime = bookingTime.plusHours(20);
        int rewardPointsAvailable = 1000;
        double currentPrice = 1000.0;
        int previousSales = 100;
        boolean isCancellation = false;

        BookingResult result = flightBookingSystem.bookFlight(passengers, bookingTime, availableSeats,
                                                              currentPrice, previousSales, isCancellation,
                                                              departureTime, rewardPointsAvailable);
        assertTrue(result.confirmation);
        assertEquals(0, result.refundAmount, 0.005);
        // totalPrice = (currentPrice * priceFactor * passengers + 100) * 0.95 - rewardPoints*0.01
        assertEquals(3885, result.totalPrice, 0.005);
        assertTrue(result.pointsUsed);
    }

    @Test
    public void testIsCancellationInExactlyTwoDays(){
        int passengers = 4;
        int availableSeats = 100;
        LocalDateTime bookingTime = LocalDateTime.now();
        LocalDateTime departureTime = bookingTime.plusDays(2);
        int rewardPointsAvailable = 0;
        double currentPrice = 1000.0;
        int previousSales = 100;
        boolean isCancellation = true;

        BookingResult result = flightBookingSystem.bookFlight(passengers, bookingTime, availableSeats,
                                                              currentPrice, previousSales, isCancellation,
                                                              departureTime, rewardPointsAvailable);
        assertFalse(result.confirmation);
        assertEquals(3200, result.refundAmount, 0.005);
        assertEquals(0, result.totalPrice, 0.005);
        assertFalse(result.pointsUsed);
    }

    @Test
    public void testIsCancellationInOneDay(){
        int passengers = 4;
        int availableSeats = 100;
        LocalDateTime bookingTime = LocalDateTime.now();
        LocalDateTime departureTime = bookingTime.plusDays(1);
        int rewardPointsAvailable = 0;
        double currentPrice = 1000.0;
        int previousSales = 100;
        boolean isCancellation = true;

        BookingResult result = flightBookingSystem.bookFlight(passengers, bookingTime, availableSeats,
                                                              currentPrice, previousSales, isCancellation,
                                                              departureTime, rewardPointsAvailable);
        assertFalse(result.confirmation);
        assertEquals(1600, result.refundAmount, 0.005);
        // totalPrice = (currentPrice * priceFactor * passengers + 100) * 0.95 - rewardPoints*0.01
        assertEquals(0, result.totalPrice, 0.005);
        assertFalse(result.pointsUsed);
    }

    @Test
    public void testHasTheLimitOfAvailableSeats(){
        int passengers = 5;
        int availableSeats = 5;
        LocalDateTime bookingTime = LocalDateTime.now();
        LocalDateTime departureTime = bookingTime.plusHours(20);
        int rewardPointsAvailable = 1000;
        double currentPrice = 1000.0;
        int previousSales = 100;
        boolean isCancellation = false;

        BookingResult result = flightBookingSystem.bookFlight(passengers, bookingTime, availableSeats,
                                                              currentPrice, previousSales, isCancellation,
                                                              departureTime, rewardPointsAvailable);
        assertTrue(result.confirmation);
        assertEquals(0, result.refundAmount, 0.005);
        assertEquals(3885, result.totalPrice, 0.005);
        assertTrue(result.pointsUsed);
    }

    @Test
    public void testDoNotApplyRewardPoints(){
        int passengers = 5;
        int availableSeats = 100;
        LocalDateTime bookingTime = LocalDateTime.now();
        LocalDateTime departureTime = bookingTime.plusHours(20);
        int rewardPointsAvailable = 0;
        double currentPrice = 1000.0;
        int previousSales = 100;
        boolean isCancellation = false;

        BookingResult result = flightBookingSystem.bookFlight(passengers, bookingTime, availableSeats,
                                                              currentPrice, previousSales, isCancellation,
                                                              departureTime, rewardPointsAvailable);
        assertTrue(result.confirmation);
        assertEquals(0, result.refundAmount, 0.005);
        assertEquals(3895, result.totalPrice, 0.005);
        assertFalse(result.pointsUsed);
    }
}

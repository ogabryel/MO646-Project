package activity;

import org.junit.Before;
import org.junit.Test;

import activity.FraudDetectionSystem.Transaction;
import activity.FraudDetectionSystem.FraudCheckResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FraudDetectionSystemTest {

    private FraudDetectionSystem fraudDetectionSystem;
    private List<String> blacklistedLocations;

    @Before
    public void initialize() {
        fraudDetectionSystem = new FraudDetectionSystem();
        blacklistedLocations = Arrays.asList("HighRiskCountry1", "HighRiskCountry2");
    }

    @Test
    public void testHighAmount() {
        Transaction highAmountTransaction = new Transaction(15000, LocalDateTime.now(), "Brazil");
        List<Transaction> previousTransactions = new ArrayList<>();

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(highAmountTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertEquals(50, result.riskScore);
    }

    @Test
    public void testExcessiveTransactionsInShortTime() {
        Transaction currentTransaction = new Transaction(100, LocalDateTime.now(), "Brazil");
        List<Transaction> previousTransactions = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now().minusMinutes(30);

        for (int i = 0; i < 11; i++) {
            previousTransactions.add(new Transaction(100, baseTime.plusMinutes(i * 5), "Brazil"));
        }

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isBlocked);
        assertEquals(30, result.riskScore);
    }

    @Test
    public void testLocationChangeInShortTime() {
        Transaction currentTransaction = new Transaction(100, LocalDateTime.now(), "France");
        List<Transaction> previousTransactions = new ArrayList<>();
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(15), "Brazil"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertEquals(20, result.riskScore);
    }

    @Test
    public void testBlacklistedLocation() {
        Transaction blacklistedTransaction = new Transaction(100, LocalDateTime.now(), "HighRiskCountry1");
        List<Transaction> previousTransactions = new ArrayList<>();

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(blacklistedTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isBlocked);
        assertEquals(100, result.riskScore);
    }

    @Test
    public void testCombinationOfFactors() {
        Transaction currentTransaction = new Transaction(12000, LocalDateTime.now(), "Brazil");
        List<Transaction> previousTransactions = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(i * 2), "Brazil"));
        }
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(15), "France"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertTrue(result.isBlocked);
        assertEquals(100, result.riskScore);  // 50 (high amount) + 30 (excessive transactions) + 20 (location change)
    }

    @Test
    public void testNormalTransaction() {
        Transaction normalTransaction = new Transaction(500, LocalDateTime.now(), "Brazil");
        List<Transaction> previousTransactions = new ArrayList<>();
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusHours(2), "Brazil"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(normalTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isFraudulent);
        assertFalse(result.isBlocked);
        assertFalse(result.verificationRequired);
        assertEquals(0, result.riskScore);
    }
}

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
    private List<Transaction> previousTransactions;

    @Before
    public void initialize() {
        fraudDetectionSystem = new FraudDetectionSystem();
        blacklistedLocations = Arrays.asList("HighRiskCountry1", "HighRiskCountry2");
        previousTransactions = new ArrayList<>();
    }

    @Test
    public void testNormalTransaction() {
        Transaction normalTransaction = new Transaction(500, LocalDateTime.now(), "Brazil");

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(normalTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isFraudulent);
        assertFalse(result.isBlocked);
        assertFalse(result.verificationRequired);
        assertEquals(0, result.riskScore);
    }

    @Test
    public void testHighRisk() {
        Transaction currentTransaction = new Transaction(12000, LocalDateTime.now(), "HighRiskCountry1");
        for (int i = 12; i >= 0; i--) {
            previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(6 * i), "Brazil"));
        }

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertTrue(result.isBlocked);
        assertEquals(100, result.riskScore);
    }

    @Test
    public void testRecentTransactionSameCountry() {
        Transaction currentTransaction = new Transaction(1000, LocalDateTime.now(), "Brazil");
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(20), "Brazil"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isFraudulent);
        assertFalse(result.verificationRequired);
        assertFalse(result.isBlocked);
        assertEquals(0, result.riskScore);
    }

    @Test
    public void testNotRecentTransactionDifferentCountry() {
        Transaction currentTransaction = new Transaction(1000, LocalDateTime.now(), "Brazil");
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(120), "France"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isFraudulent);
        assertFalse(result.verificationRequired);
        assertFalse(result.isBlocked);
        assertEquals(0, result.riskScore);
    }
}

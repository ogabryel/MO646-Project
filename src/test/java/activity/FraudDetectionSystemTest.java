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


    
    ///////////////////////////////////////
    ////                               ////
    //// Testes para eliminar mutantes ////
    ////                               ////
    /////////////////////////////////////// 

    // Mata o mutante: Linha 42 - Changed conditional boundary.
    @Test
    public void testAlmostHighAmount() {
        Transaction currentTransaction = new Transaction(10000, LocalDateTime.now(), "Brazil");

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isFraudulent);
        assertFalse(result.verificationRequired);
        assertEquals(0, result.riskScore);
    }

    // Mata o mutante: Linha 45 - Changed increment from 50 to -50.
    @Test
    public void testHighAmount() {
        Transaction currentTransaction = new Transaction(10001, LocalDateTime.now(), "Brazil");

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertEquals(50, result.riskScore);
    }

    // Mata os mutantes: Linha 51 - Negated conditional.
    //                   Linha 52 - Changed increment from 1 to -1.
    //                   Linha 57 - Changed increment from 30 to -30.
    @Test
    public void testExcessiveTransactionsInShortTime() {
        Transaction currentTransaction = new Transaction(1000, LocalDateTime.now(), "Brazil");
        for (int i = 12; i >= 0; i--) {
            previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(2 * i), "Brazil"));
        }

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isBlocked);
        assertEquals(30, result.riskScore);
    }

    // Mata o mutante: Linha 51 - Changed conditional boundary.
    @Test
    public void testExactly60MinutesTransaction() {
        LocalDateTime currentTime = LocalDateTime.now();
        Transaction currentTransaction = new Transaction(1000, currentTime, "Brazil");

        for (int i = 1; i <= 10; i++) {
            previousTransactions.add(new Transaction(100, currentTime.minusMinutes(i * 5), "Brazil"));
        }

        previousTransactions.add(new Transaction(100, currentTime.minusMinutes(60), "Brazil"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isBlocked);
        assertEquals(30, result.riskScore);
    }

    // Mata o mutante: Linha 55 - Changed conditional boundary.
    @Test
    public void testAlmostExcessiveTransactionsInShortTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        Transaction currentTransaction = new Transaction(1000, currentTime, "Brazil");

        for (int i = 1; i <= 10; i++) {
            previousTransactions.add(new Transaction(100, currentTime.minusMinutes(i * 5), "Brazil"));
        }

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isBlocked);
        assertEquals(0, result.riskScore);
    }

    // Mata o mutante: Linha 64 - Changed conditional boundary.
    @Test
    public void testAlmostLocationChangeInShortTime() {
        Transaction currentTransaction = new Transaction(1000, LocalDateTime.now(), "Brazil");
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(31), "France"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertEquals(0, result.riskScore);
    }

    // Mata o mutante: Linha 67 - Changed increment from 20 to -20.
    @Test
    public void testLocationChangeInShortTime() {
        Transaction currentTransaction = new Transaction(1000, LocalDateTime.now(), "Brazil");
        previousTransactions.add(new Transaction(100, LocalDateTime.now().minusMinutes(29), "France"));

        FraudCheckResult result = fraudDetectionSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertEquals(20, result.riskScore);
    }

}

package practice_tasks_2;

import org.example.practice_tasks_2.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountBigDecimalTest {

    Account a;
    Account b;
    
    private final String BALANCE_A = "500.00";
    private final String BALANCE_B = "200.50";

    @BeforeEach
    void setUp() {
        a = new Account(1, new BigDecimal(BALANCE_A));
        b = new Account(2, new BigDecimal(BALANCE_B));
    }

    @Test
    void simpleTransferShouldMoveMoney() {
        Account.transfer(a, b, new BigDecimal("300.25"));

        assertEquals(new BigDecimal("199.75"), a.getBalance());
        assertEquals(new BigDecimal("500.75"), b.getBalance());
    }

    @Test
    void transferInsufficientFundsShouldThrowAndNotChangeBalances() {
        assertThrows(IllegalArgumentException.class,
                () -> Account.transfer(a, b, new BigDecimal("600")));

        assertEquals(new BigDecimal(BALANCE_A), a.getBalance());
        assertEquals(new BigDecimal(BALANCE_B), b.getBalance());
    }

    @Test
    void transferToSelfShouldBeNoop() {
        Account.transfer(a, a, new BigDecimal("500"));

        assertEquals(new BigDecimal(BALANCE_A), a.getBalance());
    }

    @Test
    void transferWithNegativeAmountShouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> Account.transfer(a, b, new BigDecimal("-10")));
    }
}

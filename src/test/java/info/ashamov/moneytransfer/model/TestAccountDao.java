package info.ashamov.moneytransfer.model;

import info.ashamov.moneytransfer.TestData;
import info.ashamov.moneytransfer.dto.Account;
import info.ashamov.moneytransfer.dto.MoneyTransfer;
import info.ashamov.moneytransfer.exception.InternalException;
import info.ashamov.moneytransfer.util.DBUtil;
import info.ashamov.moneytransfer.util.PropertyUtil;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestAccountDao {
    private AccountDAO dao = AccountDAOFactory.getAccountDAO(PropertyUtil.getProperty("persistence.type"));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        PropertyUtil.initialize("application.properties");
    }

    @Before
    public void beforeTest() {
        DBUtil.initialize();
    }

    @Test
    public void accountListReceived() {
        List<Account> accounts = dao.find();
        Assert.assertEquals(TestData.ACCOUNT_LIST_SIZE, accounts.size());
    }

    @Test
    public void accountReceivedById() {
        Account account = dao.find(TestData.FIRST_EUR_ACCOUNT_ID);
        Assert.assertEquals(TestData.FIRST_EUR_ACCOUNT_HOLDER, account.getHolderName());
    }

    @Test
    public void accountNotReceivedByWrongId() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Account not found"));
        dao.find(TestData.NONEXISTENT_ID);
    }

    @Test
    public void accountCreated() {
        Account account = new Account(null, TestData.NEW_ACCOUNT_HOLDER, TestData.NEW_ACCOUNT_CC, TestData.NEW_ACCOUNT_BALANCE);
        Long id = dao.save(account);
        assertNotNull(id);
    }

    @Test
    public void accountWithNegativeBalanceNotCreated() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Account balance can't be less than 0"));
        Account account = new Account(null, TestData.NEW_ACCOUNT_HOLDER, TestData.NEW_ACCOUNT_CC, TestData.NEW_ACCOUNT_BALANCE.negate());
        dao.save(account);
    }

    @Test
    public void existentAccountDeleted() {
        dao.delete(TestData.FIRST_EUR_ACCOUNT_ID);
        List<Account> accounts = dao.find();
        Assert.assertEquals(TestData.ACCOUNT_LIST_SIZE - 1, accounts.size());
    }

    @Test
    public void nonexistentAccountNotDeleted() {
        dao.delete(TestData.NONEXISTENT_ID);
        List<Account> accounts = dao.find();
        Assert.assertEquals(TestData.ACCOUNT_LIST_SIZE, accounts.size());
    }

    @Test
    public void moneyDepositedOnAccount() {
        dao.deposit(TestData.FIRST_EUR_ACCOUNT_ID, TestData.TRANSFER_AMOUNT);
        Account account = dao.find(TestData.FIRST_EUR_ACCOUNT_ID);
        Assert.assertEquals(0, TestData.FIRST_EUR_ACCOUNT_BALANCE.add(TestData.TRANSFER_AMOUNT).compareTo(account.getBalance()));
    }

    @Test
    public void negativeAmountOfMoneyNotDepositedOnAccount() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Amount can't be negative"));
        dao.deposit(TestData.NONEXISTENT_ID, TestData.TRANSFER_AMOUNT.negate());
    }

    @Test
    public void moneyNotDepositedOnNonexistentAccount() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Account not found"));
        dao.deposit(TestData.NONEXISTENT_ID, TestData.TRANSFER_AMOUNT);
    }

    @Test
    public void moneyWithdrewFromAccount() {
        dao.withdraw(TestData.FIRST_EUR_ACCOUNT_ID, TestData.TRANSFER_AMOUNT);
        Account account = dao.find(TestData.FIRST_EUR_ACCOUNT_ID);
        Assert.assertEquals(0, TestData.FIRST_EUR_ACCOUNT_BALANCE.subtract(TestData.TRANSFER_AMOUNT).compareTo(account.getBalance()));
    }

    @Test
    public void negativeAmountOfMoneyNotWithdrewFromAccount() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Amount can't be negative"));
        dao.withdraw(TestData.NONEXISTENT_ID, TestData.TRANSFER_AMOUNT.negate());
    }

    @Test
    public void moneyNotWithdrewFromNonexistentAccount() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Account not found"));
        dao.withdraw(TestData.NONEXISTENT_ID, TestData.NEW_ACCOUNT_BALANCE);
    }

    @Test
    public void moneyNotWithdrewFromAccountWhenFundsInsufficient() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Insufficient funds"));
        dao.withdraw(TestData.FIRST_EUR_ACCOUNT_ID, TestData.FIRST_EUR_ACCOUNT_BALANCE.add(TestData.TRANSFER_AMOUNT));
    }

    @Test
    public void moneyTransferred() {
        MoneyTransfer moneyTransfer = new MoneyTransfer(TestData.FIRST_EUR_ACCOUNT_ID, TestData.SECOND_EUR_ACCOUNT_ID, TestData.TRANSFER_AMOUNT);
        BigDecimal senderBalance = dao.find(TestData.FIRST_EUR_ACCOUNT_ID).getBalance();
        BigDecimal receiverBalance = dao.find(TestData.SECOND_EUR_ACCOUNT_ID).getBalance();
        dao.transfer(moneyTransfer);
        Account sender = dao.find(TestData.FIRST_EUR_ACCOUNT_ID);
        Account receiver = dao.find(TestData.SECOND_EUR_ACCOUNT_ID);
        assertEquals(0, sender.getBalance().compareTo(senderBalance.subtract(TestData.TRANSFER_AMOUNT)));
        assertEquals(0, receiver.getBalance().compareTo(receiverBalance.add(TestData.TRANSFER_AMOUNT)));
    }

    @Test
    public void moneyNotTransferredFromNonexistentSender() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Wrong sender, account id"));
        MoneyTransfer moneyTransfer = new MoneyTransfer(TestData.NONEXISTENT_ID, TestData.SECOND_EUR_ACCOUNT_ID, TestData.TRANSFER_AMOUNT);
        dao.transfer(moneyTransfer);
    }

    @Test
    public void moneyNotTransferredFromInsufficientBalance() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Insufficient funds"));
        MoneyTransfer moneyTransfer = new MoneyTransfer(
                TestData.FIRST_EUR_ACCOUNT_ID,
                TestData.SECOND_EUR_ACCOUNT_ID,
                TestData.FIRST_EUR_ACCOUNT_BALANCE.add(TestData.TRANSFER_AMOUNT));
        dao.transfer(moneyTransfer);
    }

    @Test
    public void moneyNotTransferredToNonexistentReceiver() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Wrong receiver, account id"));
        MoneyTransfer moneyTransfer = new MoneyTransfer(TestData.FIRST_EUR_ACCOUNT_ID, TestData.NONEXISTENT_ID, TestData.TRANSFER_AMOUNT);
        dao.transfer(moneyTransfer);
    }

    @Test
    public void moneyNotTransferredBetweenDifferentCurrencies() {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Accounts have different currencies"));
        MoneyTransfer moneyTransfer = new MoneyTransfer(TestData.FIRST_EUR_ACCOUNT_ID, TestData.USD_ACCOUNT_ID, TestData.TRANSFER_AMOUNT);
        dao.transfer(moneyTransfer);
    }

    @Test
    public void moneyNotTransferredOnDatabaseLockTimeout() throws SQLException {
        expectedException.expect(InternalException.class);
        expectedException.expectMessage(startsWith("Money transfer failed"));
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     TestData.SQL_SELECT_ACCOUNT_FOR_UPDATE,
                     ResultSet.TYPE_FORWARD_ONLY,
                     ResultSet.CONCUR_UPDATABLE)) {

            connection.setAutoCommit(false);
            statement.setLong(1, TestData.SECOND_EUR_ACCOUNT_ID);
            statement.executeQuery();

            MoneyTransfer moneyTransfer =
                    new MoneyTransfer(TestData.FIRST_EUR_ACCOUNT_ID, TestData.SECOND_EUR_ACCOUNT_ID, TestData.TRANSFER_AMOUNT);
            dao.transfer(moneyTransfer);
        }
    }
}
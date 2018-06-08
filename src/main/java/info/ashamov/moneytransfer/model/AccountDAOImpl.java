package info.ashamov.moneytransfer.model;

import info.ashamov.moneytransfer.dto.MoneyTransfer;
import info.ashamov.moneytransfer.dto.Account;
import info.ashamov.moneytransfer.exception.InternalException;
import info.ashamov.moneytransfer.util.DBUtil;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class AccountDAOImpl implements AccountDAO {
    private static final String SQL_INSERT =
            "INSERT INTO account (holderName, currencyCode, balance) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM account";
    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM account WHERE id = ?";
    private static final String SQL_SELECT_BY_ID_FOR_UPDATE =
            "SELECT * FROM account WHERE id = ? FOR UPDATE";
    private static final String SQL_DELETE_BY_ID =
            "DELETE FROM account WHERE id = ?";

    private Account rsToAccount(ResultSet resultSet) throws SQLException {
        return new Account(
                resultSet.getLong("id"),
                resultSet.getString("holderName"),
                resultSet.getString("currencyCode"),
                resultSet.getBigDecimal("balance")
        );
    }

    private void changeBalance(long id, BigDecimal changeAmount) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID_FOR_UPDATE,
                     ResultSet.TYPE_FORWARD_ONLY,
                     ResultSet.CONCUR_UPDATABLE)) {

            statement.setLong(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new InternalException("Account not found, id = " + id, Response.Status.NOT_FOUND);
            }
            Account account = rsToAccount(resultSet);
            BigDecimal resultingBalance = account.getBalance().add(changeAmount);

            if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InternalException("Insufficient funds", Response.Status.BAD_REQUEST);
            }
            resultSet.updateBigDecimal("balance", resultingBalance);
            resultSet.updateRow();
        } catch (SQLException e) {
            throw new InternalException("Balance change failed, account id " + id, e);
        }
    }

    @Override
    public Long save(Account account) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            if (account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new InternalException("Account balance can't be less than 0");
            }

            statement.setString(1, account.getHolderName());
            statement.setString(2, account.getCurrencyCode());
            statement.setBigDecimal(3, account.getBalance());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new InternalException("Account creation failed, account id " + account, e);
        }
    }

    @Override
    public List<Account> find() {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                accounts.add(rsToAccount(resultSet));
            }
            return accounts;
        } catch (SQLException e) {
            throw new InternalException("Account list receiving failed", e);
        }
    }

    @Override
    public Account find(long id) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return rsToAccount(resultSet);
                } else {
                    throw new InternalException("Account not found, account id " + id, Response.Status.NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            throw new InternalException("Account receiving failed, account id " + id, e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE_BY_ID)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new InternalException("Account deleting failed, account id " + id, e);
        }
    }

    @Override
    public void deposit(long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InternalException("Amount can't be negative", Response.Status.BAD_REQUEST);
        }
        changeBalance(id, amount);
    }

    @Override
    public void withdraw(long id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InternalException("Amount can't be negative", Response.Status.BAD_REQUEST);
        }
        changeBalance(id, amount.negate());
    }

    @Override
    public void transfer(MoneyTransfer moneyTransfer) {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement senderStatement = connection.prepareStatement(SQL_SELECT_BY_ID_FOR_UPDATE,
                     ResultSet.TYPE_FORWARD_ONLY,
                     ResultSet.CONCUR_UPDATABLE);
             PreparedStatement receiverStatement = connection.prepareStatement(SQL_SELECT_BY_ID_FOR_UPDATE,
                     ResultSet.TYPE_FORWARD_ONLY,
                     ResultSet.CONCUR_UPDATABLE)) {

            if (moneyTransfer.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new InternalException("Amount can't be negative", Response.Status.BAD_REQUEST);
            }

            long senderId = moneyTransfer.getSenderAccountId();
            long receiverId = moneyTransfer.getReceiverAccountId();

            if (senderId == receiverId) {
                throw new InternalException("Sender and receiver accounts should be different", Response.Status.BAD_REQUEST);
            }

            senderStatement.setLong(1, senderId);
            receiverStatement.setLong(1, receiverId);

            connection.setAutoCommit(false);

            ResultSet senderRS, receiverRS;
            //obtain DB locks always in ASC order by account id to avoid deadlock
            if (senderId < receiverId) {
                senderRS = senderStatement.executeQuery();
                receiverRS = receiverStatement.executeQuery();
            } else {
                receiverRS = receiverStatement.executeQuery();
                senderRS = senderStatement.executeQuery();
            }

            if (!senderRS.next()) {
                throw new InternalException("Wrong sender, account id " + senderId, Response.Status.BAD_REQUEST);
            }
            Account senderAccount = rsToAccount(senderRS);
            if (senderAccount.getBalance().compareTo(moneyTransfer.getAmount()) < 0) {
                throw new InternalException("Insufficient funds, account id " + senderId, Response.Status.BAD_REQUEST);
            }

            if (!receiverRS.next()) {
                throw new InternalException("Wrong receiver, account id " + senderId, Response.Status.BAD_REQUEST);
            }
            Account receiverAccount = rsToAccount(receiverRS);

            if (!senderAccount.getCurrencyCode().equals(receiverAccount.getCurrencyCode())) {
                throw new InternalException("Accounts have different currencies", Response.Status.BAD_REQUEST);
            }

            senderRS.updateBigDecimal("balance",
                    senderAccount.getBalance().subtract(moneyTransfer.getAmount()));
            senderRS.updateRow();

            receiverRS.updateBigDecimal("balance",
                    receiverAccount.getBalance().add(moneyTransfer.getAmount()));
            receiverRS.updateRow();

            connection.commit();
        } catch (SQLException e) {
            throw new InternalException("Money transfer failed: " + moneyTransfer, e);
        }
    }
}

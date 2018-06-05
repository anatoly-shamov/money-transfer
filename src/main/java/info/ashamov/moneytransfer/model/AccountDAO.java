package info.ashamov.moneytransfer.model;

import info.ashamov.moneytransfer.dto.Account;
import info.ashamov.moneytransfer.dto.MoneyTransfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    Long save(Account account);
    List<Account> find();
    Account find(long id);
    void delete(long id);

    void deposit(long id, BigDecimal amount);
    void withdraw(long id, BigDecimal amount);
    void transfer(MoneyTransfer moneyTransfer);
}

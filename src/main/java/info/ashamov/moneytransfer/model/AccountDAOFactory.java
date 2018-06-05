package info.ashamov.moneytransfer.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import info.ashamov.moneytransfer.exception.InternalException;

public class AccountDAOFactory {
    private static Logger log = LogManager.getLogger(AccountDAOFactory.class);

    public static AccountDAO getAccountDAO(String persistenceType) {
        switch (persistenceType) {
            case "h2":
                return new AccountDAOImpl();
            default:
                String msg = String.format("Incorrect persistence type: %s", persistenceType);
                log.fatal(msg);
                throw new InternalException(msg);
        }
    }
}

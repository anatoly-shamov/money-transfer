package info.ashamov.moneytransfer;

import java.math.BigDecimal;

public class TestData {
    public static final int ACCOUNT_LIST_SIZE = 4;
    public static final Long FIRST_EUR_ACCOUNT_ID = 1L;
    public static final Long USD_ACCOUNT_ID = 2L;
    public static final Long SECOND_EUR_ACCOUNT_ID = 3L;
    public static final Long NEW_ACCOUNT_ID = 5L;
    public static final Long NONEXISTENT_ID = 10L;
    public static final String FIRST_EUR_ACCOUNT_HOLDER = "John Dow";
    public static final String NEW_ACCOUNT_HOLDER = "Mick Dundee";
    public static final String NEW_ACCOUNT_CC = "AUD";
    public static final BigDecimal FIRST_EUR_ACCOUNT_BALANCE = BigDecimal.valueOf(1000);
    public static final BigDecimal NEW_ACCOUNT_BALANCE = BigDecimal.valueOf(1500);
    public static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(100);
    public static final String SQL_SELECT_ACCOUNT_FOR_UPDATE = "SELECT * FROM account WHERE id = ? FOR UPDATE";
}

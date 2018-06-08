package info.ashamov.moneytransfer.controller;

import info.ashamov.moneytransfer.dto.MoneyTransfer;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import static org.eclipse.jetty.http.HttpStatus.Code.*;
import static org.junit.Assert.assertEquals;
import static info.ashamov.moneytransfer.TestData.*;

public class TestMoneyTransferController extends TestController {

    @Test
    public void moneyTransferred() throws Exception {
        MoneyTransfer moneyTransfer = new MoneyTransfer(FIRST_EUR_ACCOUNT_ID, SECOND_EUR_ACCOUNT_ID, TRANSFER_AMOUNT);
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(OK.getCode(), statusCode);
        }
    }

    @Test
    public void negativeAmountOfMoneyNotTransferred() throws Exception {
        MoneyTransfer moneyTransfer =
                new MoneyTransfer(FIRST_EUR_ACCOUNT_ID, SECOND_EUR_ACCOUNT_ID, TRANSFER_AMOUNT.negate());
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotTransferredForSameAccount() throws Exception {
        MoneyTransfer moneyTransfer =
                new MoneyTransfer(FIRST_EUR_ACCOUNT_ID, FIRST_EUR_ACCOUNT_ID, TRANSFER_AMOUNT);
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotTransferredFromNonexistentSender() throws Exception {
        MoneyTransfer moneyTransfer = new MoneyTransfer(NONEXISTENT_ID, SECOND_EUR_ACCOUNT_ID, TRANSFER_AMOUNT);
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotTransferredFromInsufficientBalance() throws Exception {
        MoneyTransfer moneyTransfer = new MoneyTransfer(
                FIRST_EUR_ACCOUNT_ID,
                SECOND_EUR_ACCOUNT_ID,
                FIRST_EUR_ACCOUNT_BALANCE.add(TRANSFER_AMOUNT));
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotTransferredToNonexistentReceiver() throws Exception {
        MoneyTransfer moneyTransfer = new MoneyTransfer(FIRST_EUR_ACCOUNT_ID, NONEXISTENT_ID, TRANSFER_AMOUNT);
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotTransferredBetweenDifferentCurrencies() throws Exception {
        MoneyTransfer moneyTransfer = new MoneyTransfer(FIRST_EUR_ACCOUNT_ID, USD_ACCOUNT_ID, TRANSFER_AMOUNT);
        HttpPost request = new HttpPost(uriBuilder.setPath("transfer").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(moneyTransfer)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }
}

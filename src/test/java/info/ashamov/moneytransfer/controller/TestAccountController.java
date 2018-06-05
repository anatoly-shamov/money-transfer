package info.ashamov.moneytransfer.controller;

import info.ashamov.moneytransfer.TestData;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.hamcrest.core.StringEndsWith;
import org.junit.Assert;
import org.junit.Test;
import info.ashamov.moneytransfer.dto.Account;

import static info.ashamov.moneytransfer.TestData.*;
import static org.eclipse.jetty.http.HttpStatus.Code.*;
import static org.junit.Assert.*;

public class TestAccountController extends TestController {

    @Test
    public void accountListReceived() throws Exception {
        HttpGet request = new HttpGet(uriBuilder.setPath("account/all").build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(OK.getCode(), statusCode);
            String responseBody = EntityUtils.toString(response.getEntity());
            Account[] accounts = mapper.readValue(responseBody, Account[].class);
            Assert.assertEquals(ACCOUNT_LIST_SIZE, accounts.length);
        }
    }

    @Test
    public void accountReceived() throws Exception {
        HttpGet request = new HttpGet(uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(OK.getCode(), statusCode);
            String responseBody = EntityUtils.toString(response.getEntity());
            Account account = mapper.readValue(responseBody, Account.class);
            assertEquals(FIRST_EUR_ACCOUNT_HOLDER, account.getHolderName());
        }
    }

    @Test
    public void nonexistentAccountNotReceived() throws Exception {
        HttpGet request = new HttpGet(uriBuilder.setPath("account/" + NONEXISTENT_ID).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(NOT_FOUND.getCode(), statusCode);
        }
    }

    @Test
    public void newAccountCreated() throws Exception {
        Account newAccount = new Account(null, NEW_ACCOUNT_HOLDER, NEW_ACCOUNT_CC, NEW_ACCOUNT_BALANCE);
        HttpPost request = new HttpPost(uriBuilder.setPath("account").build());
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(mapper.writeValueAsString(newAccount)));
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(CREATED.getCode(), statusCode);
            Header[] headers = response.getHeaders("Location");
            assertTrue(headers.length != 0);
            assertThat(headers[0].getValue(), StringEndsWith.endsWith(NEW_ACCOUNT_ID.toString()));
        }
    }

    @Test
    public void accountDeleted() throws Exception {
        HttpDelete request = new HttpDelete(uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(NO_CONTENT.getCode(), statusCode);
        }
    }

    @Test
    public void moneyDepositedOnAccount() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID + "/deposit/" + TRANSFER_AMOUNT).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(OK.getCode(), statusCode);
        }
    }

    @Test
    public void negativeAmountOfMoneyNotDepositedOnAccount() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID + "/deposit/" + TRANSFER_AMOUNT.negate()).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotDepositedOnNonexistentAccount() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + NONEXISTENT_ID + "/deposit/" + TRANSFER_AMOUNT).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(NOT_FOUND.getCode(), statusCode);
        }
    }

    @Test
    public void moneyWithdrewFromAccount() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID + "/withdraw/" + TRANSFER_AMOUNT).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(OK.getCode(), statusCode);
        }
    }

    @Test
    public void negativeAmountOfMoneyNotWithdrewFromAccount() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID + "/withdraw/" + TRANSFER_AMOUNT.negate()).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotWithdrewFromNonexistentAccount() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + NONEXISTENT_ID + "/withdraw/" + TRANSFER_AMOUNT).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(NOT_FOUND.getCode(), statusCode);
        }
    }

    @Test
    public void moneyNotWithdrewFromAccountWhenFundsInsufficient() throws Exception {
        HttpPatch request = new HttpPatch(
                uriBuilder.setPath("account/" + FIRST_EUR_ACCOUNT_ID + "/withdraw/" + NEW_ACCOUNT_BALANCE).build());
        try (CloseableHttpResponse response = client.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(BAD_REQUEST.getCode(), statusCode);
        }
    }
}

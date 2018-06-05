package info.ashamov.moneytransfer.controller;

import info.ashamov.moneytransfer.model.AccountDAO;
import info.ashamov.moneytransfer.model.AccountDAOFactory;
import info.ashamov.moneytransfer.dto.MoneyTransfer;
import info.ashamov.moneytransfer.util.PropertyUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferController {
    private AccountDAO dao = AccountDAOFactory.getAccountDAO(PropertyUtil.getProperty("persistence.type"));

    @POST
    public Response transfer(MoneyTransfer moneyTransfer) {
        dao.transfer(moneyTransfer);
        return Response.ok().build();
    }
}

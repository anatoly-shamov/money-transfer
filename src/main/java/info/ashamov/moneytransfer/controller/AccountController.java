package info.ashamov.moneytransfer.controller;

import info.ashamov.moneytransfer.model.AccountDAOFactory;
import info.ashamov.moneytransfer.model.AccountDAO;
import info.ashamov.moneytransfer.dto.Account;
import info.ashamov.moneytransfer.controller.annotation.PATCH;
import info.ashamov.moneytransfer.util.PropertyUtil;
import org.apache.http.client.utils.URIBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {
    private AccountDAO dao = AccountDAOFactory.getAccountDAO(PropertyUtil.getProperty("persistence.type"));
    private URIBuilder uriBuilder = new URIBuilder()
            .setScheme("http")
            .setHost(PropertyUtil.getProperty("server.host"))
            .setPort(PropertyUtil.getPropertyAsInteger("server.port"));

    @POST
    public Response save(Account account) throws URISyntaxException {
        Long id = dao.save(account);
        URI locationUri = uriBuilder.setPath("/account/" + id).build();
        return Response.created(locationUri).build();
    }

    @GET
    @Path("/all")
    public List<Account> find() {
        return dao.find();
    }

    @GET
    @Path("/{id}")
    public Account find(@PathParam("id") Long id) {
        return dao.find(id);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        dao.delete(id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{id}/deposit/{amount}")
    public Response deposit(@PathParam("id") Long id, @PathParam("amount") BigDecimal amount) {
        dao.deposit(id, amount);
        return Response.ok().build();
    }

    @PATCH
    @Path("/{id}/withdraw/{amount}")
    public Response withdraw(@PathParam("id") Long id, @PathParam("amount") BigDecimal amount) {
        dao.withdraw(id, amount);
        return Response.ok().build();
    }
}

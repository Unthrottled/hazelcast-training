package space.cyclic.reference.app.rest;

import space.cyclic.reference.beans.HazelcastSingleton;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

@Stateless
@Path("/admin")
public class HazelcastAdmin {
    @EJB
    HazelcastSingleton hazelcastSingleton;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/get/thing")
    public String getThing() throws ExecutionException, InterruptedException {
        return hazelcastSingleton.getThing().get();
    }
}

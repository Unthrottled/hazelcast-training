package space.cyclic.reference.app.rest;

import space.cyclic.reference.beans.HazelcastSingleton;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.concurrent.ExecutionException;

@Path("/admin")
public class HazelcastAdmin {
    @EJB
    HazelcastSingleton hazelcastSingleton;

    @GET
    @Path("/get/thing")
    public String getThing() throws ExecutionException, InterruptedException {
        return hazelcastSingleton.getThing().get();
    }
}

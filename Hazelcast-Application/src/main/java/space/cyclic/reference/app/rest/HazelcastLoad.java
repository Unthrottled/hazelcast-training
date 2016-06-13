package space.cyclic.reference.app.rest;

import space.cyclic.reference.beans.load.HazelcastStringLoad;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/load")
public class HazelcastLoad {
    @Inject
    HazelcastStringLoad hazelcastStringLoad;

    @POST
    @Produces("text/plain")
    @Path("/list/string")
    public String loadList(List<String> strings) throws ExecutionException, InterruptedException {
        return hazelcastStringLoad.loadStrings(strings);
    }
}

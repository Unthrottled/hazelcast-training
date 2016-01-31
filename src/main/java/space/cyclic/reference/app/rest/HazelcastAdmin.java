package space.cyclic.reference.app.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/admin")
public class HazelcastAdmin {

    @GET
    @Path("/get/thing")
    public String getThing(){
        return "thing works";
    }
}

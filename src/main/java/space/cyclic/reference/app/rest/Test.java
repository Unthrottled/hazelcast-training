package space.cyclic.reference.app.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/test")
public class Test {

    @GET
    @Path("/thing")
    public String getThing(){
        return "thing works";
    }
}

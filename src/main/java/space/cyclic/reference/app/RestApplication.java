package space.cyclic.reference.app;

import space.cyclic.reference.app.rest.HazelcastAdmin;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class RestApplication extends Application {
    private Set<Object> singletons = new HashSet<>();

    public RestApplication() {
        singletons.add(new HazelcastAdmin());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
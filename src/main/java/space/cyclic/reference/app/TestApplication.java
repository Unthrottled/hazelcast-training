package space.cyclic.reference.app;

import space.cyclic.reference.app.rest.Test;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class TestApplication extends Application {
    private Set<Object> singletons = new HashSet<Object>();

    public TestApplication() {
        singletons.add(new Test());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
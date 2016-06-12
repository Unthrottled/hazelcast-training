package space.cyclic.reference.beans;

import space.cyclic.reference.interfaces.SuperBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

@ApplicationScoped
public class HazelcastJobs {
    @Inject @SuperBean
    HazelcastSingleton hazelcastSingleton;

    public HazelcastJobs() {
    }

    public Map<String, String> getThing(){
        return Collections.emptyMap();
    }
}

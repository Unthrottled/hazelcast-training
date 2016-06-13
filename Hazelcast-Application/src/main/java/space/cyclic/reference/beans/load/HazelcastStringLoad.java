package space.cyclic.reference.beans.load;

import space.cyclic.reference.beans.HazelcastSingleton;
import space.cyclic.reference.interfaces.SuperBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class HazelcastStringLoad {
    @Inject @SuperBean
    HazelcastSingleton hazelcastSingleton;
    public String loadStrings(List<String> strings) {
        if (Objects.nonNull(strings) && !strings.isEmpty()){
            hazelcastSingleton.getTestList().addAll(strings.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        return "Does loaded, better message in future";
    }
}

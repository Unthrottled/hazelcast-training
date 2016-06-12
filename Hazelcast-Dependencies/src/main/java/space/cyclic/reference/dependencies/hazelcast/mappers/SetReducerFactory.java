package space.cyclic.reference.dependencies.hazelcast.mappers;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SetReducerFactory <T, K> implements ReducerFactory<T, K, Set<K>> {
    @Override
    public Reducer<K, Set<K>> newReducer(T t) {
        return new SetReducer();
    }

    private class SetReducer extends Reducer<K, Set<K>> {
        private Set<K> setToReturn;

        public SetReducer() {
            setToReturn = new HashSet<>();
        }

        @Override
        public void reduce(K k) {
            if (Objects.nonNull(k)){
                setToReturn.add(k);
            }
        }

        @Override
        public Set<K> finalizeReduce() {
            return setToReturn;
        }
    }
}

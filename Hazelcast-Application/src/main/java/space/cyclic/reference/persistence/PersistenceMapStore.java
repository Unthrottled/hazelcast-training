package space.cyclic.reference.persistence;

import com.hazelcast.core.MapStore;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Collection;
import java.util.Map;

public class PersistenceMapStore implements MapStore<Long, Integer>{
    private final MongoDatabase mongoDatabase;
    public PersistenceMapStore() {
        MongoClient mongoClient = new MongoClient();
        this.mongoDatabase = mongoClient.getDatabase("test");
    }

    @Override
    public void store(Long aLong, Integer integer) {

    }

    @Override
    public void storeAll(Map<Long, Integer> map) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void deleteAll(Collection<Long> collection) {

    }

    @Override
    public Integer load(Long aLong) {
        return null;
    }

    @Override
    public Map<Long, Integer> loadAll(Collection<Long> collection) {
        return null;
    }

    @Override
    public Iterable<Long> loadAllKeys() {
        return null;
    }
}

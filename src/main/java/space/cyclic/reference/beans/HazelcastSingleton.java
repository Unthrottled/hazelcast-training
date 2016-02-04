package space.cyclic.reference.beans;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.IdGenerator;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.File;
import java.util.concurrent.Future;

@Startup
@Singleton
public class HazelcastSingleton {
    private static Logger logger = Logger.getLogger(HazelcastSingleton.class);
    private static final String queueQ = "queueQ";

    HazelcastInstance hazelcastMemberOne;
    HazelcastInstance hazelcastMemberTwo;

    @PostConstruct
    public void startHazelcastNode(){
        String thing = getClass().getClassLoader().getResource("hazelcast-client.xml").getFile();
        Config hazelcastConfig = new Config().setConfigurationFile(new File(thing));

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPort(9701);
        hazelcastConfig.setNetworkConfig(networkConfig);

        ExecutorConfig executorConfig = new ExecutorConfig()
                .setName("space.cyclic.reference.bestExecutor")
                .setPoolSize(15);
        hazelcastConfig.addExecutorConfig(executorConfig);

        //Wildcard Configurations
        MapConfig mapConfig = new MapConfig()
                .setName("space.cyclic.reference.bestMap*")
                .setBackupCount(1)
                .setTimeToLiveSeconds(0);

        hazelcastConfig.addMapConfig(mapConfig);

        hazelcastConfig.setProperty("hazelcast.logging.type", "log4j");

        hazelcastMemberOne = Hazelcast.newHazelcastInstance(hazelcastConfig);

        NetworkConfig networkConfigMemberTwo = new NetworkConfig();
        networkConfigMemberTwo.setPort(9702);
        hazelcastConfig.setNetworkConfig(networkConfigMemberTwo);

        hazelcastConfig.setNetworkConfig(networkConfigMemberTwo);
        hazelcastMemberTwo = Hazelcast.newHazelcastInstance(hazelcastConfig);

        logger.warn("Members Initialized");
    }

    @PreDestroy
    public void tidyUp(){
        hazelcastMemberOne.shutdown();
        hazelcastMemberTwo.shutdown();
    }

    @Asynchronous
    public Future<String> getThing() {
        return new AsyncResult<>(String.format("hazelcast thing works\nidGenOne: %d\nidGenTwo: %d",
                getIdGenForMemberOne(), getIdGenForMemberTwo()));
    }

    @Asynchronous
    public Future<String> getDestroyThing(){
        StringBuilder toReturn = new StringBuilder();
        IQueue<String> queueOne = getQueueQMemberTwo();
        IQueue<String> queueTwo = getQueueQMemberOne();
        queueTwo.add("Best String Ever");
        toReturn.append("Queue One Contents: ").append(queueOne.peek()).append('\n');
        toReturn.append("Queue Two Contents: ").append(queueTwo.peek()).append('\n');
        toReturn.append("Destroying Queue One Instance").append('\n');
        queueOne.destroy();
        toReturn.append("Queue One Contents after destruction: ").append(queueOne.peek()).append('\n');
        toReturn.append("Queue Two Contents after destruction: ").append(queueTwo.peek()).append('\n');
        return new AsyncResult<>(toReturn.toString());
    }


    public IQueue<String> getQueueQMemberOne(){
        return hazelcastMemberOne.getQueue(queueQ);
    }

    public IQueue<String> getQueueQMemberTwo(){
        return hazelcastMemberOne.getQueue(queueQ);
    }

    private long getIdGenForMemberOne(){
        return hazelcastMemberOne.getIdGenerator("idGenerator").newId();
    }

    private long getIdGenForMemberTwo(){
        return hazelcastMemberTwo.getIdGenerator("idGenerator").newId();
    }
}

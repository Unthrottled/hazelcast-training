package space.cyclic.reference.beans;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.*;
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
    private static final String semaphoreOfSolitude = "best semaphore";
    private static final String superSemaphore = "better than best semaphore";
    private static final String megaSemaphore = "ultra semaphore";
    private static final String partitionOne = "BestPartition";
    private static final String partitionTwo = "BestPartition";

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

        /**
         * Hazelcast config is not updatable: Once a HazelcastInstance is created, the Config that was used to
         * create that HazelcastInstance should not be updated. A lot of the internal configuration objects
         * are not thread-safe and there is no guarantee that a property is going to be read after it has been
         * read for the first time.
         */

        Config hazelcastConfigTwo = new Config().setConfigurationFile(new File(thing));

        NetworkConfig networkConfigMemberTwo = new NetworkConfig();
        networkConfigMemberTwo.setPort(9702);
        hazelcastConfigTwo.setNetworkConfig(networkConfigMemberTwo);

        hazelcastConfig.setNetworkConfig(networkConfigMemberTwo);
        hazelcastMemberTwo = Hazelcast.newHazelcastInstance(hazelcastConfigTwo);

        logger.warn("Members Initialized");
    }

    /**
     * HazelcastInstance.shutdown(): If you are not using your HazelcastInstance anymore, make sure
     * to shut it down by calling the shutdown() method on the HazelcastInstance. This will release all its
     * resources and end network communication.
     *
     * Hazelcast.shutdownAll(): This method is very practical for testing purposes if you do not have
     * control over the creation of Hazelcast instances, but you want to make sure that all instances are
     * being destroyed.
     *
     */

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

    /**
     * The other type is a non-partitioned data structure, like the IAtomicLong or the ISemaphore, where
     * only a single partition is responsible for storing the main instance.
     * @return
     */
    @Asynchronous
    public Future<String> getPartitionThing(){
        StringBuilder toReturn = new StringBuilder();
        ISemaphore semaphoreOne = getSemaphoreOfSolitude();
        ISemaphore semaphoreTwo = getSuperSemaphore();
        ISemaphore semaphoreThree = getMegaSemaphore();
        ISemaphore semaphoreFour = getSemaphoreOfSolitudeNoPartitionSpecified();
        toReturn.append("Semaphore One Partition: ").append(semaphoreOne.getPartitionKey()).append('\n');
        toReturn.append("Semaphore Two Partition: ").append(semaphoreTwo.getPartitionKey()).append('\n');
        toReturn.append("Semaphore Three Partition: ").append(semaphoreThree.getPartitionKey()).append('\n');
        toReturn.append("Semaphore Four Partition: ").append(semaphoreFour.getPartitionKey()).append('\n');
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

    public ISemaphore getSemaphoreOfSolitude() {
        return hazelcastMemberOne.getSemaphore(semaphoreOfSolitude + "@" + partitionOne);
    }

    public ISemaphore getSemaphoreOfSolitudeNoPartitionSpecified() {
        return hazelcastMemberOne.getSemaphore(semaphoreOfSolitude);
    }

    public ISemaphore getSuperSemaphore() {
        return hazelcastMemberOne.getSemaphore(superSemaphore+ "@" + partitionTwo);
    }

    public ISemaphore getMegaSemaphore(){
        return hazelcastMemberOne.getSemaphore(megaSemaphore);
    }
}

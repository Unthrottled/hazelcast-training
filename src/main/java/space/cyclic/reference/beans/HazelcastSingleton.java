package space.cyclic.reference.beans;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.*;
import org.apache.log4j.Logger;
import space.cyclic.reference.ProjectConstants;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import java.io.File;
import java.util.concurrent.Future;

@Startup
@Singleton
public class HazelcastSingleton {
    private static Logger logger = Logger.getLogger(HazelcastSingleton.class);
    private static final String QUEUE_Q = "QUEUE_Q";
    private static final String SEMAPHORE_OF_SOLITUDE = "best semaphore";
    private static final String SUPER_SEMAPHORE = "better than best semaphore";
    private static final String MEGA_SEMAPHORE = "ultra semaphore";
    private static final String PARTITION_ONE = "BestPartition";
    private static final String PARTITION_TWO = "BetterPartition";
    private static final String ATOMIC_LONG_ONE = "best atomic long";

    HazelcastInstance hazelcastMemberOne;
    HazelcastInstance hazelcastMemberTwo;

    @EJB
    SystemPropertyExtraction systemPropertyExtraction;

    @PostConstruct
    public void startHazelcastNode() {

        /**
         * Hazelcast config is not updatable: Once a HazelcastInstance is created, the Config that was used to
         * create that HazelcastInstance should not be updated. A lot of the internal configuration objects
         * are not thread-safe and there is no guarantee that a property is going to be read after it has been
         * read for the first time.
         */

        Config hazelcastConfig = new Config().setConfigurationFile(
                new File(systemPropertyExtraction.getProperty(ProjectConstants.HAZELCAST_CLIENT)));

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

        hazelcastMemberTwo = Hazelcast.newHazelcastInstance(hazelcastConfig);

        logger.warn("Members Initialized");
    }

    /**
     * HazelcastInstance.shutdown(): If you are not using your HazelcastInstance anymore, make sure
     * to shut it down by calling the shutdown() method on the HazelcastInstance. This will release all its
     * resources and end network communication.
     * <p/>
     * Hazelcast.shutdownAll(): This method is very practical for testing purposes if you do not have
     * control over the creation of Hazelcast instances, but you want to make sure that all instances are
     * being destroyed.
     */

    @PreDestroy
    public void tidyUp() {
        hazelcastMemberOne.shutdown();
        hazelcastMemberTwo.shutdown();
    }

    @Asynchronous
    public Future<String> getThing() {
        return new AsyncResult<>(String.format("hazelcast thing works\nidGenOne: %d\nidGenTwo: %d",
                getIdGenForMemberOne(), getIdGenForMemberTwo()));
    }

    @Asynchronous
    public Future<String> getDestroyThing() {
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
     *
     * @return
     */
    @Asynchronous
    public Future<String> getPartitionThing() {
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

    /**
     * apply: Applies the function to the value in the IAtomicLong without changing the actual value and
     returns the result.
     • alterAndGet: Alters the value stored in the IAtomicLong by applying the function, storing the result in
     the IAtomicLong and returning the result.
     • getAndAlter: Alters the value stored in the IAtomicLong by applying the function and returning the
     original value.
     • alter: Alters the value stored in the IAtomicLong by applying the function. This method will not send
     back a result.
     * @return
     */

    @Asynchronous
    public Future<String> getAtomicLongThing() {
        StringBuilder toReturn = new StringBuilder();
        IAtomicLong nuclear = getAtomicLong();
        IFunction<Long, Long> bestFunction = numberToApply -> {
            Long longToReturn = numberToApply;
            if (longToReturn < 10) {
                longToReturn += 2l;
            }
            else {
                longToReturn += 1l;
            }
            return longToReturn;
        };
        nuclear.getAndSet(1);
        Long bestFunctionResult = nuclear.apply(bestFunction);
        toReturn.append("apply.result:").append(bestFunctionResult).append('\n');
        toReturn.append("apply.value:").append(nuclear.get()).append('\n');

        nuclear.getAndSet(1);
        nuclear.alter(bestFunction);
        toReturn.append("alter.result:").append(nuclear.get()).append('\n');

        nuclear.getAndSet(1);
        bestFunctionResult = nuclear.alterAndGet(bestFunction);
        toReturn.append("alterAndGet.result:").append(bestFunctionResult).append('\n');
        toReturn.append("alterAndGet.value:").append(nuclear.get()).append('\n');

        nuclear.getAndSet(1);
        bestFunctionResult = nuclear.getAndAlter(bestFunction);
        toReturn.append("getAndAlter.result:").append(bestFunctionResult).append('\n');
        toReturn.append("getAndAlter.value:").append(nuclear.get()).append('\n');

        return new AsyncResult<>(toReturn.toString());
    }


    public IQueue<String> getQueueQMemberOne() {
        return hazelcastMemberOne.getQueue(QUEUE_Q);
    }

    public IQueue<String> getQueueQMemberTwo() {
        return hazelcastMemberOne.getQueue(QUEUE_Q);
    }

    private long getIdGenForMemberOne() {
        return hazelcastMemberOne.getIdGenerator("idGenerator").newId();
    }

    private long getIdGenForMemberTwo() {
        return hazelcastMemberTwo.getIdGenerator("idGenerator").newId();
    }

    public ISemaphore getSemaphoreOfSolitude() {
        return hazelcastMemberOne.getSemaphore(SEMAPHORE_OF_SOLITUDE + "@" + PARTITION_ONE);
    }

    public ISemaphore getSemaphoreOfSolitudeNoPartitionSpecified() {
        return hazelcastMemberOne.getSemaphore(SEMAPHORE_OF_SOLITUDE);
    }

    public ISemaphore getSuperSemaphore() {
        return hazelcastMemberOne.getSemaphore(SUPER_SEMAPHORE + "@" + PARTITION_TWO);
    }

    public ISemaphore getMegaSemaphore() {
        return hazelcastMemberOne.getSemaphore(MEGA_SEMAPHORE);
    }

    public IAtomicLong getAtomicLong() {
        return hazelcastMemberTwo.getAtomicLong(ATOMIC_LONG_ONE);
    }
}

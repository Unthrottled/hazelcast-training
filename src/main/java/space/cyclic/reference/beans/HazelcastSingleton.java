package space.cyclic.reference.beans;

import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
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

    HazelcastInstance hazelcastMemberOne;
    HazelcastInstance hazelcastMemberTwo;

    @PostConstruct
    public void startHazelcastNode(){
        String thing = getClass().getClassLoader().getResource("hazelcast-client.xml").getFile();
        Config hazelcastConfig = new Config().setConfigurationFile(new File(thing));
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

        NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setPort(9702);
        hazelcastConfig.setNetworkConfig(networkConfig);

        hazelcastConfig.setNetworkConfig(networkConfig);
        hazelcastMemberTwo = Hazelcast.newHazelcastInstance(hazelcastConfig);

        logger.warn("Members Initialized");
    }

    @Asynchronous
    public Future<String> getThing() {
        return new AsyncResult<>("hazelcast thing works");
    }
}

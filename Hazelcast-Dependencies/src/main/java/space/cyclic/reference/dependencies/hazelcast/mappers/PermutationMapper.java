package space.cyclic.reference.dependencies.hazelcast.mappers;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class PermutationMapper implements Mapper<String, String, String, String> {

    @Override
    public void map(String keyIn, String valueIn, Context<String, String> context) {

    }
}

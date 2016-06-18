package space.cyclic.reference.dependencies.hazelcast.mappers;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PermutationMapper implements Mapper<String, String, String, String> {

    @Override
    public void map(String keyIn, String valueIn, Context<String, String> context) {
        getPermutations(valueIn).forEach(keyOut-> context.emit(keyOut, valueIn));
    }

    Collection<String> getPermutations(String string) {
        Collection<String> toReturn = new HashSet<>();
        for (int i = 0; i < string.length() - 1; ++i){
            for (int j = i + 1; j < string.length(); ++j){
                toReturn.add(string.substring(i, j));
                toReturn.add(string.substring(j-i, j));
            }
            toReturn.add(string.substring(i));

        }
        return toReturn;
    }
}

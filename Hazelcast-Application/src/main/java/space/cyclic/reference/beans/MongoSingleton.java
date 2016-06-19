package space.cyclic.reference.beans;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import space.cyclic.reference.interfaces.SuperBean;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.util.Arrays.asList;

@SuperBean
@Singleton
public class MongoSingleton {
    private final MongoDatabase mongoDatabase;
    public MongoSingleton() {
        MongoClient mongoClient = new MongoClient();
        this.mongoDatabase = mongoClient.getDatabase("test");
    }

    @PostConstruct
    public void init() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        /**
         * If the document passed to the insertOne method does not contain the _id field,
         * the driver automatically adds the field to the document and sets the field’s
         * value to a generated ObjectId.
         */
        mongoDatabase.getCollection("restaurants").insertOne(
                new Document("address",
                        new Document()
                                .append("street", "2 Avenue")
                                .append("zipcode", "10075")
                                .append("building", "1480")
                                .append("coord", asList(-73.9557413, 40.7720266)))
                        .append("borough", "Manhattan")
                        .append("cuisine", "Italian")
                        .append("grades", asList(
                                new Document()
                                        .append("date", format.parse("2014-10-01T00:00:00Z"))
                                        .append("grade", "A")
                                        .append("score", 11),
                                new Document()
                                        .append("date", format.parse("2014-01-16T00:00:00Z"))
                                        .append("grade", "B")
                                        .append("score", 17)))
                        .append("name", "Vella")
                        .append("restaurant_id", "41704620"));

        FindIterable<Document> thingICanIterateOver = mongoDatabase.getCollection("restaurants").find();
        thingICanIterateOver.forEach((Block<Document>) document -> {

        });
    }

    @Override
    public String toString(){
        return "Super Bean: MongoSingleton!!";
    }
}

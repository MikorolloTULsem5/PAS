package pas.gV.model.data.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import pas.gV.model.data.datahandling.entities.Entity;
import pas.gV.model.exceptions.MyMongoException;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.UUID;

public abstract class AbstractMongoRepository<T> implements AutoCloseable {

    private final ConnectionString connectionString = new ConnectionString(
            // Local with docker
            "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=replica_set_single"
            // MongoDB Atlas (cloud)
//            "mongodb+srv://Michal:ZvDI3RNUGeTKjHTU@atlascluster.pweqkng.mongodb.net/"
    );
    private final MongoCredential credential = MongoCredential.createCredential("admin", "admin",
            "adminpassword".toCharArray());

    private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
            .automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
            .build());
    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    public AbstractMongoRepository() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase("reserveACourt");
    }

    protected MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return mongoDatabase;
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    /*--------------------------------------------Additional----------------------------------------------------*/

    protected MongoCollection<? extends Entity> getCollection() {
        return null;
    }

    public String getCollectionName() {
        return null;
    }

    /*----------------------------------------------CRUD-------------------------------------------------------*/

    public abstract T create(T initObj);

    public abstract List<T> read(Bson filter);

    public List<T> readAll() {
        return this.read(Filters.empty());
    }

    public T readByUUID(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var list = this.read(filter);
        return !list.isEmpty() ? list.get(0) : null;
    }

    public boolean update(UUID uuid, String fieldName, Object value) {
        if (fieldName.equals("_id")) {
            throw new MyMongoException("Proba zmiany UUID!");
        }
        Bson filter = Filters.eq("_id", uuid.toString());
        Bson setUpdate = Updates.set(fieldName, value);
        UpdateResult result = this.getCollection().updateOne(filter, setUpdate);
        return result.getModifiedCount() != 0;
    }

    public abstract boolean updateByReplace(UUID uuid, T dto);

    public boolean delete(UUID uuid) {
        Bson filter = Filters.eq("_id", uuid.toString());
        var deletedObj = this.getCollection().findOneAndDelete(filter);
        return deletedObj != null;
    }
}

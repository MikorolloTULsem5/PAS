package nbd.gV.data.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import nbd.gV.data.datahandling.dto.CourtDTO;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.data.datahandling.mappers.CourtMapper;
import nbd.gV.exceptions.CourtException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.model.courts.Court;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.UUID;

@ApplicationScoped
public class CourtMongoRepository extends AbstractMongoRepository<CourtDTO> {

    static final String COLLECTION_NAME = "courts";

    public CourtMongoRepository() {
        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains(COLLECTION_NAME);
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "area",
                                        "basecost",
                                        "courtnumber",
                                        "rented"
                                    ],
                                    "properties": {
                                        "rented": {
                                            "bsonType": "int",
                                            "minimum": 0,
                                            "maximum": 1,
                                            "description": "Must be 1 for rented and 0 for available"
                                        }
                                    }
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection(COLLECTION_NAME, createCollectionOptions);
        }
    }

    @Override
    protected MongoCollection<CourtDTO> getCollection() {
        return getDatabase().getCollection(getCollectionName(), CourtDTO.class);
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }

    @Override
    public boolean delete(UUID uuid) {
        Bson filter = Filters.eq("courtid", uuid.toString());
        ClientSession clientSession = getMongoClient().startSession();
        try {
            ///TODO ty no nwm xdd
            clientSession.startTransaction();
            var reservation = this.getDatabase().getCollection(ReservationMongoRepository.COLLECTION_NAME, ReservationDTO.class).find(filter).first();
            if (reservation != null) {
                throw new IllegalStateException();
            }
            boolean result = super.delete(uuid);
            if (result) {
                clientSession.commitTransaction();
            } else {
                clientSession.abortTransaction();
            }
            return result;
        } catch (Exception exception) {
            clientSession.abortTransaction();
            clientSession.close();
            if (exception instanceof IllegalStateException ise) {
                throw ise;
            }
            throw new MyMongoException(exception.getMessage());
        } finally {
            clientSession.close();
        }
    }

//    @PostConstruct
//    private void init() {
//        create(CourtMapper.toMongoCourt(new Court(UUID.fromString("634d9130-0015-42bb-a70a-543dee846760"), 100, 100, 1)));
//        create(CourtMapper.toMongoCourt(new Court(UUID.fromString("fe6a35bb-7535-4c23-a259-a14ac0ccedba"),100, 200, 2)));
//        create(CourtMapper.toMongoCourt(new Court(UUID.fromString("30ac2027-dcc8-4af7-920f-831b51023bc9"),300, 200, 3)));
//        create(CourtMapper.toMongoCourt(new Court(UUID.fromString("d820d682-0f5d-46b7-9963-66291e5f64b0"),350, 100, 4)));
//        create(CourtMapper.toMongoCourt(new Court(UUID.fromString("2e9258b2-98dd-4f9a-8f73-6f4f56c2e618"),150, 200, 5)));
//    }
//
//    @PreDestroy
//    private void destroy() {
//        getCollection().deleteMany(Filters.empty());
//    }
}

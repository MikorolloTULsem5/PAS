package nbd.gV.data.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.result.UpdateResult;
import nbd.gV.data.datahandling.dto.CourtDTO;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.exceptions.MyMongoException;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.UUID;

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
            clientSession.startTransaction();
            var reservation = this.getDatabase().getCollection(ReservationMongoRepository.COLLECTION_NAME, ReservationDTO.class).find(filter).first();
            if (reservation != null) {
                return false;
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
            throw new MyMongoException(exception.getMessage());
        } finally {
            clientSession.close();
        }
    }

    ///TODO przepiac metode
    public boolean update(CourtDTO court){
        Bson filter = Filters.eq("_id", court.getCourtId());
        UpdateResult result = getCollection().replaceOne(filter,court);
        return result.getModifiedCount() != 0;
    }
}

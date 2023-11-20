package nbd.gV.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import nbd.gV.mappers.CourtMapper;
import org.bson.Document;

import java.util.ArrayList;

public class CourtMongoRepository extends AbstractMongoRepository<CourtMapper> {

    public CourtMongoRepository() {
        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains("courts");
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
            getDatabase().createCollection("courts", createCollectionOptions);
        }
    }

    @Override
    protected MongoCollection<CourtMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), CourtMapper.class);
    }

    @Override
    public String getCollectionName() {
        return "courts";
    }
}

package nbd.gV.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationOptions;
import nbd.gV.mappers.ClientMapper;
import org.bson.Document;

import java.util.ArrayList;

public class ClientMongoRepository extends AbstractMongoRepository<ClientMapper> {

    public ClientMongoRepository() {
        boolean collectionExists = getDatabase().listCollectionNames().into(new ArrayList<>()).contains("clients");
        if (!collectionExists) {
            ValidationOptions validationOptions = new ValidationOptions().validator(
                    Document.parse("""
                            {
                                "$jsonSchema": {
                                    "bsonType": "object",
                                    "required": [
                                        "firstname",
                                        "lastname",
                                        "personalid",
                                        "archive",
                                        "clienttype"
                                    ],
                                    "properties": {
                                        "personalid": {
                                            "bsonType": "string",
                                            "minimum": 11,
                                            "maximum": 11,
                                            "description": "Must contain exactly 11 digits"
                                        }
                                    }
                                }
                            }
                            """));
            CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions()
                    .validationOptions(validationOptions);
            getDatabase().createCollection("clients", createCollectionOptions);
        }
    }

    @Override
    protected MongoCollection<ClientMapper> getCollection() {
        return getDatabase().getCollection(getCollectionName(), ClientMapper.class);
    }

    @Override
    public String getCollectionName() {
        return "clients";
    }
}

package integrationtests;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import pas.gV.model.data.repositories.CourtMongoRepository;
import pas.gV.model.data.repositories.ReservationMongoRepository;
import pas.gV.model.data.repositories.UserMongoRepository;

import pas.gV.restapi.data.dto.AdminDTO;
import pas.gV.restapi.data.dto.ClientDTO;
import pas.gV.restapi.data.dto.CourtDTO;
import pas.gV.restapi.data.dto.ReservationDTO;
import pas.gV.restapi.data.dto.ResourceAdminDTO;

import pas.gV.restapi.services.CourtService;
import pas.gV.restapi.services.ReservationService;
import pas.gV.restapi.services.userservice.AdminService;
import pas.gV.restapi.services.userservice.ClientService;
import pas.gV.restapi.services.userservice.ResourceAdminService;

import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

public class NewCleaningClassForTests {
    private static final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder()
            .automatic(true)
            .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
            .build());

    private static final MongoClientSettings settings = MongoClientSettings.builder()
            .credential(MongoCredential.createCredential("admin", "admin", "adminpassword".toCharArray()))
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=replica_set_single"))
//            .applyConnectionString(new ConnectionString("mongodb+srv://Michal:ZvDI3RNUGeTKjHTU@atlascluster.pweqkng.mongodb.net/"))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .codecRegistry(CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    pojoCodecRegistry
            ))
            .build();
    private static final MongoDatabase mongoDatabase = MongoClients.create(settings).getDatabase("reserveACourt");

    static void cleanUsers() {
        mongoDatabase.getCollection("users").deleteMany(Filters.empty());
    }

    static void cleanCourts() {
        mongoDatabase.getCollection("courts").deleteMany(Filters.empty());
    }

    static void cleanReservations() {
        mongoDatabase.getCollection("reservations").deleteMany(Filters.empty());
    }

    static ClientDTO client1;
    static ClientDTO client2;
    static ClientDTO client3;
    static ClientDTO client4;

    static CourtDTO court1;
    static CourtDTO court2;
    static CourtDTO court3;
    static CourtDTO court4;
    static CourtDTO court5;

    static ReservationDTO reservation1;
    static ReservationDTO reservation2;
    static ReservationDTO reservation3;
    static ReservationDTO reservation4;
    static ReservationDTO reservation5;
    static ReservationDTO reservation6;
    static ReservationDTO reservation7;

    static final LocalDateTime dataStart = LocalDateTime.of(2023, Month.NOVEMBER, 30, 14, 20);

    static void cleanAll() {
        cleanReservations();
        cleanUsers();
        cleanCourts();
    }

    static void initClients() {
        ClientService clientServiceTest = new ClientService(new UserMongoRepository(), null);
        cleanUsers();
        client1 = clientServiceTest.registerClient("Adam", "Smith", "loginek", "password", "normal");
        client2 = clientServiceTest.registerClient("Eva", "Braun", "loginek13", "password", "athlete");
        client3 = clientServiceTest.registerClient("Michal", "Pi", "michas13", "password", "coach");
        client4 = clientServiceTest.registerClient("Peter", "Grif", "griffPet", "password", "normal");
    }

    static void initCourts() {
        CourtService courtServiceTest = new CourtService(new CourtMongoRepository());
        cleanCourts();
        court1 = courtServiceTest.registerCourt(100, 100, 1);
        court2 = courtServiceTest.registerCourt(100, 200, 2);
        court3 = courtServiceTest.registerCourt(300, 200, 3);
        court4 = courtServiceTest.registerCourt(300, 200, 4);
        court5 = courtServiceTest.registerCourt(300, 200, 6);
    }

    static void initReservations() {
        ReservationService reservationServiceTest = new ReservationService(new ReservationMongoRepository());
        cleanAll();
        initClients();
        initCourts();
        reservation1 = reservationServiceTest.makeReservation(UUID.fromString(client1.getId()), UUID.fromString(court1.getId()), dataStart);
        reservation2 = reservationServiceTest.makeReservation(UUID.fromString(client2.getId()), UUID.fromString(court2.getId()), dataStart);
        reservation3 = reservationServiceTest.makeReservation(UUID.fromString(client3.getId()), UUID.fromString(court3.getId()), LocalDateTime.of(2023, Month.NOVEMBER, 28, 14, 20));
        reservationServiceTest.returnCourt(UUID.fromString(court3.getId()), dataStart);

        //Extra for getters
        reservation4 = reservationServiceTest.makeReservation(UUID.fromString(client2.getId()), UUID.fromString(court3.getId()), LocalDateTime.of(2023, Month.NOVEMBER, 28, 15, 0));
        reservationServiceTest.returnCourt(UUID.fromString(court3.getId()), LocalDateTime.of(2023, Month.DECEMBER, 2, 12, 20));
        reservation5 = reservationServiceTest.makeReservation(UUID.fromString(client3.getId()), UUID.fromString(court4.getId()), dataStart);
        reservationServiceTest.returnCourt(UUID.fromString(court4.getId()), LocalDateTime.of(2023, Month.DECEMBER, 1, 14, 20));
        reservation6 = reservationServiceTest.makeReservation(UUID.fromString(client1.getId()), UUID.fromString(court3.getId()), LocalDateTime.of(2023, Month.DECEMBER, 15, 10, 0));
        reservation7 = reservationServiceTest.makeReservation(UUID.fromString(client3.getId()), UUID.fromString(court5.getId()), LocalDateTime.of(2023, Month.DECEMBER, 16, 10, 0));
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    static AdminDTO admin1;
    static AdminDTO admin2;

    static void initAdmins() {
        AdminService adminServiceServiceTest = new AdminService(new UserMongoRepository(), null);
        cleanUsers();
        admin1 = adminServiceServiceTest.registerAdmin("adminek1@1234", "adminek1@1234");
        admin2 = adminServiceServiceTest.registerAdmin("adminek2@9876", "adminek2@9876");
    }


    static ResourceAdminDTO adminRes1;
    static ResourceAdminDTO adminRes2;

    static void initResAdmins() {
        ResourceAdminService resourceAdminServiceTest = new ResourceAdminService(new UserMongoRepository(), null);
        cleanUsers();
        adminRes1 = resourceAdminServiceTest.registerResourceAdmin("adminekRes1@1234", "adminekRes1@1234");
        adminRes2 = resourceAdminServiceTest.registerResourceAdmin("adminekRes2@9876", "adminekRes2@9876");
    }

    @Test
    void test() {
//        cleanAll();
    }
}

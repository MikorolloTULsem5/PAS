//package integrationtests;
//
//import com.mongodb.client.model.Filters;
//import nbd.gV.data.datahandling.dto.ClientDTO;
//import nbd.gV.data.datahandling.dto.ReservationDTO;
//import nbd.gV.data.repositories.CourtMongoRepository;
//import nbd.gV.data.repositories.ReservationMongoRepository;
//import nbd.gV.data.repositories.UserMongoRepository;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.util.UUID;
//
//public class CleaningClass {
//    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
//    static final UserMongoRepository clientRepository = new UserMongoRepository();
//    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
//
//    @BeforeAll
//    static void cleanDB() {
//        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
//                ReservationDTO.class).deleteMany(Filters.empty());
//        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
//        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getCourtId())));
//    }
//
//    @Test
//    void test() {}
//}

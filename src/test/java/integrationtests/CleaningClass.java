//package integrationtests;
//
//import com.mongodb.client.model.Filters;
//import nbd.gV.data.datahandling.dto.ClientDTO;
//import nbd.gV.data.datahandling.dto.ReservationDTO;
//import nbd.gV.data.repositories.CourtMongoRepository;
//import nbd.gV.data.repositories.ReservationMongoRepository;
//import nbd.gV.data.repositories.UserMongoRepository;
//import nbd.gV.model.courts.Court;
//import nbd.gV.model.reservations.Reservation;
//import nbd.gV.model.users.Client;
//import nbd.gV.restapi.services.CourtService;
//import nbd.gV.restapi.services.ReservationService;
//import nbd.gV.restapi.services.userservice.ClientService;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.time.Month;
//import java.util.UUID;
//
//public class CleaningClass {
//    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
//    static final UserMongoRepository clientRepository = new UserMongoRepository();
//    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
//    static final ClientService clientServiceTest = new ClientService(clientRepository);
//    static final CourtService courtServiceTest = new CourtService(courtRepository);
//    static final ReservationService reservationServiceTest = new ReservationService(reservationRepository);
//
//    static Client client1;
//    static Client client2;
//    static Client client3;
//
//    static Court court1;
//    static Court court2;
//    static Court court3;
//
//    static Reservation reservation1;
//    static Reservation reservation2;
//    static Reservation reservation3;
//
//    static final LocalDateTime dataStart = LocalDateTime.of(2023, Month.NOVEMBER, 30, 14, 20);
//
//    static void clean() {
//        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
//                ReservationDTO.class).deleteMany(Filters.empty());
//        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
//        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getId())));
//    }
//
//    static void initClients() {
//        client1 = clientServiceTest.registerClient("Adam", "Smith", "loginek", "normal");
//        client2 = clientServiceTest.registerClient("Eva", "Braun", "loginek13", "athlete");
//        client3 = clientServiceTest.registerClient("Michal", "Pi", "michas13", "coach");
//    }
//
//    static void initCourts() {
//        court1 = courtServiceTest.registerCourt(100, 100, 1);
//        court2 = courtServiceTest.registerCourt(100, 200, 2);
//        court3 = courtServiceTest.registerCourt(300, 200, 3);
//    }
//
//    static void initReservations() {
//        reservation1 = reservationServiceTest.makeReservation(client1.getId(), court1.getId(), dataStart);
//        reservation2 = reservationServiceTest.makeReservation(client2.getId(), court2.getId(), dataStart);
//        reservation3 = reservationServiceTest.makeReservation(client3.getId(), court3.getId(), LocalDateTime.of(2023, Month.NOVEMBER, 28, 14, 20));
//        reservationServiceTest.returnCourt(court3.getId(), dataStart);
//    }
//
//    ///TODO wywalic
//    @Test
//    void cleanTest() {
////        clean();
////        initClients();
////        initCourts();
////        initReservations();
//    }
//
//}

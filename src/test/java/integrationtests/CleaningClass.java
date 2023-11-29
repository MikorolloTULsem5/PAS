package integrationtests;

import com.mongodb.client.model.Filters;
import nbd.gV.data.datahandling.dto.ClientDTO;
import nbd.gV.data.datahandling.dto.ReservationDTO;
import nbd.gV.data.repositories.CourtMongoRepository;
import nbd.gV.data.repositories.ReservationMongoRepository;
import nbd.gV.data.repositories.UserMongoRepository;
import nbd.gV.restapi.services.userservice.ClientService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class CleaningClass {
    static final ReservationMongoRepository reservationRepository = new ReservationMongoRepository();
    static final UserMongoRepository clientRepository = new UserMongoRepository();
    static final CourtMongoRepository courtRepository = new CourtMongoRepository();
    static final ClientService clientServiceTest = new ClientService(clientRepository);

    static void clean() {
        reservationRepository.getDatabase().getCollection(reservationRepository.getCollectionName(),
                ReservationDTO.class).deleteMany(Filters.empty());
        clientRepository.readAll(ClientDTO.class).forEach((mapper) -> clientRepository.delete(UUID.fromString(mapper.getId())));
        courtRepository.readAll().forEach((mapper) -> courtRepository.delete(UUID.fromString(mapper.getCourtId())));
    }

    static void initClients() {
        clientServiceTest.registerClient("Adam", "Smith", "loginek", "normal");
        clientServiceTest.registerClient("Eva", "Braun", "loginek13", "athlete");
        clientServiceTest.registerClient("Michal", "Pi", "michas13", "coach");
    }


    ///TODO wywalic
    @Test
    void cleanTest() {
        clean();
    }

}

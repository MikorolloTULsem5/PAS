package nbd.gV.clients;

import com.mongodb.client.model.Filters;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.exceptions.ClientException;
import nbd.gV.exceptions.MainException;
import nbd.gV.exceptions.MyMongoException;
import nbd.gV.mappers.ClientMapper;
import nbd.gV.repositories.ClientMongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientMongoRepository clientRepository;

    public ClientManager() {
        this.clientRepository = new ClientMongoRepository();
    }

    public Client registerClient(String firstName, String lastName, String personalID, ClientType clientType) {
        Client newClient = new Client(firstName, lastName, personalID, clientType);
        try {
            if (!clientRepository.read(Filters.eq("personalid", personalID)).isEmpty()) {
                throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie! - klient o tym numerze PESEL" +
                        "znajduje sie juz w bazie");
            }

            if (!clientRepository.create(ClientMapper.toMongoClient(newClient))) {
                throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie! - brak odpowiedzi");
            }
        } catch (MyMongoException exception) {
            throw new ClientException("Nie udalo sie zarejestrowac klienta w bazie!");
        }
        return newClient;
    }

    public void unregisterClient(Client client) {
        if (client == null) {
            throw new MainException("Nie mozna wyrejestrowac nieistniejacego klienta!");
        }
        try {
            client.setArchive(true);
            if (!clientRepository.update(client.getClientId(), "archive", true)) {
                client.setArchive(false);
                throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta.");
            }
        } catch (Exception exception) {
            client.setArchive(false);
            throw new ClientException("Nie udalo sie wyrejestrowac podanego klienta. - nieznany blad");
        }
    }

    public Client getClient(UUID clientID) {
        try {
            ClientMapper clientMapper = clientRepository.readByUUID(clientID);
            return clientMapper != null ? ClientMapper.fromMongoClient(clientMapper) : null;
        } catch (Exception exception) {
            throw new ClientException("Blad transakcji.");
        }
    }

    public List<Client> getAllClients() {
        try {
            List<Client> clientsList = new ArrayList<>();
            for (var el : clientRepository.readAll()) {
                clientsList.add(ClientMapper.fromMongoClient(el));
            }
            return clientsList;
        } catch (Exception exception) {
            throw new ClientException("Nie udalo sie uzyskac clientow.");
        }
    }

    public Client findClientByPersonalId(String personalId) {
        var list = clientRepository.read(Filters.eq("personalid", personalId));
        return !list.isEmpty() ? ClientMapper.fromMongoClient(list.get(0)) : null;
    }
}

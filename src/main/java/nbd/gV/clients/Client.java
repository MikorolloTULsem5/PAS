package nbd.gV.clients;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nbd.gV.clients.clienttype.ClientType;
import nbd.gV.exceptions.MainException;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Client {

    @Setter(AccessLevel.NONE)
    private UUID clientId;
    private String firstName;
    private String lastName;
    private String personalId;
    private boolean archive = false;
    private ClientType clientType;

    public Client(String firstName, String lastName, String personalId, ClientType clientType) {
        if (firstName.isEmpty() || lastName.isEmpty() || personalId.isEmpty() || clientType == null)
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");

        this.clientId = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.clientType = clientType;
    }

    public Client(UUID uuid, String firstName, String lastName, String personalId, ClientType clientType) {
        this(firstName, lastName, personalId, clientType);
        this.clientId = uuid;
    }

    public String getClientInfo() {
        return "Klient - %s %s o numerze PESEL %s\n".formatted(firstName, lastName, personalId);
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public int getClientMaxHours() {
        return clientType.getMaxHours();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return archive == client.archive &&
                Objects.equals(clientId, client.clientId) &&
                Objects.equals(firstName, client.firstName) &&
                Objects.equals(lastName, client.lastName) &&
                Objects.equals(personalId, client.personalId) &&
                Objects.equals(clientType.getClientTypeName(), client.clientType.getClientTypeName());
    }
}

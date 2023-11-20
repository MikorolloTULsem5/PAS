package nbd.gV.users;

import lombok.Getter;
import lombok.Setter;
import nbd.gV.users.clienttype.ClientType;
import nbd.gV.exceptions.MainException;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Client extends User {

    private String firstName;
    private String lastName;
    private String personalId;
    private ClientType clientType;

    public Client(UUID id, String login, String firstName, String lastName, String personalId, ClientType clientType) {
        super(id, login);
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.clientType = clientType;
    }

    ///TODO do refactoru 1 (zostawione dla kompatybilnosci wstecznej)
    public Client(String firstName, String lastName, String personalId, ClientType clientType) {
        super(UUID.randomUUID(), "exampleLogin");
        if (firstName.isEmpty() || lastName.isEmpty() || personalId.isEmpty() || clientType == null) {
            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.personalId = personalId;
        this.clientType = clientType;
    }

    ///TODO do refactoru 2 (zostawione dla kompatybilnosci wstecznej)
    public Client(UUID uuid, String firstName, String lastName, String personalId, ClientType clientType) {
        this(uuid, "exampleLogin", firstName, lastName, personalId, clientType);
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
        return Objects.equals(getId(), client.getId());
        ///TODO czy to spelnienie 1 wymagania z zadania (o byciu podstawa rownosci obiektow)???
//        return archive == client.archive &&
//                Objects.equals(id, client.id) &&
//                Objects.equals(firstName, client.firstName) &&
//                Objects.equals(lastName, client.lastName) &&
//                Objects.equals(personalId, client.personalId) &&
//                Objects.equals(clientType.getClientTypeName(), client.clientType.getClientTypeName());
    }
}

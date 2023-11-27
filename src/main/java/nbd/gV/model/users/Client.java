package nbd.gV.model.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nbd.gV.model.users.clienttype.ClientType;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Client extends User {

    private String firstName;
    private String lastName;
    private ClientType clientType;

    public Client(UUID id, String firstName, String lastName, String login, ClientType clientType) {
        super(id, login);

        ///TODO do wywalenia przy zmianie walidacji
//        if (firstName.isEmpty() || lastName.isEmpty() || login.isEmpty() || clientType == null) {
//            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");
//        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = clientType;
    }

    public double applyDiscount(double price) {
        return clientType.applyDiscount(price);
    }

    public int clientMaxHours() {
        return clientType.getMaxHours();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(getId(), client.getId());
        ///TODO czy to spelnienie 1 wymagania z zadania (o byciu podstawa rownosci obiektow)???
//        return isArchive() == client.isArchive() &&
//                Objects.equals(getId(), client.getId()) &&
//                Objects.equals(firstName, client.firstName) &&
//                Objects.equals(lastName, client.lastName) &&
//                Objects.equals(getLogin(), client.getLogin()) &&
//                Objects.equals(clientType.getClientTypeName(), client.clientType.getClientTypeName());
    }
}

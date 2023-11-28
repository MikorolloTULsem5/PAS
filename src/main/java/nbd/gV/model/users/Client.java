package nbd.gV.model.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
public class Client extends User {

    private enum ClientType {
        NORMAL(0, 3), ATHLETE(10, 6), COACH(20, 12);

        private final double discount;
        private final int maxHours;
        ClientType(double discount, int maxHours) {
            this.discount = discount;
            this.maxHours = maxHours;
        }

        public double applyDiscount() {
            return discount;
        }

        public int getMaxHours() {
            return maxHours;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @Getter
    @Setter
    private String firstName;
    @Getter
    @Setter
    private String lastName;
    private ClientType clientType;
    private String clientTypeName;

    public Client(UUID id, String firstName, String lastName, String login, String clientType) {
        super(id, login);

        ///TODO do wywalenia przy zmianie walidacji
//        if (firstName.isEmpty() || lastName.isEmpty() || login.isEmpty() || clientType == null) {
//            throw new MainException("Brakujacy parametr przy tworzeniu obiektu klienta!");
//        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.clientType = switch (clientType.toLowerCase()) {
            case "athlete" -> ClientType.ATHLETE;
            case "coach" -> ClientType.COACH;
            default -> ClientType.NORMAL;
        };
    }

    public double applyDiscount() {
        return clientType.applyDiscount();
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
    }

    public String getClientTypeName() {
        return clientType.toString();
    }

    public void setClientTypeName(String clientType) {
        this.clientType = switch (clientType.toLowerCase()) {
            case "athlete" -> ClientType.ATHLETE;
            case "coach" -> ClientType.COACH;
            default -> ClientType.NORMAL;
        };
    }
}

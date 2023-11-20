package nbd.gV.clients.clienttype;

public abstract class ClientType {

    private final String name;

    public ClientType() {
        name = this.getClass().getSimpleName();
    }

    public abstract double applyDiscount(double price);

    public abstract int getMaxHours();

    public String getClientTypeName() {
        return name;
    }

    public String getTypeInfo() {
        return "%s %s wynosi %d%n".formatted("Maksymalna liczba godzin rezerwacji dla typu",
                name, getMaxHours());
    }
}

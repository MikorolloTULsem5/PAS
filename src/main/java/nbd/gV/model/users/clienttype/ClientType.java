package nbd.gV.model.users.clienttype;

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
}

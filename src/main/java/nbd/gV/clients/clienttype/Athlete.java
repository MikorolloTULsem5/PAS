package nbd.gV.clients.clienttype;

public class Athlete extends ClientType {

    @Override
    public double applyDiscount(double price) {
        return 10;
    }

    @Override
    public int getMaxHours() {
        return 6;
    }
}

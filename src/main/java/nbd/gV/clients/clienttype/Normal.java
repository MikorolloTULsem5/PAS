package nbd.gV.clients.clienttype;


public class Normal extends ClientType {
    @Override
    public double applyDiscount(double price) {
        return 0;
    }

    @Override
    public int getMaxHours() {
        return 3;
    }
}

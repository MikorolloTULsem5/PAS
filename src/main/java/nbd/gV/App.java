package nbd.gV;

import nbd.gV.managers.ClientManager;
import nbd.gV.users.Client;
import nbd.gV.users.clienttype.Normal;

public class App {
    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();

        clientManager.registerClient("Adam", "Smith", "adamSmith123", new Normal());
        clientManager.registerClient("Eva", "Braun", "11Breva", new Normal());
        clientManager.registerClient("Michal", "Braun", "michal12Br", new Normal());

        System.out.println(clientManager.usersSize());
        clientManager.findClientByLoginFitting("12").forEach(Client::getClientInfo);
    }
}

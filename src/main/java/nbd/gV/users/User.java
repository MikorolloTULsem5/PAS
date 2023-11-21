package nbd.gV.users;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public abstract class User {

    private final UUID id;
    ///TODO wywalic pesel, zostawic login???
    private final String login;
    @Setter
    private boolean archive = false;

    public User(UUID id, String login) {
        this.id = id;
        this.login = login;
    }

    ///TODO dodac DTO dla Usera (zapewnienie dziedziczenia) oraz obslugi w Manadzerze i Repozytorium
}

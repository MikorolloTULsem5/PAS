package nbd.gV.users;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class User {

    @Setter(AccessLevel.NONE)
    private final UUID id;
    ///TODO wywalic pesel, zostawic login???
    private String login;
    private boolean archive = false;

    public User(UUID id, String login) {
        this.id = id;
        this.login = login;
    }

    ///TODO dodac DTO dla Usera (zapewnienie dziedziczenia) oraz obslugi w Manadzerze i Repozytorium
}

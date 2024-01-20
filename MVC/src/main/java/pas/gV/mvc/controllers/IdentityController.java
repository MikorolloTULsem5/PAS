package pas.gV.mvc.controllers;

import lombok.Getter;
import lombok.Setter;

import pas.gV.mvc.model.Client;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(value = "session")
@Component(value = "identityController")
public class IdentityController {
    public final String DEFAULT_CONTEXT_LOGIN = "Anonymous";
    @Getter
    @Setter
    private String contextLogin = DEFAULT_CONTEXT_LOGIN;

    @Getter
    @Setter
    private Client client = new Client();
}

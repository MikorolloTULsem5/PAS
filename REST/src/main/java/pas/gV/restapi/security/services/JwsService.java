package pas.gV.restapi.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pas.gV.restapi.data.dto.ClientDTO;

import java.security.Key;
import java.util.Map;

@Service
public class JwsService {

    @Value("${security.jwt.key}")
    private String secretKey;

    public String generateSignatureForUser(Map<String, Object> claims) {
        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateSignatureForClient(ClientDTO client) {
        return generateSignatureForUser(Map.of(
                "archive", client.isArchive(),
                "id", client.getId(),
                "login", client.getLogin(),
                "clientType", client.getClientType()
        ));
    }

    public boolean verifyClientSignature(String signature, ClientDTO client) {
        String currentSignature = this.generateSignatureForUser(Map.of(
                "archive", client.isArchive(),
                "id", client.getId(),
                "login", client.getLogin(),
                "clientType", client.getClientType()
        ));
        return signature.equals(currentSignature);
    }

    public String extractLogin(String signature) {
        return extractAllClaims(signature).get("login", String.class);
    }

    private Claims extractAllClaims(String signature) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(signature)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

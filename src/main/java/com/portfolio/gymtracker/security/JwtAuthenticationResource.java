package com.portfolio.gymtracker.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JwtAuthenticationResource {
    
    private JwtEncoder jwtEncoder;

    public JwtAuthenticationResource(JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/authenticate")
    public JwtReponse authenticate(Authentication authencation){
        return new JwtReponse(createToken(authencation));
    }

    private String createToken(Authentication authentication){
        var claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(60 * 15))
            .subject(authentication.getName())
            .claim("scope", createScope(authentication))
            .build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(parameters).getTokenValue();    
    }   

    private String createScope(Authentication authentication){
        return authentication.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.joining(" "));
    }
}

record JwtReponse(String token){}

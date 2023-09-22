package com.portfolio.gymtracker.security;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.source.JWKSource;


import javax.sql.DataSource;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsUtils;

@Configuration
public class JwtSecurityConfiguration {

    // @Bean
    // public CorsConfigurationSource corsConfigurationSource() {
    //     CorsConfiguration configuration = new CorsConfiguration();
    //     configuration.setAllowedOrigins(List.of("*")); // Set the allowed origins
    //     configuration.setAllowedMethods(List.of("*")); // Set the allowed HTTP methods
    //     configuration.addAllowedHeader("*");   // Set the allowed headers
    //     configuration.setAllowCredentials(true); // Allow credentials (if needed)

    //     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration); // Apply the configuration to all endpoints

    //     return source;
    // }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.sessionManagement(
            session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS
        ));

        // http.cors().configurationSource(corsConfigurationSource());
        http.cors();
        http.authorizeHttpRequests(auth -> {
                auth
                    // .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    // .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/authenticate")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/public/**")).permitAll()
                    
                    .anyRequest().authenticated();
            });
            // .authorizeRequests(
            //     auth -> 
            //         auth
            //             .antMatchers(HttpMethod.OPTIONS,"/**")
            //             .permitAll()
            //             .requestMatchers(PathRequest.toH2Console())
            //             .permitAll()
            //             .antMatchers("/h2-console/**")
            //             .permitAll()
            //             .anyRequest()
            //             .authenticated()); // (3)
        http.httpBasic();
        http.csrf().disable();

        http.headers().frameOptions().sameOrigin();

        http.oauth2ResourceServer(
            OAuth2ResourceServerConfigurer::jwt
        );

        return http.build();
    }

    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
            .build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource){

        var admin = User.withUsername("delienelu")
            .password("somepassword")
            .passwordEncoder(str -> passwordEncoder().encode(str))
            .roles("ADMIN", "MODER", "USER")
            .build();

        var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.createUser(admin);

        return jdbcUserDetailsManager;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public KeyPair keyPair(){
       try {
         var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
         keyPairGenerator.initialize(2048);
         return keyPairGenerator.generateKeyPair();
       } catch (Exception e) {
            throw new RuntimeException(e);
       }
    }

    @Bean
    public RSAKey rsaKey(KeyPair keyPair){
        return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
            .privateKey(keyPair.getPrivate())
            .keyID(UUID.randomUUID().toString())
            .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey){
        var jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
        
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws Exception{
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey())
            .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource){
        return new NimbusJwtEncoder(jwkSource);
    }
}

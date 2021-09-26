package info.ejava.assignments.security.race.config;

import info.ejava.assignments.security.race.security.AccountProperties;
import info.ejava.assignments.security.race.security.RaceAccounts;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfiguration {
    @Bean
    @ConfigurationProperties("race")
    public RaceAccounts accounts() {
        return new RaceAccounts();
    }
}

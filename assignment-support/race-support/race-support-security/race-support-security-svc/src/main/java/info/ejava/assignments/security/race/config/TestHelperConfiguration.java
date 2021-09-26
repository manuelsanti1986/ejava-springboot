package info.ejava.assignments.security.race.config;

import info.ejava.assignments.security.race.security.AccountProperties;
import info.ejava.assignments.security.race.security.RaceAccounts;
import info.ejava.examples.common.web.paging.RestTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("authorities")
@Slf4j
public class TestHelperConfiguration {
    @Bean
    public AccountProperties anAccount(RaceAccounts accounts) {
        if (accounts.getAccounts().size()>=1) {
            AccountProperties account = accounts.getAccounts().stream()
                    .filter(a->!a.getAuthorities().contains("ROLE_ADMIN"))
                    .filter(a->!a.getAuthorities().contains("ROLE_MGR"))
                    .filter(a->!a.getAuthorities().contains("PROXY"))
                    .findFirst()
                    .orElseThrow(()->new IllegalStateException("cannot find user without elevated roles"));
            log.info("using account({}) for anAuthUser", account);
            return account;
        } else {
            throw new IllegalStateException("no user.name/password or accounts specified");
        }
    }

    @Bean
    public AccountProperties mgrAccount(RaceAccounts accounts) {
        return findUserWithAuthority(accounts, "ROLE_MGR");
    }

    @Bean
    public AccountProperties adminAccount(RaceAccounts accounts) {
        return findUserWithAuthority(accounts, "ROLE_ADMIN");
    }

    private AccountProperties findUserWithAuthority(RaceAccounts accounts, String authority) {
        if (accounts.getAccounts().size()>=1) {
            AccountProperties account = accounts.getAccounts().stream()
                    .filter(a->a.getAuthorities().contains(authority))
                    .findFirst()
                    .orElseThrow(()->new IllegalStateException("cannot find user authority " + authority));
            log.info("using account({}) for %s authority", account, authority);
            return account;
        } else {
            throw new IllegalStateException("no user.name/password or accounts specified");
        }
    }

    @Bean
    @Qualifier("userMap")
    public Map<String, RestTemplate> authnUsers(RestTemplateBuilder builder, RaceAccounts accounts) {
        Map<String, RestTemplate> authnUsers = new HashMap<>();
        for (AccountProperties account: accounts.getAccounts()) {
            ClientHttpRequestInterceptor authn=
                    new BasicAuthenticationInterceptor(account.getUsername(), account.getPassword());
            authnUsers.put(account.getUsername(), new RestTemplateConfig().restTemplateDebug(builder, authn));
        }
        return authnUsers;
    }

    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder) {
        return new RestTemplateConfig().restTemplateDebug(builder);
    }
    @Bean
    public RestTemplate authnUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties anAccount) {
        return authnUsers.get(anAccount.getUsername());
    }
    @Bean
    public RestTemplate mgrUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties mgrAccount) {
        return authnUsers.get(mgrAccount.getUsername());
    }
    @Bean
    public RestTemplate adminUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties adminAccount) {
        return authnUsers.get(adminAccount.getUsername());
    }
}

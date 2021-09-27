package info.ejava.assignments.security.race.config;

import info.ejava.assignments.security.race.security.RaceAccounts;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {
        RacesSecurityConfiguration.class,
        RacersSecurityConfiguration.class,
        AuthorizationTestHelperConfiguration.class})
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthoritiesConfiguration {
    @Configuration
    @Profile("authorities")
    public class AuthoritiesSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(m->m.antMatchers("/api/**"));
            http.authorizeRequests(cfg->cfg.antMatchers(HttpMethod.GET).permitAll());
            http.authorizeRequests(cfg->cfg.anyRequest().authenticated());

            http.httpBasic();
            http.csrf(cfg->cfg.disable());
            http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }

        @Bean
        public UserDetailsService userDetailsService(PasswordEncoder encoder, RaceAccounts accounts) {
            User.UserBuilder builder = User.builder().passwordEncoder(encoder::encode);
            List<UserDetails> users = accounts.getAccounts().stream()
                    .map(a->builder.username(a.getUsername())
                            .password(a.getPassword())
                            .authorities(a.getAuthorities().toArray(new String[0]))
                            .build())
                    .collect(Collectors.toList());
            return new InMemoryUserDetailsManager(users);
        }
    }

}

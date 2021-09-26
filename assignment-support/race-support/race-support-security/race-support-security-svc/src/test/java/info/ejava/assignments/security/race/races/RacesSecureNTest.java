package info.ejava.assignments.security.race.races;

import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import info.ejava.assignments.api.race.client.races.RacesAPI;
import info.ejava.assignments.api.race.client.races.RacesAPIClient;
import info.ejava.assignments.security.race.config.AuthoritiesConfiguration;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes={AuthoritiesConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "debug=true")
@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("authorities")
public class RacesSecureNTest {
    private ServerConfig serverConfig;

    @BeforeEach
    void init(@LocalServerPort int port) {
        serverConfig = new ServerConfig().withPort(port).build();
    }

    @Nested
    class anonymous_user {
        private RacesAPI anonymousClient;
        @BeforeEach
        void init(@Autowired RestTemplate anonymousUser) {
            anonymousClient = new RacesAPIClient(anonymousUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_get_race() {
            //when
            RestClientResponseException ex = Assertions.assertThrows(
                    RestClientResponseException.class,
                    () -> anonymousClient.getRace("aRaceId"));
            //then - was not rejected because of identity
            then(ex.getRawStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }

        @Test
        void can_get_races() {
            //when
            ResponseEntity<RaceListDTO> response = anonymousClient.getRaces(1,0);
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Nested
    class authenticated_user {
        private RacesAPI authnClient;
        @BeforeEach
        void init(@Autowired RestTemplate authnUser) {
            authnClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_create_race() {
        }
        @Test
        void cannot_modify_another_owners_race() {
        }
        @Test
        void cannot_cancel_another_owners_race() {
        }
        @Test
        void cannot_delete_another_owners_race() {
        }
    }

    @Nested
    class race_owner {
        private RacesAPI authnClient;
        @BeforeEach
        void init(@Autowired RestTemplate authnUser) {
            authnClient = new RacesAPIClient(authnUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_modify_their_race() {
        }
        @Test
        void can_cancel_their_race() {
        }
        @Test
        void can_delete_their_race() {
        }
    }

    @Nested
    class user_having_role_mgr {
        private RacesAPI mgrClient;
        @BeforeEach
        void init(@Autowired RestTemplate mgrUser) {
            mgrClient = new RacesAPIClient(mgrUser, serverConfig, MediaType.APPLICATION_JSON);
        }

        @Test
        void can_delete_any_race() {
        }
        @Test
        void cannot_delete_all_races() {
        }
    }

    @Nested
    class user_having_role_admin {
        private RacesAPI adminClient;
        @BeforeEach
        void init(@Autowired RestTemplate adminUser) {
            adminClient = new RacesAPIClient(adminUser, serverConfig, MediaType.APPLICATION_JSON);
        }


        @Test
        void admin_can_delete_all_races() {
            //when
            ResponseEntity response = adminClient.deleteAllRaces();
            //then
            then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }
}

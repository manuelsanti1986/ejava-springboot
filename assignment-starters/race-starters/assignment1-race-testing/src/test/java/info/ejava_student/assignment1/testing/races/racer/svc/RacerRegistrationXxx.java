package info.ejava_student.assignment1.testing.races.racer.svc;


import info.ejava_student.assignment1.testing.races.racer.dto.RacerDTO;

public class RacerRegistrationXxx {
    private RacerRegistrationService subject;
    private RacerDTO validRacer;
    private RacerValidator validatorMock;

    void init() {
    }

    void can_register_valid_racer() {
        //when - registering a valid racer
        //then - racer will be validated and assigned an ID
    }

    void rejects_invalid_racer() {
        //given - an invalid racer
        //then - racer will be validated, fail, and results reported in an exception
    }
}

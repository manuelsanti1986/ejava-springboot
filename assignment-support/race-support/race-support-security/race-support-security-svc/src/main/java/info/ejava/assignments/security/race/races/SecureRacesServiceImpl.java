package info.ejava.assignments.security.race.races;

import info.ejava.assignments.api.race.client.races.RaceDTO;
import info.ejava.assignments.api.race.client.races.RaceListDTO;
import info.ejava.assignments.api.race.races.RacesService;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class SecureRacesServiceImpl implements RacesService {
    private final RacesService serviceImpl;

    protected String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : null;
    }
    protected boolean hasAuthority(String authority) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof UserDetails ?
                ((UserDetails) principal).getAuthorities().contains(new SimpleGrantedAuthority(authority)) :
                false;
    }
    protected void isOwnerOrAuthority(String raceId, String authority) {
        if (null==authority || !hasAuthority(authority)) {
            RaceDTO race = serviceImpl.getRace(raceId);
            if (!StringUtils.equals(race.getOwnername(), getUsername())) {
                throw new AccessDeniedException(
                        String.format("%s is not race owner or have %s authority", getUsername(), authority));
            }
        }
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public RaceDTO createRace(RaceDTO newRace) {
        newRace.setOwnername(getUsername());
        return serviceImpl.createRace(newRace);
    }

    @Override
    public RaceDTO getRace(String id) {
        return serviceImpl.getRace(id);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public RaceDTO updateRace(String id, RaceDTO updateRace) {
        isOwnerOrAuthority(id, null);
        return serviceImpl.updateRace(id, updateRace);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public RaceDTO cancelRace(String id) {
        isOwnerOrAuthority(id, null);
        return serviceImpl.cancelRace(id);
    }

    @Override
    public RaceListDTO getRaces(Integer pageSize, Integer pageNumber) {
        return serviceImpl.getRaces(pageSize, pageNumber);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteRace(String id) {
        isOwnerOrAuthority(id, "ROLE_MGR");
        serviceImpl.deleteRace(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAllRaces() {
        serviceImpl.deleteAllRaces();
    }
}

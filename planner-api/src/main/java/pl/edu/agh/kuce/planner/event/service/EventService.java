package pl.edu.agh.kuce.planner.event.service;

import org.springframework.stereotype.Service;
import pl.edu.agh.kuce.planner.auth.persistence.User;
import pl.edu.agh.kuce.planner.auth.persistence.UserRepository;
import pl.edu.agh.kuce.planner.auth.service.JwtService;
import pl.edu.agh.kuce.planner.event.dto.CreateRequest;
import pl.edu.agh.kuce.planner.event.dto.CreateResponse;
import pl.edu.agh.kuce.planner.event.dto.ListRequest;
import pl.edu.agh.kuce.planner.event.dto.ListResponse;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEvent;
import pl.edu.agh.kuce.planner.event.persistence.OneTimeEventRepository;

import java.util.LinkedList;
import java.util.Optional;

@Service
public class EventService {
    private final OneTimeEventRepository oneTimeEventRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public EventService(OneTimeEventRepository oneTimeEventRepository, JwtService jwtService, UserRepository userRepository) {
        this.oneTimeEventRepository = oneTimeEventRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public CreateResponse create(CreateRequest request) {
        Optional<Integer> id = getIdFromToken(request.token());

        if(id.isEmpty()){
            return new CreateResponse("Authentication error");
        }
        oneTimeEventRepository.save(new OneTimeEvent(
                id.get(),
                request.title(),
                request.amount(),
                request.timestamp()
                ));
        return new CreateResponse("Ok");
    }

    public ListResponse list(ListRequest request) {
        Optional<Integer> id = getIdFromToken(request.token());

        if(id.isEmpty()){
            return new ListResponse(new LinkedList<>());
        }
        return new ListResponse(oneTimeEventRepository.findByUserId(id.get()).stream().toList());
    }

    private Optional<Integer> getIdFromToken(String token){
        Optional<String> nick = jwtService.tryRetrieveNick(token);

        if(nick.isEmpty()){
            return Optional.empty();
        }
        Optional<User> user = userRepository.findOneByNickOrEmail(nick.get());

        if(user.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(user.get().getId());
    }
}

package co.com.sofka.cargame;

import co.com.sofka.business.generic.UseCaseHandler;
import co.com.sofka.business.repository.DomainEventRepository;
import co.com.sofka.business.support.RequestCommand;
import co.com.sofka.cargame.domain.juego.command.CrearJuegoCommand;
import co.com.sofka.cargame.domain.juego.command.InicarJuegoCommand;
import co.com.sofka.cargame.infra.services.JugadorScoreQueryService;
import co.com.sofka.cargame.usecase.CrearJuegoUseCase;
import co.com.sofka.cargame.usecase.InicarJuegoUseCase;
import co.com.sofka.cargame.usecase.model.JugadorScore;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.asyn.SubscriberEvent;
import co.com.sofka.infraestructure.repository.EventStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins="*")
public class JuegoController {
    @Autowired
    private SubscriberEvent subscriberEvent;
    @Autowired
    private EventStoreRepository eventStoreRepository;
    @Autowired
    private CrearJuegoUseCase crearJuegoUseCase;
    @Autowired
    private InicarJuegoUseCase inicarJuegoUseCase;

    @Autowired
    private JugadorScoreQueryService jugadoresScoreQueryService;

    @PostMapping("/crearJuego")
    public String crearJuego(@RequestBody CrearJuegoCommand command) {
        crearJuegoUseCase.addRepository(domainEventRepository());
        UseCaseHandler.getInstance()
                .asyncExecutor(crearJuegoUseCase, new RequestCommand<>(command))
                .subscribe(subscriberEvent);
        return command.getJuegoId();
    }

    @PostMapping("/iniciarJuego")
    public String iniciarJuego(@RequestBody InicarJuegoCommand command) {
        inicarJuegoUseCase.addRepository(domainEventRepository());
        UseCaseHandler.getInstance()
                .setIdentifyExecutor(command.getJuegoId())
                .asyncExecutor(inicarJuegoUseCase, new RequestCommand<>(command))
                .subscribe(subscriberEvent);
        return command.getJuegoId();
    }

    @GetMapping("/historialGanadores")
    public List<JugadorScore> historialGanadores() {
        return jugadoresScoreQueryService.getJugadoresConScore();
    }


    private DomainEventRepository domainEventRepository() {
        return new DomainEventRepository() {
            @Override
            public List<DomainEvent> getEventsBy(String aggregateId) {
                return eventStoreRepository.getEventsBy("juego", aggregateId);
            }

            @Override
            public List<DomainEvent> getEventsBy(String aggregateName, String aggregateRootId) {
                return eventStoreRepository.getEventsBy(aggregateName, aggregateRootId);
            }
        };
    }
}

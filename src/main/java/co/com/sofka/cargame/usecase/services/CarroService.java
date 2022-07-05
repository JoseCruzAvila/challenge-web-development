package co.com.sofka.cargame.usecase.services;

import co.com.sofka.cargame.domain.carro.values.CarroId;
import co.com.sofka.cargame.usecase.model.Carro;

import java.util.List;
import java.util.Map;

public interface CarroService {
    String getConductorIdPor(CarroId carroId);

    List<Carro> getCarrosPorConductores(Map<String, String> conductores);
}
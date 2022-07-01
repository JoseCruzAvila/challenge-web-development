package co.com.sofka.cargame.usecase.services;

import co.com.sofka.cargame.domain.juego.Jugador;
import co.com.sofka.cargame.domain.juego.values.JuegoId;
import co.com.sofka.cargame.usecase.model.JugadorScore;

import java.util.List;

public interface JuegoService {
    Integer getKilometros(JuegoId juegoId);
}
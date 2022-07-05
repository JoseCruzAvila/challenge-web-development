package co.com.sofka.cargame.usecase.model;

import java.util.Map;

public class Carro {
    protected String carroId;
    protected String color;
    protected String juegoId;
    private Map<String, String> conductor;

    public Carro() {

    }

    public String getCarroID() {
        return carroId;
    }

    public void setCarroID(String carroId) {
        this.carroId = carroId;
    }

    public Map<String, String> getConductor() {
        return conductor;
    }

    public void setConductor(String cedula, String nombre) {
        this.conductor = Map.of(cedula, nombre);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(String juegoId) {
        this.juegoId = juegoId;
    }

    @Override
    public String toString() {
        return "Carro {" +
                "CarroId='" + carroId + '\''+
                ",color='" + color + '\'' +
                ",juegoId='" + juegoId + '\'' +
                ",conductor='" + conductor.toString() + '\'' +
                '}';
    }
}

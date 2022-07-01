package co.com.sofka.cargame.usecase.model;

public class JugadorScore {

    private String jugadorId;
    private String nombre;
    private int puntos;

    public JugadorScore() {

    }

    public JugadorScore(String jugadorId, String nombre, int puntos) {
        this.jugadorId = jugadorId;
        this.nombre = nombre;
        this.puntos = puntos;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    @Override
    public String toString() {
        return "JugadorScore {" +
                "jugadorId='" + jugadorId + '\''+
                "nombre='" + nombre + '\'' +
                ", puntos='" + puntos + '\'' +
                '}';
    }
}

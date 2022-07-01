package co.com.sofka.cargame.infra.services;


import co.com.sofka.cargame.usecase.model.JugadorScore;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JugadorScoreQueryService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public JugadorScoreQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<JugadorScore> getJugadoresConScore() {
        var lookup = LookupOperation.newLookup()
                .from("juego.JugadorCreado")
                .localField("jugadorId.uuid")
                .foreignField("jugadorId.uuid")
                .as("jugador");
        var replaceRoot = Aggregation.replaceRoot(
                                                ObjectOperators.valueOf(Aggregation.ROOT)
                                                        .mergeWith(
                                                                ArrayOperators.ArrayElemAt
                                                                        .arrayOf("jugador")
                                                                        .elementAt(0)));
        var group = Aggregation.group("jugadorId", "nombre").count().as("score");

        var aggregation = Aggregation.newAggregation(
                lookup,
                replaceRoot,
                group
        );

        return mongoTemplate.aggregate(aggregation, "juego.PrimerLugarAsignado", String.class)
                .getMappedResults()
                .stream()
                .map(body -> new Gson().fromJson(body, JugadorScoreRecord.class))
                .map(jugadorScoreRecord -> {
                    var jugadorScore = new JugadorScore();
                    jugadorScore.setJugadorId(jugadorScoreRecord.get_Id().getJugadorId().getUuid());
                    jugadorScore.setNombre(jugadorScoreRecord.get_Id().getNombre().getValue());
                    jugadorScore.setPuntos(jugadorScoreRecord.getScore());

                    return jugadorScore;
                }).collect(Collectors.toList());
    }

    public static class JugadorScoreRecord {
        private Jugador _id;
        private int score;

        public Jugador get_Id() {
            return _id;
        }

        public Integer getScore() {
            return score;
        }

        public void set_Id(Jugador _id) {
            this._id = _id;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public static class Jugador {
            private JugadorId jugadorId;
            private Nombre nombre;

            public JugadorId getJugadorId() {
                return jugadorId;
            }

            public void setJugadorId(JugadorId jugadorId) {
                this.jugadorId = jugadorId;
            }

            public Nombre getNombre() {
                return nombre;
            }

            public void setNombre(Nombre nombre) {
                this.nombre = nombre;
            }

            public static class JugadorId {
                private String uuid;

                public String getUuid() {
                    return uuid;
                }

                public void setUuid(String uuid) {
                    this.uuid = uuid;
                }
            }

            public static class Nombre {
                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}

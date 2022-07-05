package co.com.sofka.cargame.infra.services;

import co.com.sofka.cargame.domain.carro.values.CarroId;
import co.com.sofka.cargame.usecase.services.CarroService;
import co.com.sofka.cargame.usecase.model.Carro;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class CarroQueryService implements CarroService {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CarroQueryService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String getConductorIdPor(CarroId carroId) {
        var query = new Query(where("aggregateRootId").is(carroId.value()));
        return Objects.requireNonNull(mongoTemplate.findOne(query, CarroRecord.class, "carro.ConductorAsignado"))
                .getCedula().getUuid();
    }

    @Override
    public List<Carro> getCarrosPorId(String juegoId) {
        var lookup = LookupOperation.newLookup()
                .from("carro.CarroCreado")
                .localField("aggregateRootId")
                .foreignField("carroId.uuid")
                .as("carro");
        var replaceRoot = Aggregation.replaceRoot(
                ObjectOperators.valueOf(Aggregation.ROOT)
                        .mergeWith(
                                ArrayOperators.ArrayElemAt
                                        .arrayOf("carro")
                                        .elementAt(0)));
        var project = Aggregation.project("nombre", "cedula", "carroId", "juegoId", "color");
        var match = Aggregation.match(Criteria.where("juegoId.uuid").is(juegoId));

        var aggregation = Aggregation.newAggregation(
                lookup,
                replaceRoot,
                project,
                match
        );

        return mongoTemplate.aggregate(aggregation, "carro.ConductorAsignado", String.class)
                .getMappedResults()
                .stream()
                .map(body -> new Gson().fromJson(body, FullCarroRecord.class))
                .map(fullCarroRecord -> {
                    var carro = new co.com.sofka.cargame.usecase.model.Carro();
                    carro.setCarroID(fullCarroRecord.getCarroID().getUuid());
                    carro.setColor(fullCarroRecord.getColor().getValue());
                    carro.setJuegoId(fullCarroRecord.getJuegoId().getUuid());
                    carro.setConductor(fullCarroRecord.getCedula().getUuid() ,fullCarroRecord.getNombre());

                    return carro;
                }).collect(Collectors.toList());
    }

    public static class FullCarroRecord {
        protected CarroID carroId;
        protected Color color;
        protected JuegoId juegoId;
        private String nombre;
        private Cedula cedula;

        public CarroID getCarroID() {
            return carroId;
        }

        public void setCarroID(CarroID carroId) {
            this.carroId = carroId;
        }
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Cedula getCedula() {
            return cedula;
        }

        public void setCedula(Cedula cedula) {
            this.cedula = cedula;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public JuegoId getJuegoId() {
            return juegoId;
        }

        public void setJuegoId(JuegoId juegoId) {
            this.juegoId = juegoId;
        }
    }

    public static class CarroRecord {
        private String nombre;
        private Cedula cedula;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public Cedula getCedula() {
            return cedula;
        }

        public void setCedula(Cedula cedula) {
            this.cedula = cedula;
        }
    }

    public static class CarroID {
        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    public static class Cedula {
        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    public static class JuegoId {
        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

    public static class Color {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String uuid) {
            this.value = value;
        }
    }
}

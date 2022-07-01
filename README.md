# Nats
docker run -d --name nats -p 4222:4222 nats 

# Rabbit
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management

# Mongo
docker run -d --name mongodb  -p 27017:27017 -v $HOME/data:/data/db mongo:3

`
curl -X POST localhost:8080/crearJuego -H 'Content-Type: application/json' -d '{ "kilometros": 3, "juegoId":"ffff-xxxx-gggg", "jugadores": { "112233": "Camilo andres", "4455443": "Pedro", "fffff": "Santiago" } }'
`

`
curl -X POST localhost:8080/iniciarJuego -H 'Content-Type: application/json' -d '{ "juegoId":"ffff-xxxx-gggg" }'
`
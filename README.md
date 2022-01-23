# BIL TOUNSI DEVOPS
## First Kafka intro

### Kafka infrastructure:

> Launch the infra by moving to docker folder, then simply run:
```sh
docker-compose up -d
```
> You have to create the network "tounsi-ms-infra" in your first run via:
```sh
docker network create tounsi-ms-infra
```

### Stream processing via the Kafka-Stream[Java]:
> Launch the SpringBoot app via:
```sh
./gradlew
```

### Make some noise [Python]:
>To push some data to the system, you need to setup the python env, this is easy thanks to the "pipenv" tool, google to install it if it isn't around:
```sh
pipenv install
pipenv shell
python simulator.py
```
Feel free to alter the data inside the simulator.py, feel free to play with the transaction id, amount etc.

### Visualization:
>To query the Kafka local store visit http://127.0.0.1:5000/bank-balance/[1234|balanceId]
>To visualize Kafka backstages visit http://127.0.0.1:9000 and http://127.0.0.1:8080, the first is Kafdrop, my favorite quick and light tool to check topics, and the last is Kafka UI which is a more sophisticatd tool to do complexe stuff.
>To play with Grafana visit http://127.0.0.1:3000, you can find the credentiel is the docker-compose.yml file 

✨Disclaimer✨
This project is built on top of the [bank-transactions] repo, my additions are:
1. Migrating to Gradle, I prefer it since I'm an old Android developer.
2. Adding AVRO serdes for the transaction entity and adapt the Stream processing logic.  
3. Integrating Kafka Ksqldb, Kafka Connect, Kafka Schema Registry, kafka-ui and Kafdrop.
4. Adding Grafana, a sample data-source and a dashboard.
5. Adding the Python sub-project to demonstrate the usage of the Kafka python client and to feed data easily.

That is it,
Feel free to join me on [Linkedin] or to submit a PR if any suggestion comes to your mind.
Visit the [bank-transactions] repo for more diagrams and explanation.
Enjoy upgrading your systems to the next level!

[linkedin]: <https://www.linkedin.com/in/hamdy-ben-salah-24686160/>
[bank-transactions]: <https://github.com/Programming-with-Mati/bank-transactions/>

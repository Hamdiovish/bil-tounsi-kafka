import os
from confluent_kafka.avro import AvroProducer
from confluent_kafka import avro, KafkaException, TopicPartition
import time

FG_KAFKA = {
	"BOOSTRAP_SERVERS" : os.environ.get('FG_BOOSTRAP_SERVERS', "localhost:29092"),
	"SCHEMA_REGISTRY"  : os.environ.get('FG_SCHEMA_REGISTRY', "http://localhost:8081"),
}

bootstrap_servers   = FG_KAFKA["BOOSTRAP_SERVERS"]
schema_registry     = FG_KAFKA["SCHEMA_REGISTRY"]


def load_avro_schema_from_file(key_file,value_file):
    key_schema = None
    value_schema = None
    if key_file != None:
        key_schema = avro.load("config/avro/" + key_file)
    if value_file != None:
        value_schema = avro.load("config/avro/" + value_file)
    return key_schema, value_schema

def push(marketdata_value):
    key_schema, value_schema = load_avro_schema_from_file(None, "sample.avro")

    avroProducer = AvroProducer({
        'bootstrap.servers': bootstrap_servers,
        'schema.registry.url': schema_registry
        }, default_key_schema=key_schema, default_value_schema=value_schema)
    avroProducer.produce(topic="bank-transactions", value=value, key=value["balanceId"])
    avroProducer.flush()

if __name__ == "__main__":
    print("Start feeding:")
    now = int(time.time()*1000)
    value = {}
    value["id"]="A001"
    value["balanceId"]=1234
    value["concept"]="Amazon"
    value["amount"]=-100
    value["time"]="22-01-2022 19:00:00"
    value["state"]="CREATED"
    push(value)
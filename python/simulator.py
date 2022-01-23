import os
import time
from confluent_kafka import avro, KafkaException, TopicPartition
from confluent_kafka.avro import CachedSchemaRegistryClient
from confluent_kafka.avro.serializer.message_serializer import MessageSerializer as AvroSerializer

from schema_registry.client import SchemaRegistryClient, schema
from fastavro.schema import load_schema
from confluent_kafka import Producer

FG_KAFKA = {
	"BOOSTRAP_SERVERS" : os.environ.get('FG_BOOSTRAP_SERVERS', "localhost:29092"),
	"SCHEMA_REGISTRY"  : os.environ.get('FG_SCHEMA_REGISTRY', "http://localhost:8081"),
}

bootstrap_servers   = FG_KAFKA["BOOSTRAP_SERVERS"]
schema_registry     = FG_KAFKA["SCHEMA_REGISTRY"]


def load_avro_schema_from_file(file_name):
    schema = None
    if file_name != None:
        schema = avro.load("config/avro/" + file_name)
    return schema


def push(value):
    topic="bank-transactions"

    schema_data = load_avro_schema_from_file("sample.avsc")
    client = CachedSchemaRegistryClient({"url":schema_registry})

    ser = AvroSerializer(client)
    serialize_avro = ser.encode_record_with_schema
    value_payload = serialize_avro(topic, schema_data, value, is_key=False)

    p = Producer({'bootstrap.servers': bootstrap_servers})

    bytes_key = value["balanceId"].to_bytes(8, 'big')
    p.produce(topic, key=bytes_key, value=value_payload)
    p.flush()



if __name__ == "__main__":
    print("Start feeding:")
    now = int(time.time()*1000)
    value = {}
    value["id"]="A001"
    value["balanceId"]=1234
    value["concept"]="Deposit"
    value["amount"]=100
    value["time"]="22-01-2022 19:00:00"
    value["state"]="CREATED"
    push(value)

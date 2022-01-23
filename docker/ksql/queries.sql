SET 'auto.offset.reset' = 'earliest';
CREATE SINK CONNECTOR FG_TX_PG_01 WITH (
    'connector.class'                     = 'io.confluent.connect.jdbc.JdbcSinkConnector',
    'connection.url'                      = 'jdbc:postgresql://pg_database:5432/biltounsi',
    'connection.user'                     = 'biltounsi',
    'connection.password'                 = 'biltounsi',
    'topics'                              = 'bank-transactions,rejected-transactions',
    'value.converter'                     = 'io.confluent.connect.avro.AvroConverter',
    'value.converter.schema.registry.url' = 'http://schema-registry:8081',
    'auto.create'                         = 'true',
    'auto.evolve'                         = 'true',
    'table.name.format'                   = 'bank-transactions',
    'transforms'                        = 'timestamp',
    'transforms.timestamp.type'         = 'org.apache.kafka.connect.transforms.TimestampConverter$Value',
    'transforms.timestamp.target.type'  = 'Timestamp',
    'transforms.timestamp.field'        = 'time',
    'transforms.timestamp.format'       = 'yyyy-MM-dd HH:mm:ss'
  );

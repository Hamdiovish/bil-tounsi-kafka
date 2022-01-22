package tn.biltounsi.kafka.biltounsi.config;

import java.util.HashMap;
import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import avro.tn.biltounsi.BankTransaction;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import tn.biltounsi.kafka.biltounsi.model.BankBalance;
import tn.biltounsi.kafka.biltounsi.model.JsonSerde;

@Configuration
public class StreamConfiguration {

	public static final String BANK_TRANSACTIONS = "bank-transactions";
	public static final String BANK_BALANCES = "bank-balances";
	public static final String REJECTED_TRANSACTIONS = "rejected-transactions";
	public static final String BANK_BALANCES_STORE = "bank-balances-store";

	@Value("${kafka.streams.host.info:localhost:8080}")
	private String kafkaStreamsHostInfo;
	@Value("${kafka.streams.state.dir:/tmp/kafka-streams/bank-balance-queries}")
	private String kafkaStreamsStateDir;
	@Value("${kafka.streams.schema.registry.url}")
	private String schemaRegistryUrl;

	@Bean
	public Properties kafkaStreamsConfiguration() {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-queries");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
		properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
		properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, kafkaStreamsHostInfo);
		properties.put(StreamsConfig.STATE_DIR_CONFIG, kafkaStreamsStateDir);
		return properties;
	}

	@Bean
	public KafkaStreams kafkaStreams(@Qualifier("kafkaStreamsConfiguration") Properties streamConfiguration) {
		var topology = buildTopology();
		var kafkaStreams = new KafkaStreams(topology, streamConfiguration);

		kafkaStreams.cleanUp();
		kafkaStreams.start();

		Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

		return kafkaStreams;
	}

	@Bean
	public HostInfo hostInfo() {
		var split = kafkaStreamsHostInfo.split(":");
		return new HostInfo(split[0], Integer.parseInt(split[1]));
	}

	public <T extends SpecificRecord> SpecificAvroSerde<T> getSpecificAvroSerde(boolean isKey) {
		final SpecificAvroSerde<T> specificAvroSerde = new SpecificAvroSerde<>();

		final HashMap<String, String> serdeConfig = new HashMap<>();
		serdeConfig.put("schema.registry.url", schemaRegistryUrl);

		specificAvroSerde.configure(serdeConfig, isKey);
		return specificAvroSerde;
	}

	@Bean
	public Topology buildTopology() {
		Serde<BankTransaction> bankTransactionSerdes = getSpecificAvroSerde(false);
		Serde<BankBalance> bankBalanceSerde = new JsonSerde<>(BankBalance.class);
		StreamsBuilder streamsBuilder = new StreamsBuilder();

		KStream<Long, BankBalance> bankBalancesStream = streamsBuilder
				.stream(BANK_TRANSACTIONS, Consumed.with(Serdes.Long(), bankTransactionSerdes)).groupByKey()
				.aggregate(BankBalance::new, (key, value, aggregate) -> aggregate.process(value),
						Materialized.<Long, BankBalance, KeyValueStore<Bytes, byte[]>>as(BANK_BALANCES_STORE)
								.withKeySerde(Serdes.Long()).withValueSerde(bankBalanceSerde))
				.toStream();
		bankBalancesStream.to(BANK_BALANCES, Produced.with(Serdes.Long(), bankBalanceSerde));

//		bankBalancesStream.mapValues((readOnlyKey, value) -> value.getLatestTransactions().first())
//				.filter((key, value) -> value.state == BankTransactionState.REJECTED)
//				.to(REJECTED_TRANSACTIONS, Produced.with(Serdes.Long(), bankTransactionSerdes));

		return streamsBuilder.build();
	}
}

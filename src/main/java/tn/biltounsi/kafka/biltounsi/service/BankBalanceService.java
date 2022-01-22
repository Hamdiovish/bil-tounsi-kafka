package tn.biltounsi.kafka.biltounsi.service;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.biltounsi.kafka.biltounsi.model.BankBalance;

@Service
public class BankBalanceService {

	private final KafkaStreams kafkaStreams;
	private final HostInfo hostInfo;
	public static final String BANK_BALANCES_STORE = "bank-balances-store";

	@Autowired
	public BankBalanceService(KafkaStreams kafkaStreams, HostInfo hostInfo) {
		this.kafkaStreams = kafkaStreams;
		this.hostInfo = hostInfo;
	}

	public BankBalance getBankBalance(Long bankBalanceId) {
		return getStore().get(bankBalanceId);
	}

	private ReadOnlyKeyValueStore<Long, BankBalance> getStore() {
		return kafkaStreams
				.store(StoreQueryParameters.fromNameAndType(BANK_BALANCES_STORE, QueryableStoreTypes.keyValueStore()));
	}
}

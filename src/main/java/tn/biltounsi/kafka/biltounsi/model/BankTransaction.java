package tn.biltounsi.kafka.biltounsi.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;  

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BankTransaction implements Comparable<BankTransaction> {

    private String id;
    private Long balanceId;
    private String concept;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "dd-MM-yyyy hh:mm:ss")
    public Date time;
    @Builder.Default
    public BankTransactionState state = BankTransactionState.CREATED;

    @Override
    public int compareTo(BankTransaction o) {
        var r = o.time.compareTo(this.time);
        if(r == 0) return o.id.compareTo(this.id);
        return r;
    }
    
    public BankTransaction(avro.tn.biltounsi.BankTransaction avroBankTrx) {
    	this.id = avroBankTrx.getId();
    	this.balanceId = (long) avroBankTrx.getBalanceId();
    	this.concept = avroBankTrx.getConcept();
    	this.amount = new BigDecimal(avroBankTrx.getAmount());
    	this.state = BankTransactionState.valueOf(avroBankTrx.getState());
    	try {
			this.time = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(avroBankTrx.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

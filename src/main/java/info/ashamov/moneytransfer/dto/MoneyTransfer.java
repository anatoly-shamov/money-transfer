package info.ashamov.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MoneyTransfer {
    @JsonProperty(required = true)
    private Long senderAccountId;

    @JsonProperty(required = true)
    private Long receiverAccountId;

    @JsonProperty(required = true)
    private BigDecimal amount;

    public MoneyTransfer() {
    }

    public MoneyTransfer(Long senderAccountId, Long receiverAccountId, BigDecimal amount) {
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
    }

    public Long getSenderAccountId() {
        return senderAccountId;
    }

    public void setSenderAccountId(Long senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public Long getReceiverAccountId() {
        return receiverAccountId;
    }

    public void setReceiverAccountId(Long receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoneyTransfer that = (MoneyTransfer) o;

        if (senderAccountId != null ? !senderAccountId.equals(that.senderAccountId) : that.senderAccountId != null)
            return false;
        if (receiverAccountId != null ? !receiverAccountId.equals(that.receiverAccountId) : that.receiverAccountId != null)
            return false;
        return amount != null ? amount.equals(that.amount) : that.amount == null;
    }

    @Override
    public int hashCode() {
        int result = senderAccountId != null ? senderAccountId.hashCode() : 0;
        result = 31 * result + (receiverAccountId != null ? receiverAccountId.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MoneyTransfer{" +
                "senderAccountId=" + senderAccountId +
                ", receiverAccountId=" + receiverAccountId +
                ", amount=" + amount +
                '}';
    }
}

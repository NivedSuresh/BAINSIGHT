package org.bainsight.processing.Model.Dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.bainsight.OrderType;
import org.bainsight.TransactionType;


/* The Brokers are requested to send each and every required ID
   field as String to ease the process for the Exchange */
@Builder
public record OrderRequest(

        @NotBlank(message = "Invalid trading symbol provided")
        String symbol,

        @NotBlank(message = "Unable to proceed the current order.")
        String exchange,

        @Min(value = 1, message = "Bid/Ask should have a minimum quantity of 1")
        Long quantity,

        @NotNull(message = "Invalid transaction type, user can either ask or bid")
        TransactionType transactionType,

        @NotNull(message = "Invalid order type, user can either send market orders or limit orders.")
        OrderType orderType,

        @NotNull(message = "Please provide a valid marketPrice to proceed with the order")
        Double price

) { }

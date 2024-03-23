package org.exchange.library.Dto.Order;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.exchange.library.Enums.OrderType;
import org.exchange.library.Enums.TransactionType;
import org.exchange.library.Enums.Validity;


/* The Brokers are requested to send each and every required ID
   field as String to ease the process for the Exchange */
@Builder
public record OrderRequest(
        @NotNull(message = "Invalid transaction type, user can either ask or bid")
        TransactionType transactionType,
        @NotNull(message = "Invalid order type, user can either send market orders or limit orders.")
        OrderType orderType,
        @NotBlank(message = "Invalid tradingSymbol provided")
        String symbol,
        @Min(value = 1, message = "Bid/Ask should have a minimum quantity of 1")
        Long quantity,
        @NotNull(message = "Please provide a valid marketPrice to proceed with the order")
        Double price,
        @NotNull(message = "Invalid order validity selected")
        Validity validity,
        Double triggerPrice
) { }

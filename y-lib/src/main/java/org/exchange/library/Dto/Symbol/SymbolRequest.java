package org.exchange.library.Dto.Symbol;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SymbolRequest {
    private Long id;
    @Pattern(regexp = "^\\S+$", message = "Please enter valid user information")
    @Size(min = 2, max = 10,
            message = "MarketRelated name shouldn't exceed 10 characters, and a minimum of 2 characters are expected.")
    private String tradingSymbol;
}

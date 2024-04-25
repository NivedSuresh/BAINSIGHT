package org.bainsight.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bainsight.*;
import org.bainsight.processing.Mapper.Mapper;
import org.bainsight.processing.Model.Dto.OrderRequest;
import org.bainsight.processing.Service.OrderProcessingService;
import org.exchange.library.Advice.Error;
import org.exchange.library.Advice.ErrorResponse;
import org.exchange.library.Utils.STRINGS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OrderProcessingApplicationTests {


	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	Mapper mapper;


	@Test
	void testPlaceOrder() throws Exception {

		String ucc = STRINGS.UCC;

		OrderRequest orderRequest = OrderRequest.builder()
				.orderType(OrderType.ORDER_TYPE_MARKET)
				.transactionType(TransactionType.ASK)
				.price(100.0)
				.exchange("NSE")
				.quantity(50L)
				.symbol("AAPL")
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/bainsight/order")
						.header("x-auth-user-id", ucc)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderRequest)))
				.andExpect(status().is(HttpStatus.ACCEPTED.value()))
				.andReturn()
				.getResponse()
				.getContentAsString();

	}


	/**
	 * INVALID UNIQUE CLIENT ID THUS ORDER WILL FAIL!
	 * */
	@Test
	void failedOrderPlacement() throws Exception {
		String ucc = UUID.randomUUID().toString();

		OrderRequest orderRequest = OrderRequest.builder()
				.orderType(OrderType.ORDER_TYPE_MARKET)
				.transactionType(TransactionType.ASK)
				.price(100.0)
				.exchange("NSE")
				.quantity(50L)
				.symbol("AAPL")
				.build();


		String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post("/api/bainsight/order")
						.header("x-auth-user-id", ucc)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderRequest)))
				.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
				.andReturn()
				.getResponse().getContentAsString();

		ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

		Assertions.assertEquals(Error.RISK_CHECK_FAILED, errorResponse.errorCode());
	}


}

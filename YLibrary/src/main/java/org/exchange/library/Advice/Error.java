package org.exchange.library.Advice;

public class Error {
    public static final String INVALID_CREDENTIALS = "EXCEPTION-1001";
    public static final String INVALID_REFRESH_TOKEN = "EXCEPTION-1002";
    public static final String DATABASE_INTERACTION_FAILED = "EXCEPTION-1003";
    public static final String MFA_INITIAL_FAILURE = "EXCEPTION-1004";
    public static final String UN_IDENTIFIED_EXCEPTION = "EXCEPTION-1004";
    public static final String ENTITY_ALREADY_EXISTS = "EXCEPTION-1005";
    public static final String INVALID_BROKER_UPDATE_REQUEST = "EXCEPTION-1006";
    public static final String CONFIRM_PASSWORD_MISMATCH = "EXCEPTION-1007";
    public static final String INVALID_JWT = "EXCEPTION-1008";
    public static final String INVALID_ORDER_CATEGORY_FOR_REQUEST = "EXCEPTION-1009";
    public static final String INVALID_ORDER_TYPE = "EXCEPTION-1010";
    public static final String SERVICE_NOT_IMPLEMENTED = "EXCEPTION-1011";
    public static final String INVALID_ORDER_REQUEST = "EXCEPTION-1012";
    public static final String NOT_ENOUGH_SECURITY_TO_SELL = "EXCEPTION-1013";
    public static final String NON_EXISTENT_ORDER = "EXCEPTION-1014";
    public static final String KAFKA_ORDER_BOOK_ADDITION_FAILURE = "EXCEPTION-1015";
    public static final String KAFKA_ORDER_BOOK_CANCELLATION_FAILURE = "EXCEPTION-1016";
    public static final String UNKNOWN_KAFKA_EXCEPTION = "EXCEPTION-1017";
    public static final String KAFKA_ORDER_REJECTION_FAILURE = "EXCEPTION-1018";
    public static final String NOT_ENOUGH_BALANCE_TO_BID = "EXCEPTION-1019";
    public static final String JWT_EXPIRED = "EXCEPTION-1020";
}

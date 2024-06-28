package org.exchange.library.Dto.Notification;

public record Notification<T extends Ucc>(String topic, String message, T data, NotificationStatus status) {}


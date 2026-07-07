package com.julio.bank.api.controller.dto;

public record EventRequestDto(String type, String origin, String destination, Long amount) {
}

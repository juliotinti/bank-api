package com.julio.bank.api.controller.dto;

public record ApiErrorResponse(int status, String error, String message, String path) {
}

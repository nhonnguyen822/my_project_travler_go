package com.example.tourtravelserver.enums;

public enum BookingStatus {
    PENDING, CONFIRMED, CANCELLED;

    public String getVietnameseStatus() {
        switch (this) {
            case PENDING:
                return "ĐANG CHỜ XÁC NHẬN";
            case CONFIRMED:
                return "ĐÃ XÁC NHẬN";
            case CANCELLED:
                return "ĐÃ HỦY";
            default:
                return this.name();
        }
    }

    public String getStatusIcon() {
        switch (this) {
            case PENDING:
                return "⏳";
            case CONFIRMED:
                return "✓";
            case CANCELLED:
                return "✗";
            default:
                return "";
        }
    }
}

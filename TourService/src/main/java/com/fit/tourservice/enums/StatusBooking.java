package com.fit.tourservice.enums;

public enum StatusBooking {
//    Đang chờ xác nhận: Đơn đặt tour đã được khách hàng gửi nhưng chưa được nhà cung cấp hoặc hệ thống xác nhận.
//    Đã xác nhận: Đơn đặt tour đã được nhà cung cấp hoặc hệ thống xác nhận.
//    Đã thanh toán: Khách hàng đã hoàn tất thanh toán cho tour.
//    Đang xử lý: Đơn đặt tour đang được xử lý để chuẩn bị cho tour.
//    Hoàn tất: Tour đã hoàn tất và khách hàng đã tham gia tour.
//    Đã hủy: Đơn đặt tour đã bị hủy bởi khách hàng hoặc nhà cung cấp.
//    Hoàn tiền: Hệ thống đã thực hiện hoàn tiền cho khách hàng nếu đơn đặt bị hủy hoặc có yêu cầu hoàn tiền.
//    Đang chờ thanh toán: Đơn đặt tour đã được xác nhận nhưng chưa hoàn tất thanh toán.
//    Đã từ chối: Đơn đặt tour bị từ chối vì một lý do nào đó (hết chỗ, không đủ điều kiện,...).
    PENDING_CONFIRMATION(0),  // Đang chờ xác nhận
    CONFIRMED(1),             // Đã xác nhận
    PAID(2),                  // Đã thanh toán
    PROCESSING(3),            // Đang xử lý
    COMPLETED(4),             // Hoàn tất
    CANCELLED(5),             // Đã hủy
    REFUNDED(6),              // Hoàn tiền
    PENDING_PAYMENT(7),       // Đang chờ thanh toán
    REJECTED(8);              // Đã từ chối

    private int value;

    public int getValue() {
        return value;
    }

    private StatusBooking(int value) {
        this.value = value;
    }
}

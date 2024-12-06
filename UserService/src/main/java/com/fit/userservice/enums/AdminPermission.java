package com.fit.userservice.enums;

public enum AdminPermission {
    //    Toàn quyền truy cập mọi tính năng của hệ thống.
    FULL_ACCESS(0),
    //    Quyền quản lý thông tin tour du lịch (tạo, sửa, xóa tour).
    MANAGE_TOURS(1),
    //    Quyền quản lý người dùng và quyền truy cập của họ.
    MANAGE_USERS(2),
    //    Quyền xem báo cáo về tài chính, số lượng tour đặt, đánh giá khách hàng.
    VIEW_REPORTS(3),
    //    Quyền chỉnh sửa nội dung trang web (mô tả tour, hình ảnh, blog, v.v.). viết lớp enum như nào
    EDIT_CONTENT(4);

    private int value;

    public int getValue() {
        return value;
    }

    AdminPermission(int value) {
        this.value = value;
    }
}

FROM rasa/rasa:3.6.20

# Thiết lập thư mục làm việc
WORKDIR /app

# Sao chép các tệp cần thiết vào container
COPY ./data /app/data
COPY ./actions /app/actions
COPY ./config.yml /app/config.yml
COPY ./domain.yml /app/domain.yml
COPY ./credentials.yml /app/credentials.yml
COPY ./endpoints.yml /app/endpoints.yml
COPY requirements.txt /app/requirements.txt
COPY entrypoint.sh /app/entrypoint.sh

# Chuyển sang người dùng root để cài đặt các phụ thuộc
USER root

# Cấp quyền thực thi cho script entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Thêm biến môi trường để tắt cảnh báo SQLAlchemy
ENV SQLALCHEMY_SILENCE_UBER_WARNING=1

# Cập nhật pip và cài đặt các phụ thuộc
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Mở các cổng cho API Rasa và Action Server
EXPOSE 5005 5055

## Huấn luyện mô hình Rasa
#RUN rasa train

# Đặt entrypoint là script
ENTRYPOINT ["/bin/bash", "/app/entrypoint.sh"]

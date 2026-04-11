# PayOS Integration - Hướng dẫn sử dụng (Updated)

## 1. Cấu hình
Thêm thông tin PayOS vào file `application.yml`:
```yaml
payment:
  payOS:
    clientId: YOUR_CLIENT_ID
    apiKey: YOUR_API_KEY
    checksumKey: YOUR_CHECKSUM_KEY
    returnUrl: http://localhost:4200/payment/success
    cancelUrl: http://localhost:4200/payment/cancel
```

## 2. API Endpoints (Updated)

### 2.1 Tạo Payment Link
```http
POST /api/v1/payments/payos/create-payment-link
Content-Type: application/json

{
  "amount": 50000,
  "description": "Thanh toán đơn hàng #12345",
  "returnUrl": "http://localhost:4200/payment/success",
  "cancelUrl": "http://localhost:4200/payment/cancel",
  "items": [
    {
      "name": "Sản phẩm A",
      "quantity": 2,
      "price": 25000
    }
  ]
}
```

**Response:**
```json
{
  "error": 0,
  "message": "success",
  "data": {
    "bin": "970415",
    "accountNumber": "12345678",
    "accountName": "NGUYEN VAN A",
    "amount": 50000,
    "description": "Thanh toán đơn hàng #12345",
    "orderCode": 123456,
    "currency": "VND",
    "paymentLinkId": "abc123def456",
    "status": "PENDING",
    "checkoutUrl": "https://pay.payos.vn/web/abc123def456",
    "qrCode": "data:image/png;base64,iVBORw0KGgo..."
  }
}
```

### 2.2 Tạo Embedded Payment Link
```http
POST /api/v1/payments/payos/create-embedded-payment-link
Content-Type: application/json

{
  "amount": 25000,
  "description": "Thanh toán embedded",
  "returnUrl": "http://localhost:4200/payment/success",
  "cancelUrl": "http://localhost:4200/payment/cancel",
  "items": [
    {
      "name": "Mỳ tôm Hảo Hảo ly",
      "quantity": 1,
      "price": 25000
    }
  ]
}
```

### 2.3 Lấy thông tin Payment
```http
GET /api/v1/payments/payos/payment-info/{orderCode}
```

**Response:**
```json
{
  "error": 0,
  "message": "ok",
  "data": {
    "id": "abc123def456",
    "orderCode": 123456,
    "amount": 50000,
    "amountPaid": 50000,
    "amountRemaining": 0,
    "status": "PAID",
    "createdAt": "2023-09-20T10:30:00.000Z",
    "transactions": [...]
  }
}
```

### 2.4 Hủy Payment
```http
PUT /api/v1/payments/payos/cancel/{orderCode}?reason=Khách hàng hủy
```

### 2.5 Webhook (PayOS gọi về)
```http
POST /api/v1/payments/payos/webhook
Content-Type: application/json

{
  "code": "00",
  "desc": "success",
  "data": {
    "orderCode": 123456,
    "amount": 50000,
    "description": "Thanh toán đơn hàng #12345",
    "accountNumber": "12345678",
    "reference": "FT22089123456789",
    "transactionDateTime": "2023-09-20T10:30:00.000Z"
  },
  "signature": "abc123..."
}
```

### 2.6 Confirm Webhook
```http
POST /api/v1/payments/payos/confirm-webhook
Content-Type: application/json

{
  "webhookUrl": "https://yourdomain.com/api/v1/payments/payos/webhook"
}
```

## 3. Cách sử dụng từ frontend

### 3.1 Tạo payment và chuyển hướng
```javascript
// Tạo payment link
const response = await fetch('/api/v1/payments/payos/create-payment-link', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    amount: 50000,
    description: 'Thanh toán đơn hàng #12345',
    returnUrl: 'http://localhost:4200/payment/success',
    cancelUrl: 'http://localhost:4200/payment/cancel',
    items: [{
      name: 'Sản phẩm A',
      quantity: 1,
      price: 50000
    }]
  })
});

const result = await response.json();

if (result.error === 0) {
  // Chuyển hướng tới trang thanh toán
  window.location.href = result.data.checkoutUrl;
} else {
  console.error('Lỗi tạo payment link:', result.message);
}
```

### 3.2 Hiển thị QR Code
```javascript
// Hiển thị QR code để khách quét
if (result.error === 0) {
  const qrImage = document.getElementById('qr-code');
  qrImage.src = result.data.qrCode;
}
```

### 3.3 Kiểm tra trạng thái payment
```javascript
// Polling để kiểm tra trạng thái thanh toán
const checkPaymentStatus = async (orderCode) => {
  const response = await fetch(`/api/v1/payments/payos/payment-info/${orderCode}`);
  const result = await response.json();
  
  if (result.error === 0) {
    return result.data.status;
  }
  return null;
};

// Sử dụng
const orderCode = 123456;
const interval = setInterval(async () => {
  const status = await checkPaymentStatus(orderCode);
  if (status === 'PAID') {
    clearInterval(interval);
    alert('Thanh toán thành công!');
    // Redirect hoặc update UI
  } else if (status === 'CANCELLED') {
    clearInterval(interval);
    alert('Thanh toán đã bị hủy!');
  }
}, 3000); // Check mỗi 3 giây
```

## 4. Test với Postman

### 4.1 Collection mẫu:
```json
{
  "info": {
    "name": "PayOS API Tests - Updated"
  },
  "item": [
    {
      "name": "Create Payment Link",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/v1/payments/payos/create-payment-link",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "raw": "{\n  \"amount\": 50000,\n  \"description\": \"Test payment\",\n  \"returnUrl\": \"http://localhost:4200/success\",\n  \"cancelUrl\": \"http://localhost:4200/cancel\",\n  \"items\": [\n    {\n      \"name\": \"Test Item\",\n      \"quantity\": 1,\n      \"price\": 50000\n    }\n  ]\n}"
        }
      }
    },
    {
      "name": "Get Payment Info",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/v1/payments/payos/payment-info/{{orderCode}}"
      }
    },
    {
      "name": "Cancel Payment",
      "request": {
        "method": "PUT",
        "url": "http://localhost:8080/api/v1/payments/payos/cancel/{{orderCode}}?reason=Test cancel"
      }
    }
  ]
}
```

## 5. Các thay đổi chính từ version trước:

### 5.1 Controller thay đổi:
- **Response format**: Sử dụng ObjectNode với format `{error, message, data}` thay vì ResponseEntity
- **Direct PayOS integration**: Inject PayOS bean trực tiếp thay vì qua Service layer
- **Error handling**: Consistent error response format
- **Method changes**: Cancel endpoint sử dụng PUT thay vì POST

### 5.2 Configuration đơn giản hóa:
- Sử dụng `@Value` thay vì `@ConfigurationProperties`
- Loại bỏ PayOSService layer để giảm complexity

### 5.3 Security updates:
- Thêm endpoint `/confirm-webhook`
- Thay đổi cancel endpoint từ POST sang PUT

## 6. Lưu ý quan trọng

- ⚠️ **Thay thế API keys**: Nhớ thay `YOUR_CLIENT_ID`, `YOUR_API_KEY`, `YOUR_CHECKSUM_KEY` bằng thông tin thật
- 🔄 **Response format**: Tất cả API trả về format `{error: 0/(-1), message: string, data: object}`
- 🛡️ **Webhook security**: Luôn verify webhook signature trước khi xử lý
- 📱 **Frontend handling**: Check `error` field trong response trước khi sử dụng `data`
- 🔗 **URL requirements**: Return và Cancel URLs phải accessible từ internet khi deploy production
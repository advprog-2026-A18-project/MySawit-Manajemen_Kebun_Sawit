# Manajemen-Kebun-Sawit-API

---

## Base URL
```
http://localhost:8082/api/kebun
```

---

## API Reference

### 1. Get All Kebun (List & Filter)

```
  GET /kebun
```

### Query Parameters
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `search_nama` | `string` | Filter by nama kebun |
| `search_kode` | `string` | Filter by kode_kebun |

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code (200, 400, 500, etc) |
| `message` | `string` | Success/error message |
| `data` | `array` | List of kebun objects |

---

### 2. Get Kebun Detail

```
  GET /kebun/{kode_kebun}
```

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code |
| `message` | `string` | Success/error message |
| `data` | `object` | Kebun object with mandor and supir details |

---

### 3. Get Supir by Filter

```
  GET /kebun/{kode_kebun}/supir
```

### Query Parameters
| Parameter | Type | Description |
| `search_nama` | `string` | Filter by nama supir |

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code |
| `message` | `string` | Success/error message |
| `data` | `array` | List of supir objects |

---

### 4. Create Kebun

```
  POST /kebun
```

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Request Body
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `kode_kebun` | `string` | **Required**. Unique kode kebun |
| `nama_kebun` | `string` | **Required**. Nama kebun |
| `luas_hektare` | `int` | **Required**. Luas dalam hectare |
| `koordinat` | `string` | **Required**. JSON format koordinat 4 titik |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code (201 for created) |
| `message` | `string` | Success/error message |
| `data` | `object` | Created kebun object |

---

### 5. Update Kebun

```
  PUT /kebun/{kode_kebun}
```

> **Note:** `kode_kebun` cannot be changed.

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Request Body
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `nama_kebun` | `string` | **Optional**. Nama kebun |
| `luas_hektare` | `int` | **Optional**. Luas dalam hectare |
| `koordinat` | `string` | **Optional**. JSON format koordinat |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code |
| `message` | `string` | Success/error message |
| `data` | `object` | Updated kebun object |

---

### 6. Delete Kebun

```
  DELETE /kebun/{kode_kebun}
```

> **Note:** Cannot delete if still bound to a Mandor.

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code (204 for success, 409 for conflict) |
| `message` | `string` | Success/error message |

---

### 7. Assign Mandor to Kebun

```
  POST /kebun/{kode_kebun}/mandor
```

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Request Body
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `mandor_id` | `string` | **Required**. ID of mandor from User Management Service |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code |
| `message` | `string` | Success/error message |
| `data` | `object` | Updated kebun with mandor |

---

### 8. Unassign Mandor from Kebun

```
  DELETE /kebun/{kode_kebun}/mandor
```

> **Note:** Must assign to another kebun.

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Query Parameters
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `new_kebun_id` | `string` | **Required**. kode_kebun baru |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code (400 if new_kebun_id not provided) |
| `message` | `string` | Success/error message |

---

### 9. Assign Supir to Kebun

```
  POST /kebun/{kode_kebun}/supir
```

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Request Body
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `supir_id` | `string` | **Required**. ID of supir from User Management Service |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code |
| `message` | `string` | Success/error message |
| `data` | `object` | Updated supir info |

---

### 10. Unassign Supir from Kebun

```
  DELETE /kebun/{kode_kebun}/supir/{supir_id}
```

> **Note:** Must assign to another kebun.

### Request Headers
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `Authorization` | `string` | **Required**. Bearer `<JWT_ACCESS_TOKEN>` |

### Query Parameters
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `new_kebun_id` | `string` | **Required**. kode_kebun baru |

### Response
| Parameter | Type | Description |
| :-------- | :--- | :---------- |
| `statusCode` | `int` | HTTP status code (400 if new_kebun_id not provided) |
| `message` | `string` | Success/error message |

---

## Response Format

### Success Response
```json
{
  "statusCode": 200,
  "message": "Success",
  "data": { ... }
}
```

### Error Response
```json
{
  "statusCode": 400,
  "message": "Error message",
  "data": null
}
```

### Common Status Codes
| Code | Description |
| :--- | :---------- |
| `200` | OK - Success |
| `201` | Created - Resource created |
| `204` | No Content - Success with no response body |
| `400` | Bad Request - Invalid input |
| `401` | Unauthorized - Invalid/missing token |
| `403` | Forbidden - No permission |
| `404` | Not Found - Resource doesn't exist |
| `409` | Conflict - Business logic error |
| `500` | Internal Server Error |

---
# Banking System

Aplicacion Bancaria:

- **Backend**: Java 17, Spring Boot 4, JPA, MySQL 8, MapStruct, Lombok, OpenPDF
- **Frontend**: Angular 17, Jest
- **Despliegue**: Docker Compose - MySQL, Back-end, Front-end con Nginx -
- **Validador API**: Postman v2.1

## Estructura

```
banking-system/
  banking-api/            # Spring Boot 4
  banking-app/            # Angular 17
  postman/                # Coleccion y environment
  BaseDatos.sql           # Script de creacion con datos de ejemplo
  docker-compose.yml      # Orquesta MySQL, back-end y front-end
```

## Desarrollo Backend

- **MVC** con capas: controller, service, repository, entity.
- **DTO y MapStruct** implementado con records para no exponer entidades.
- **Strategy** este patron de diseño se implementa para la logica de movimientos (`DepositoStrategy`, `RetiroStrategy`) con un `MovimientoStrategyFactory` que indexa los beans por tipo.
- **Singleton de configuracion** via `@ConfigurationProperties` (`BankingProperties`).
- **Exception Handler global** (`GlobalExceptionHandler`) que devuelve un `ApiError` general.
- **Validaciones a nivel de modelo** con `jakarta.validation`, `@NotBlank`, `@Min`, `@Pattern`, `@DecimalMin`, entre otros,  tanto en entidades como en DTOs.
- **Programacion funcional** el uso de streams, `Predicate`, `map/filter/reduce` y lambdas en servicios y reportes.
- **Herencia JPA JOINED**: `Cliente` extiende `Persona`.
- **Unit Test** con JUnit 5, Mockito y MockMvc; tests de servicios y de controladores.
- **Inteface UI**: Angular 17 (standalone components, Signals, Angular CLI)

## Desarrollo Frontend
La arquitectura se diseñó siguiendo principios de clean code, componentes standalone, servicios desacoplados, interceptores, y pruebas unitarias con Jest.
- **Angular 17 (standalone components, Signals, Angular CLI)**
- **Typescript**
- **RxJS (Observables, HttpClient)**
- **Angular Router**
- **HttpClient + HttpParams**
- **Jest para pruebas unitarias**
- **TestBed + HttpClientTestingModule**
- **Interceptors para manejo centralizado de errores**
- **NGX**

## Backend (integración)

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET/POST/PUT/PATCH/DELETE | `/api/clientes`, `/api/clientes/{clienteId}` | CRUD de clientes |
| GET/POST/PUT/PATCH/DELETE | `/api/cuentas`, `/api/cuentas/{numeroCuenta}` | CRUD de cuentas |
| GET/POST/DELETE | `/api/movimientos`, `/api/movimientos/{id}` | CRUD de movimientos |
| GET | `/api/reportes?clienteId=&desde=&hasta=` | Estado de cuenta JSON + PDF base64 |
| GET | `/api/reportes/pdf?clienteId=&desde=&hasta=` | Descarga directa del PDF |

## Infraestructura Frontend
- **Environment variables para manejar apiUrl**
- **Estructura modular por features:**
```aidl
features/
  clients/
  accounts/
  transactions/
  reports/
core/
  services/
  interceptors/
  models/

```
Swagger UI: http://localhost:8080/api/swagger-ui.html

## Reglas de negocio implementadas

- Credito (DEPOSITO) -> suma al `saldoDisponible`. Debito (RETIRO) -> resta.
- Si `saldoDisponible == 0` y se intenta debitar -> respuesta 400 con mensaje **"Saldo no disponible"**.
- Si `retirosDelDia + valor > limiteDiario (1000)` -> respuesta 400 con mensaje **"Cupo diario Excedido"**.
- El limite diario es configurable en `application.yml` (`banco.retiro.limite-diario`).

## Despliegue con Docker

```bash
cd banking-system
docker compose up --build
```

Servicios:
- MySQL: `localhost:3306` (db `banking_db`, user `banking_user`, pass `12345Bank`)
- Backend: http://localhost:8080/api
- Frontend: http://localhost:4200

El script `BaseDatos.sql` se ejecuta automaticamente la primera vez que arranca MySQL.

Para detener: `docker compose down`. Para borrar tambien la BD: `docker compose down -v`.

## Ejecucion sin Docker

### Backend

```bash
cd banking-api
mvn spring-boot:run
```

Es requisito tener instalado y corriendo  MySQL en `localhost:3306` con la BD `banking_db`. 
Si quieres regenerar el esquema, ejecuta `BaseDatos.sql`.

Testing:
```bash
mvn test
```

### Frontend

```bash
cd banking-app
npm install
npm start
```

Abre http://localhost:4200.

Ejecutar tests Jest:
```bash
npm test
npm run test:coverage
```

## Postman v9.13.2

1. Abrir Postman v9.13.2.
2. Importar `postman_collection/Banking.postman_collection.json`.
3. Importar `postman/Banking.postman_environment.json` 
4. Ejecutar la coleccion completa con **Runner**. 
5. Cada request tiene tests automaticos.

## Casos de uso del enunciado

Los datos de ejemplo (Jose Lema, Marianela Montalvo, Juan Osorio) ya estan precargados via `BaseDatos.sql`. Puedes:

1. Crear cuentas adicionales (POST `/cuentas`).
2. Realizar movimientos (POST `/movimientos`):
   - Retiro 575 sobre 478758 -> saldo final 1425.
   - Deposito 600 sobre 225487 -> saldo final 700.
   - Deposito 150 sobre 495878 -> saldo final 150.
   - Retiro 540 sobre 496825 -> saldo final 0.
3. Generar el reporte de estado de cuenta de Marianela Montalvo entre dos fechas.

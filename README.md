## Codebase Architecture & Directory Structure

### Directory tree (up to 3 levels)

```
src/
  backend/
    models/
      Appointment.java
      CounterStaff.java
      Customer.java
      CustomerFeedback.java
      Feedback.java
      Manager.java
      Payment.java
      ServicePrice.java
      Technician.java
      User.java
      enums/
        AppointmentStatus.java
        PaymentMethod.java
        Role.java
        ServiceType.java
    repository/
      AppointmentRepository.java
      FeedbackRepository.java
      InterfaceRepo.java
      PaymentRepository.java
      ServicePriceRepository.java
      UserRepository.java
    service/
      AppoinmentService.java
      AuthService.java
      FeedbackService.java
      GenerateReportService.java
      PaymentService.java
      ServicePriceService.java
      UserService.java
      ServiceException.java
    util/
      IdGenerator.java
      SessionManager.java
  frontend/
    auth/
      LoginPage.java
    counterstaff/
      AppointmentManagementPage.java
      CustomerManagementPage.java
      PaymentPage.java
    customer/
      FeedbackPage.java
      ViewPaymentHistoryPage.java
      ViewServiceHistoryPage.java
    manager/
      AnalyzeReportPage.java
      StaffManagementPage.java
      SetPricePage.java
      ViewCommentPage.java
      ViewPaymentHistoryPage.java
    technician/
      UpdateAppointmentPage.java
      ViewCommentPage.java
    DashboardPage.java
    MainPage.java
    PersonalProfilePage.java
text/
  appointments.txt
  feedbacks.txt
  payments.txt
  service_prices.txt
  users.txt
```

### Responsibilities (directory / key files)

| Path | Responsibility |
|---|---|
| `src/backend/models` | Domain models (User, Technician, Customer, Appointment, Payment, Feedback, ServicePrice) used across services and UI. |
| `src/backend/repository` | File-backed persistence layer — read/parse and write model objects to `text/*.txt`. Key repos: `UserRepository`, `AppointmentRepository`, `FeedbackRepository`, `PaymentRepository`, `ServicePriceRepository`. |
| `src/backend/service` | Business logic, validation and orchestration. Examples: `UserService` (user lifecycle, reset password), `AuthService` (login/logout/session), `AppoinmentService` (appointment workflows), `FeedbackService` (create/query feedback), `PaymentService` (payments), `GenerateReportService` (manager reports). |
| `src/backend/util` | Utilities: `IdGenerator` (ID sequences), `SessionManager` (current user context). |
| `src/frontend` | Swing UI pages and wiring. `MainPage` constructs services and injects them into `DashboardPage`, which assembles role-based tabs (manager, counter-staff, technician, customer). |
| `src/frontend/manager/StaffManagementPage.java` | Staff CRUD UI, password-reset action (calls `UserService.resetSelectedUserPassword`). |
| `src/frontend/counterstaff/CustomerManagementPage.java` | Customer CRUD UI. |
| `src/frontend/technician/ViewCommentPage.java` | Technician-facing feedback viewer (uses `FeedbackService.getTechnicianFeedback`). |
| `text/*.txt` | Line-oriented, human-editable data store: `users.txt`, `appointments.txt`, `feedbacks.txt`, `payments.txt`, `service_prices.txt`. |

### Data Flow (brief)

UI (Swing pages) invoke service methods (e.g., `UserService`, `AppoinmentService`, `FeedbackService`, `PaymentService`) to perform actions. Services validate input and translate operations into repository calls; repository implementations serialize and deserialize the domain models to plain text files under `text/`. `SessionManager` provides the current user context to services for authorization-sensitive operations. Aggregations (manager reports) read from multiple repositories to compute summaries rendered in the manager UI. `ServiceException` is used to bubble validation and domain errors back to the UI, which displays user-facing dialogs.

---

If you'd like, I can refine this into a full `README.md` with setup/run instructions and a short sequence diagram for common flows (reset password, submit feedback).

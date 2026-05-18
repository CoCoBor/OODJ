# OODJ Reverse Engineering Report

## Scope
This document is purely meant to help us understand the current system, So that we can properly plan a fix.

## 1. Repository Inventory

### Tech Stack
- Language: Java
- UI toolkit: Swing and AWT
- Runtime: Java desktop application running on the local JVM
- Build system: None detected
- Package manager: None detected
- Persistence: File-based storage using plain text files under `text/`
- Infrastructure: None detected

### Repository Structure
- Root Java files: The application is a flat default-package Java project with all `.java` files in the root folder.
- [MainFrame.java](../MainFrame.java): Login screen and application entry point.
- [ManagerPage.java](../ManagerPage.java): Manager dashboard.
- [CounterStaffPage.java](../CounterStaffPage.java): Counter staff dashboard.
- [TechnicianPage.java](../TechnicianPage.java): Technician dashboard.
- [CustomerPage.java](../CustomerPage.java): Customer dashboard.
- [UserFileHandler.java](../UserFileHandler.java): Shared user, price, and report file operations.
- [CounterHandler.java](../CounterHandler.java): Counter staff file operations.
- [TechnicianHandler.java](../TechnicianHandler.java): Technician file operations.
- [CustomerHandler.java](../CustomerHandler.java): Customer file operations.
- [Appointment.java](../Appointment.java): Appointment data model.
- [User.java](../User.java): User data model.
- [DeleteUser.java](../DeleteUser.java): Shared delete helper.
- `Information/`: Human-authored documentation.
- `text/`: Persistent data files.
- `receipts/`: Generated receipt files.

### Entry Points
- Primary entry point: [MainFrame.main](../MainFrame.java#L146)
- Startup UI initialization: [MainFrame.initialize](../MainFrame.java#L37)

### Dependencies
- Internal dependencies only.
- External integrations: None found.
- Third-party services: None found.

### Configuration System
- Environment variables: None found.
- Config files: None found.
- Secrets handling: None found.
- Deployment config: None found.
- Runtime path dependency: The app expects relative paths such as `text/users_id.txt` and `receipts/`.

## 2. System Architecture

### High-Level Architecture
- Architecture style: Single-process monolithic desktop application.
- Frontend/backend relationship: Swing screens call static handler methods directly.
- Request flow: User action -> Swing listener -> handler method -> text file read/write -> UI refresh.
- Data flow: Flat files act as the application database.

### Component Breakdown

#### MainFrame
- Responsibility: Login and role routing.
- Important methods: `validateLogin`, `initialize`, `main`.
- Files used: `text/users_id.txt`.

#### ManagerPage
- Responsibility: Manager dashboard, user table, and access to pricing, reports, and feedback.
- Important methods: constructor, `loadUserData`, `refreshTable`, `showPage`.
- Dependencies: [UserFileHandler](../UserFileHandler.java), [AddUser](../AddUser.java), [UpdateUser](../UpdateUser.java), [DeleteUser](../DeleteUser.java), [Prices](../Prices.java), [ViewReport](../ViewReport.java), [ManagerFeedback](../ManagerFeedback.java).

#### CounterStaffPage
- Responsibility: Customer management, appointments, payment collection, and receipt generation.
- Important methods: constructor, `showCustomerPanel`, `generateRange`, `loadPaymentTable`.
- Dependencies: [CounterHandler](../CounterHandler.java), [CustomerAddUser](../CustomerAddUser.java), [CustomerUpdateUser](../CustomerUpdateUser.java), [DeleteUser](../DeleteUser.java).

#### TechnicianPage
- Responsibility: Technician profile, assigned appointments, completion, and feedback.
- Important methods: constructor, `showEditProfile`, `showMyAppointments`, `showAppointmentDetail`, `showMyFeedbacks`.
- Dependencies: [TechnicianHandler](../TechnicianHandler.java), [CounterHandler](../CounterHandler.java).

#### CustomerPage
- Responsibility: Customer profile, service history, payment history, technician feedback, and comment entry.
- Important methods: constructor, `showEditProfile`, `showServiceHistory`, `showPaymentHistory`, `showFeedbacks`, `showCommentDialog`.
- Dependencies: [CustomerHandler](../CustomerHandler.java), [TechnicianHandler](../TechnicianHandler.java), [CounterHandler](../CounterHandler.java).

#### UserFileHandler
- Responsibility: Shared user lookup, add, price storage, and report generation.
- Important methods: `getAllUsersObject`, `getStaffOnly`, `getCustomersOnly`, `addUser`, `findUser`, `setPrice`, `getPrice`, `generateReport`, `checkSameTp`, `checkSamePhone`, `checkPhoneNumber`.

#### CounterHandler
- Responsibility: Counter staff business logic and appointment/payment file operations.
- Important methods: `updateUser`, `updateCustomer`, `addVehicle`, `getCustomersOnly`, `getTechnicians`, `addAppointment`, `technicianAvailability`, `getAllAppointmentsForTable`, `updatePaymentStatus`, `markPaymentAsPaid`, `getCompletedUnpaidAppointments`, `getCompletedPaidAppointments`, `generateReceipt`, `readReceipt`, `getCustomerFeedback`.

#### TechnicianHandler
- Responsibility: Technician appointments, status changes, technician feedback, and customer comments.
- Important methods: `getAppointmentsForTechnician`, `updateAppointmentStatus`, `saveFeedback`, `getFeedbackForAppointment`, `getFeedbacksByTechnician`, `getAllFeedbacks`, `saveCustomerComment`.

#### CustomerHandler
- Responsibility: Customer appointment lookup and price lookup.
- Important methods: `getAppointmentsForCustomer`, `getPriceForService`.

### Application Lifecycle
- Startup sequence: `MainFrame.main` -> `MainFrame.initialize` -> login form display.
- Initialization process: Role selection and credential validation against `text/users_id.txt`.
- Service registration: None.
- Middleware/hooks: None.

### Authentication and Authorization
- Login flow: First name, TP number, password, and role are compared against `text/users_id.txt`.
- Session handling: In-memory `User` object passed to dashboards. No token/session framework.
- Roles: Manager, Counter Staff, Technician, Customer.
- Security middleware: None.

### Database Architecture
- ORM/query builder: None.
- Models/entities: [User](../User.java), [Appointment](../Appointment.java).
- Relationships: Appointments reference customer TP and technician TP; vehicle records map customer TP to plate/type.
- Migration system: None.
- Data access pattern: Read entire file, modify lines in memory, rewrite file.

### API Architecture
- API style: None. This is a GUI application, not a networked service.
- Request validation: Manual validation via helper methods and UI checks.
- Error handling: Exceptions are mostly caught and shown in dialogs or printed to console.

### Async/Background Processing
- None found.

## 3. Business Logic Mapping

### User Login
- Purpose: Authenticate a user and open the correct dashboard.
- Trigger: Login button in [MainFrame.java](../MainFrame.java).
- Flow: Validate credentials -> load `User` object -> open dashboard.
- Files involved: [MainFrame.java](../MainFrame.java), [UserFileHandler.java](../UserFileHandler.java), `text/users_id.txt`.
- Edge cases: Invalid credentials show an error label.

### Manager User CRUD
- Purpose: Create, update, and delete staff users.
- Trigger: Manager dashboard buttons and table row selection.
- Flow: Open add/update forms -> validate TP and phone -> write `text/users_id.txt`.
- Files involved: [ManagerPage.java](../ManagerPage.java), [AddUser.java](../AddUser.java), [UpdateUser.java](../UpdateUser.java), [DeleteUser.java](../DeleteUser.java), [UserFileHandler.java](../UserFileHandler.java).
- Edge cases: Last manager deletion is blocked.

### Counter Staff Customer CRUD
- Purpose: Manage customer profiles and their vehicle details.
- Trigger: Manage Customers in [CounterStaffPage.java](../CounterStaffPage.java).
- Flow: Add/update/delete customers -> update `text/users_id.txt` and `text/customer_vehicle.txt`.
- Files involved: [CounterStaffPage.java](../CounterStaffPage.java), [CustomerAddUser.java](../CustomerAddUser.java), [CustomerUpdateUser.java](../CustomerUpdateUser.java), [CounterHandler.java](../CounterHandler.java).
- Edge cases: Phone number must be 10 digits and TP/phone must be unique.

### Appointment Scheduling
- Purpose: Book service appointments for customers.
- Trigger: Create Appointment in [CounterStaffPage.java](../CounterStaffPage.java).
- Flow: Choose customer, service, date, time, technician -> check availability -> append appointment record.
- Files involved: [CounterStaffPage.java](../CounterStaffPage.java), [CounterHandler.java](../CounterHandler.java), `text/appointments.txt`, `text/customer_vehicle.txt`.
- Edge cases: Availability check uses hour-based overlap logic.

### Technician Completion and Feedback
- Purpose: Mark work complete and record technician feedback.
- Trigger: Double-click appointment in [TechnicianPage.java](../TechnicianPage.java).
- Flow: Load assigned appointments -> mark complete -> save feedback -> rewrite appointment or feedback files.
- Files involved: [TechnicianPage.java](../TechnicianPage.java), [TechnicianHandler.java](../TechnicianHandler.java), `text/appointments.txt`, `text/feedbacks.txt`.
- Edge cases: Feedback is keyed by customer TP, date, and time.

### Customer History and Comments
- Purpose: Let customers review past appointments and leave comments.
- Trigger: Service History and double-click comment dialog in [CustomerPage.java](../CustomerPage.java).
- Flow: Load customer appointments -> show history/payment/feedback -> save comment into appointment row.
- Files involved: [CustomerPage.java](../CustomerPage.java), [CustomerHandler.java](../CustomerHandler.java), [TechnicianHandler.java](../TechnicianHandler.java), `text/appointments.txt`.
- Edge cases: Comment text is sanitized to avoid comma/newline parsing issues.

### Receipt Generation
- Purpose: Produce a receipt after payment.
- Trigger: Paid appointment selected in [CounterStaffPage.java](../CounterStaffPage.java).
- Flow: Mark appointment as PAID -> generate receipt file -> show receipt content.
- Files involved: [CounterStaffPage.java](../CounterStaffPage.java), [CounterHandler.java](../CounterHandler.java), `receipts/`.
- Edge cases: Receipt files are named by TP, date, and time.

## 4. Execution Flow Tracing

### Login Flow
User -> [MainFrame.java](../MainFrame.java) -> [UserFileHandler.findUser](../UserFileHandler.java#L67) -> dashboard

### Appointment Creation Flow
User -> [CounterStaffPage.java](../CounterStaffPage.java) -> [CounterHandler.technicianAvailability](../CounterHandler.java#L189) -> [CounterHandler.addAppointment](../CounterHandler.java#L133) -> `text/appointments.txt`

### Technician Completion Flow
User -> [TechnicianPage.java](../TechnicianPage.java) -> [TechnicianHandler.updateAppointmentStatus](../TechnicianHandler.java#L47) -> `text/appointments.txt`

### Technician Feedback Flow
User -> [TechnicianPage.java](../TechnicianPage.java) -> [TechnicianHandler.saveFeedback](../TechnicianHandler.java#L78) -> `text/feedbacks.txt`

### Customer Comment Flow
User -> [CustomerPage.java](../CustomerPage.java) -> [TechnicianHandler.saveCustomerComment](../TechnicianHandler.java#L192) -> `text/appointments.txt`

### Payment and Receipt Flow
User -> [CounterStaffPage.java](../CounterStaffPage.java) -> [CounterHandler.markPaymentAsPaid](../CounterHandler.java#L340) / [CounterHandler.generateReceipt](../CounterHandler.java#L452) -> `text/appointments.txt` / `receipts/`

## 5. Technical Debt and Risks

- Plaintext passwords are stored in [text/users_id.txt](../text/users_id.txt).
- Multiple classes mix UI and persistence responsibilities.
- File rewrites are non-transactional and unsafe for concurrency.
- Appointment parsing depends on fixed column positions.
- There is documentation drift between the README/docs and the actual source names.
- There is no build system, test suite, or deployment configuration in the repo.

## 6. Notes on Verification
- `Feedback.java` appears to be a duplicate feedback viewer. Its active status is Needs verification because no current call site was found in the workspace search.
- The docs mention `MainPage.java` and `Appointments.java`, but the codebase uses [MainFrame.java](../MainFrame.java) and [Appointment.java](../Appointment.java).
- If you refactor this project, the safest first targets are [UserFileHandler.java](../UserFileHandler.java), [CounterHandler.java](../CounterHandler.java), and [CounterStaffPage.java](../CounterStaffPage.java).
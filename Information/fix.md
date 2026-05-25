# Refactoring Plan 

This document is purely meant for planning purposes:
- ../MainFrame.java
- ../ManagerPage.java
- ../CounterStaffPage.java
- ../CustomerPage.java
- ../TechnicianPage.java
- ../UserFileHandler.java
- ../CounterHandler.java
- ../CustomerHandler.java
- ../TechnicianHandler.java
- ../AddUser.java
- ../UpdateUser.java
- ../DeleteUser.java
- ../text/users_id.txt
- ../text/appointments.txt
- ../text/customer_vehicle.txt
- ../text/price.txt
- ../text/feedbacks.txt

## Phase 1 - Current Architecture Analysis

### 1. Current architecture style

Current style is a monolithic Swing desktop system with procedural service logic.

- Monolithic Swing app:
  - Entry at ../MainFrame.java line 146.
  - Role dashboards are separate JFrame classes, but all run in one process.
- Procedural handler style:
  - Handler classes are static-method utilities, not object-oriented services with explicit dependencies.
  - Examples: ../UserFileHandler.java, ../CounterHandler.java, ../TechnicianHandler.java, ../CustomerHandler.java.
- Tight coupling:
  - UI classes call file handlers directly.
  - UI classes also perform business decisions, parsing, and orchestration.
- Weak layering:
  - There is no clear domain service layer, repository layer, or validation layer.

### 2. Current project structure

#### UI classes
- Login/startup: ../MainFrame.java
- Manager dashboard: ../ManagerPage.java
- Counter staff dashboard: ../CounterStaffPage.java
- Technician dashboard: ../TechnicianPage.java
- Customer dashboard: ../CustomerPage.java
- Manager utility UIs: ../AddUser.java, ../UpdateUser.java, ../Prices.java, ../ViewReport.java, ../ManagerFeedback.java
- Counter customer utility UIs: ../CustomerAddUser.java, ../CustomerUpdateUser.java

#### Handler classes
- ../UserFileHandler.java
  - User list/load/add/find
  - Price load/update
  - Report generation
  - Basic phone and uniqueness checks
- ../CounterHandler.java
  - User updates, customer updates, vehicle writes
  - Appointment create and availability checks
  - Payment status updates and receipt generation
  - Customer feedback extraction from appointments
- ../TechnicianHandler.java
  - Technician appointment reads and completion updates
  - Technician feedback persistence
  - Customer comment write-back to appointment rows
- ../CustomerHandler.java
  - Customer appointment read operations
  - Price lookups

#### Models/entities
- ../User.java: mutable user model.
- ../Appointment.java: read-only appointment projection used by tables/payment/receipt.

#### Utility/shared classes
- ../DeleteUser.java: shared delete implementation with "last manager" protection.

#### File persistence structure
- ../text/users_id.txt
- ../text/appointments.txt
- ../text/customer_vehicle.txt
- ../text/price.txt
- ../text/feedbacks.txt
- ../receipts/*.txt

### 3. Dependency relationships and coupling issues

#### UI depends directly on handlers
- ../MainFrame.java calls login validation in itself and user loading in ../UserFileHandler.java.
- ../CounterStaffPage.java directly calls many methods in ../CounterHandler.java and ../UserFileHandler.java.
- ../TechnicianPage.java directly calls ../TechnicianHandler.java and ../CounterHandler.java.
- ../CustomerPage.java directly calls ../CustomerHandler.java and ../TechnicianHandler.java.

#### God classes
- ../CounterHandler.java is a god class.
  - It mixes identity/profile updates, vehicle persistence, scheduling, payment rules, receipt generation, and feedback extraction.
- ../CounterStaffPage.java is a god UI class.
  - It contains profile editing, customer CRUD orchestration, appointment workflow, payment workflow, and receipt interaction.
- ../UserFileHandler.java is also overloaded.
  - It combines user persistence, pricing persistence, report aggregation, and validation helpers.

#### UI and business logic mixing
- Customer and technician IDs are parsed from display strings in ../CounterStaffPage.java.
- Payment state transitions are triggered from UI event listeners directly.
- Appointment and feedback business rules are spread across UI and handlers.

### 4. Data storage structure and risks

#### users_id.txt
- Current format: name,tp,phone,password,role
- Example evidence: ../text/users_id.txt
- Risks:
  - Plaintext password storage.
  - Invalid rows already exist, for example empty name and TP.
  - Duplicate phone and weak data integrity.

#### appointments.txt
- Current practical format: customerDisplayName,customerTP,plate,service,technicianDisplayName,techTP,date,time,status,customerComment,paymentStatus
- Example evidence: ../text/appointments.txt
- Parsing strategy:
  - Most reads use split by comma and fixed indices.
  - Some methods force array length via Arrays.copyOf.
- Risks:
  - Index-based parsing is fragile.
  - Any malformed line can break semantics.
  - Display-formatted fields are persisted, then reparsed.

#### customer_vehicle.txt
- Current format: customerTP,plate,type
- Example evidence: ../text/customer_vehicle.txt
- Risks:
  - Invalid empty line exists.
  - No uniqueness constraints on plate.

#### price.txt
- Current format: service|price
- Example evidence: ../text/price.txt
- Risks:
  - Price values can be invalid strings and negative-like tokens.
  - No strict numeric validation before persistence.

#### feedbacks.txt
- Current format: customerTP,date,time,techTP,techName,feedbackText
- Example evidence: ../text/feedbacks.txt
- Risks:
  - No robust escaping strategy for commas except split limit usage.
  - Referential link relies on customerTP/date/time matching appointment rows.

#### receipt files
- Current format: human-readable text report generated after payment.
- Location: ../receipts/receipt_{tp}_{date}_{time}.txt
- Risks:
  - No uniqueness conflict handling beyond filename pattern.
  - No integrity relationship check when reading generated receipt.

## Phase 2 - Complete Refactoring Roadmap

Refactoring order is designed for minimal breakage while preserving txt compatibility.

### Phase R1 - Stabilize persistence contracts first
- Goal:
  - Freeze explicit file schemas and define parser/writer contracts.
- Affected classes:
  - ../UserFileHandler.java, ../CounterHandler.java, ../TechnicianHandler.java, ../CustomerHandler.java
- Architectural reason:
  - Every workflow relies on fragile ad-hoc parsing.
- Expected improvement:
  - Deterministic read/write behavior and controlled handling of malformed rows.
- Risks:
  - Existing malformed rows may fail strict parsing.
- Migration strategy:
  - Introduce tolerant parser mode first, log invalid rows, then progressively tighten validation.

### Phase R2 - Introduce repository layer per file aggregate
- Goal:
  - Move raw file IO out of business logic into repositories.
- Affected classes:
  - Replace direct file code in handlers.
- Architectural reason:
  - Current handlers mix business rules and persistence concerns.
- Expected improvement:
  - Testable business logic and reusable data access methods.
- Risks:
  - Behavior drift in edge cases if repository methods change matching criteria.
- Migration strategy:
  - Wrap old static methods behind repository adapters first; switch callers incrementally.

### Phase R3 - Create explicit validation layer
- Goal:
  - Centralize all input and state validations.
- Affected classes:
  - ../AddUser.java, ../UpdateUser.java, ../CustomerAddUser.java, ../CustomerUpdateUser.java, ../MainFrame.java, ../CounterStaffPage.java, ../TechnicianPage.java, ../CustomerPage.java
- Architectural reason:
  - Validation is inconsistent and scattered.
- Expected improvement:
  - Uniform error messages and fewer invalid records written.
- Risks:
  - Users may see stricter validation than before.
- Migration strategy:
  - Run new validators in warn mode for one pass; then enforce hard failures.

### Phase R4 - Split god handlers into domain services
- Goal:
  - Decompose by business capability.
- Affected classes:
  - ../CounterHandler.java and ../UserFileHandler.java first.
- Architectural reason:
  - High coupling and low cohesion are blocking maintainability.
- Expected improvement:
  - Clear class boundaries and easier grading on OOP quality.
- Risks:
  - UI call sites are many and tightly coupled.
- Migration strategy:
  - Introduce facade service maintaining existing signatures, then route internally to new focused services.

### Phase R5 - Authentication and session redesign
- Goal:
  - Single authentication flow with secure credential handling and role authorization decisions.
- Affected classes:
  - ../MainFrame.java, ../UserFileHandler.java
- Architectural reason:
  - Current login duplicates verification and uses plaintext.
- Expected improvement:
  - Cleaner responsibilities and reduced auth defects.
- Risks:
  - Existing passwords may need migration to hashed form.
- Migration strategy:
  - Dual-mode verifier: support legacy plaintext during migration, rewrite to hashed on successful login/reset.

### Phase R6 - Appointment and scheduling redesign
- Goal:
  - Correct time overlap logic and structured assignment flow.
- Affected classes:
  - ../CounterStaffPage.java, ../CounterHandler.java, ../TechnicianHandler.java, ../CustomerHandler.java
- Architectural reason:
  - Scheduling logic currently depends on restricted hour options and string parsing.
- Expected improvement:
  - Correct conflict detection and better state consistency.
- Risks:
  - Existing appointment rows may use inconsistent date/time formats.
- Migration strategy:
  - Normalize date/time parser with fallback patterns and canonical writing format.

### Phase R7 - Payment domain correction
- Goal:
  - Make payment status authoritative and consistent across customer, counter, and reports.
- Affected classes:
  - ../CustomerPage.java, ../CounterStaffPage.java, ../CounterHandler.java, ../UserFileHandler.java
- Architectural reason:
  - Current customer payment view can display incorrect paid status.
- Expected improvement:
  - Consistent financial state and report correctness.
- Risks:
  - Legacy rows with missing payment status.
- Migration strategy:
  - Default missing values to UNPAID and run reconciliation checks on startup.

### Phase R8 - UI cleanup and orchestration simplification
- Goal:
  - Reduce dashboard complexity and centralize navigation/use-case orchestration.
- Affected classes:
  - ../ManagerPage.java, ../CounterStaffPage.java, ../TechnicianPage.java, ../CustomerPage.java
- Architectural reason:
  - UI classes currently contain too much business behavior.
- Expected improvement:
  - Better separation and easier future extension.
- Risks:
  - Temporary UI regressions during extraction.
- Migration strategy:
  - Extract feature panels/controllers one workflow at a time.

### Phase R9 - Data integrity and recovery controls
- Goal:
  - Prevent corruption from partial writes and malformed data.
- Affected classes:
  - All file-writing paths.
- Architectural reason:
  - File writes are non-transactional and overwrite entire files.
- Expected improvement:
  - Safer persistence under runtime failures.
- Risks:
  - Slightly more IO overhead.
- Migration strategy:
  - Write-to-temp then atomic replace; create backup snapshots for critical files.

## Phase 3 - Target Architecture Design

### Layer 1: UI Layer
- Responsibilities:
  - Render Swing components, collect user input, display results/errors.
  - Invoke application services with request objects.
- Allowed dependencies:
  - Service layer only.
- Forbidden responsibilities:
  - File parsing, direct file IO, business rule decisions, identity verification logic.

### Layer 2: Service Layer
- Responsibilities:
  - Execute use-cases: authentication, user management, scheduling, payments, feedback, reporting.
  - Enforce business invariants and workflow sequencing.
- Allowed dependencies:
  - Validation layer and repository layer.
- Forbidden responsibilities:
  - Swing component manipulation, raw line-based file handling.

### Layer 3: Repository/File Layer
- Responsibilities:
  - Read/write domain records from txt files via parser/serializer contracts.
  - Guarantee schema handling, defaulting, and safe writes.
- Allowed dependencies:
  - Domain models and parser utilities.
- Forbidden responsibilities:
  - Domain policy decisions, role authorization logic, UI logic.

### Layer 4: Domain Models
- Responsibilities:
  - Carry data and domain invariants.
  - Represent business state transitions through explicit methods/state enums.
- Allowed dependencies:
  - None outside foundational value types.
- Forbidden responsibilities:
  - File IO and Swing concerns.

### Layer 5: Validation Layer
- Responsibilities:
  - Input validation (format, required fields, ranges).
  - Business precondition validation (appointment can be paid only when completed, etc.).
- Allowed dependencies:
  - Domain values only.
- Forbidden responsibilities:
  - Persisting data or directly mutating UI.

### Target request/data interaction flow
1. UI captures input and creates request data.
2. Validation layer checks syntactic and semantic validity.
3. Service layer runs business rules and decides state transition.
4. Repository layer loads and persists domain records safely.
5. Service returns result object.
6. UI renders success/failure and refreshed views.

### Where each concern belongs
- Validation belongs in validation classes and service precondition checks.
- Parsing belongs only in repository/parser layer.
- Business rules belong in services and domain model methods.
- UI should only orchestrate user interaction and presentation.

## Phase 4 - Authentication Flow A to Z

### Current implementation weaknesses
- Dual verification path:
  - ../MainFrame.java validates credentials directly, then loads user separately through ../UserFileHandler.java.
- Plaintext credential model:
  - Password is compared as plain string from ../text/users_id.txt.
- UI handling issue:
  - Login password input uses JTextField, not password masking.
- Authorization coupling:
  - Role decision is mostly UI-side conditional routing.

### Ideal authentication flow

1. User enters TP, password, and role in login form.
2. UI validates basic field presence only.
3. UI calls AuthService.authenticate(request).
4. AuthService invokes AuthValidator for format and role checks.
5. AuthService loads user identity from UserRepository by TP.
6. PasswordVerifier checks supplied password against stored hash.
7. RoleAuthorizer verifies selected role matches stored role permissions.
8. On success, AuthService returns AuthResult with authenticated UserSession.
9. SessionContext stores current user identity and role for runtime usage.
10. UI router opens correct dashboard based on AuthResult.
11. On failure, AuthService returns typed error codes; UI displays safe messages.

### Responsibility mapping
- UI Login Form:
  - Only collect credentials and display messages.
- AuthService:
  - Main orchestration and decision point.
- UserRepository:
  - Load persisted user records.
- PasswordVerifier:
  - Credential comparison and migration strategy.
- SessionContext:
  - Hold authenticated runtime user state.

### What must never happen in UI layer
- Never parse users_id lines.
- Never compare passwords.
- Never decide role security policy.
- Never persist credentials.

### Security improvements for this project
- Replace plaintext with hashed password storage.
- Use JPasswordField for login.
- Add failed-attempt throttling in AuthService.
- Normalize and trim inputs before verification.
- Return generic auth error to avoid account enumeration.

## Phase 5 - Payment Flow A to Z

### Current flow and flaws

Current flow:
- Technician marks appointment completed in ../TechnicianPage.java via ../TechnicianHandler.updateAppointmentStatus.
- Counter loads completed/unpaid and completed/paid via ../CounterHandler.java.
- Counter collects payment by writing PAID to appointment row.
- Receipt is generated from appointment projection and current service price.
- Customer payment history currently infers Paid from completion in ../CustomerPage.java.

Current flaws:
- Payment display inconsistency:
  - ../CustomerPage.java can show Paid incorrectly for completed UNPAID rows.
- Payment eligibility rules are implicit and scattered.
- Price at payment-time is not snapshot-persisted, only looked up dynamically.
- File rewrite operations are non-transactional.

### Improved payment architecture

#### Core rules
- Appointment is payable only when status is Completed and paymentStatus is UNPAID.
- Payment operation must be idempotent.
- Payment history must read actual paymentStatus, not inferred status.
- Receipt should store paid amount snapshot captured at payment time.

#### Proposed end-to-end flow
1. Counter opens unpaid eligible appointments from PaymentService.listPendingPayments.
2. PaymentService validates appointment eligibility.
3. PaymentService resolves amount using PricingService and records paidAmount snapshot.
4. PaymentService updates paymentStatus to PAID and stores paidAt timestamp.
5. PaymentService requests ReceiptService to create receipt document.
6. Receipt metadata is linked back to appointment payment record.
7. Customer payment history reads paymentStatus and paidAmount directly from repository data.
8. ReportService aggregates revenue from paid records only.

#### Safe txt update strategy
- Read current file.
- Locate exact target record by strong key (customerTP + technicianTP + date + time + service).
- Apply mutation in memory.
- Write to temp file.
- Validate row count and target record mutation.
- Replace original atomically.

#### Corruption and consistency controls
- Add operation log entry for payment mutation attempts.
- If receipt generation fails after payment update, mark payment as PAID but receiptPending and allow retry.
- Never recompute paid amount from current price after payment is finalized.

## Phase 6 - Appointment Flow A to Z

### Current weaknesses
- Technician assignment and customer selection rely on parsing display strings in ../CounterStaffPage.java.
- Overlap logic in ../CounterHandler.java is hour-based and limited.
- Appointment identity keys are weak and distributed.
- Completion, feedback, and comments are managed across separate files with soft linkage.

### Improved appointment architecture

#### Proper scheduling logic
- Define canonical time model using LocalDate and LocalTime semantics.
- Convert service type to explicit duration.
- Overlap formula per technician per day:
  - overlap exists when newStart < existingEnd and existingStart < newEnd.
- Check only active appointment states for conflicts.

#### Appointment creation flow
1. Counter UI submits customerTP, technicianTP, serviceType, date, startTime.
2. AppointmentValidator verifies fields and entity existence.
3. SchedulingService checks technician availability with canonical datetime values.
4. On pass, AppointmentService creates appointment with status Pending and paymentStatus UNPAID.
5. AppointmentRepository persists record.

#### Technician completion flow
1. Technician loads assigned pending appointments.
2. Technician marks completion through AppointmentService.completeAppointment.
3. Service validates ownership and current status.
4. Status transitions Pending -> Completed.
5. Payment eligibility becomes true automatically.

#### Feedback lifecycle
1. Technician submits technical feedback tied to appointmentId composite key.
2. Customer submits comment tied to same key.
3. FeedbackService composes unified view for manager by join on key.

#### Customer history flow
- CustomerHistoryService reads appointments by customerTP and returns service timeline with both completion and payment states.

## Phase 7 - Important System Flows (End-to-End)

Each flow is specified with trigger, UI, service, repository, validation, failure handling, and state update.

### A. Application startup and dashboard loading
1. Trigger: Application launch.
2. UI interaction: login frame opens.
3. Service interaction: AuthService invoked after submit.
4. Repository interaction: UserRepository read.
5. Validation: required fields and role selection.
6. Failure handling: auth error message, remain on login.
7. State updates: SessionContext set and role dashboard opened.

### B. User registration (manager adds staff)
1. Trigger: manager clicks Add User.
2. UI interaction: form submit.
3. Service interaction: UserManagementService.registerStaff.
4. Repository interaction: UsersRepository uniqueness check and append.
5. Validation: required fields, TP uniqueness, phone format, role allowed.
6. Failure handling: duplicate/invalid errors surfaced in form.
7. State updates: user record added and table refreshed.

### C. Customer CRUD by counter staff
1. Trigger: manage customer panel actions.
2. UI interaction: add/update/delete dialogs.
3. Service interaction: CustomerService methods.
4. Repository interaction: users and customer_vehicle repositories.
5. Validation: TP/phone/plate completeness and format.
6. Failure handling: transactional rollback on partial failure.
7. State updates: customer and vehicle records synchronized.

### D. Appointment creation and technician assignment
1. Trigger: create appointment submit.
2. UI interaction: select customer/service/date/time/technician.
3. Service interaction: SchedulingService and AppointmentService.
4. Repository interaction: appointments read/write, users/vehicle reads.
5. Validation: entity existence and time overlap checks.
6. Failure handling: conflict messages, no persistence.
7. State updates: new pending appointment added.

### E. Technician workflow
1. Trigger: technician opens My Appointments and updates one.
2. UI interaction: detail dialog, complete or save feedback.
3. Service interaction: AppointmentService.complete and FeedbackService.saveTechFeedback.
4. Repository interaction: appointments and feedback repositories.
5. Validation: appointment ownership, status transition validity, non-empty feedback.
6. Failure handling: status conflict or invalid feedback errors.
7. State updates: appointment completed and feedback saved.

### F. Payment and receipt workflow
1. Trigger: counter collects payment from unpaid table.
2. UI interaction: payment confirmation and receipt generation request.
3. Service interaction: PaymentService.collectPayment and ReceiptService.generate.
4. Repository interaction: appointments write and receipts write.
5. Validation: appointment must be completed and unpaid.
6. Failure handling: retry-safe payment update and receipt regeneration path.
7. State updates: payment status set to paid and receipt record created.

### G. Manager reporting workflow
1. Trigger: manager opens report page.
2. UI interaction: report view rendering.
3. Service interaction: ReportService.aggregateRevenueAndVolume.
4. Repository interaction: appointments and price/payment snapshot reads.
5. Validation: malformed rows excluded with warning counters.
6. Failure handling: show partial report with data-quality warning.
7. State updates: none (read-only flow).

### H. Feedback/comment workflow
1. Trigger: technician/customer submit feedback/comment.
2. UI interaction: feedback/comment forms.
3. Service interaction: FeedbackService.saveTechFeedback and saveCustomerComment.
4. Repository interaction: feedback and appointment records.
5. Validation: role, non-empty text, target appointment exists.
6. Failure handling: save failure returns explicit error, no silent drop.
7. State updates: feedback linked to appointment identity key.

### I. txt synchronization and recovery workflow
1. Trigger: any write operation.
2. UI interaction: none.
3. Service interaction: service delegates write to repository transaction wrapper.
4. Repository interaction: temp write, integrity check, replace.
5. Validation: schema and target record existence.
6. Failure handling: rollback to backup and report incident.
7. State updates: operation log written.

## Phase 8 - Class Responsibility Redesign

### CounterHandler redesign

#### Current responsibilities
- User profile updates
- Customer updates
- Vehicle add
- Customer/technician lookup
- Appointment create/list/schedule check
- Payment status updates
- Receipt generation/read
- Customer feedback extraction

#### Why problematic
- Violates single responsibility heavily.
- Encourages direct UI coupling.
- Hard to test and reason about state transitions.

#### Proposed decomposition
- ProfileService
- CustomerService
- SchedulingService
- AppointmentService
- PaymentService
- ReceiptService
- FeedbackQueryService
- AppointmentRepository, UserRepository, VehicleRepository

#### New boundaries
- Counter UI may call only service facade methods.
- No direct file mutation in UI.

### UserFileHandler redesign

#### Current responsibilities
- User repository behavior
- Price repository behavior
- Report aggregation
- Validation checks

#### Why problematic
- Data access and business/report logic are mixed.
- Validation utility methods are globally coupled.

#### Proposed decomposition
- UsersRepository
- PricingRepository
- ReportService
- UserValidationService
- AuthRepository adapter

### CustomerHandler redesign

#### Current responsibilities
- Appointment reads
- Price lookup

#### Why problematic
- Returns raw String arrays, forcing index-based logic upstream.

#### Proposed decomposition
- CustomerHistoryService
- AppointmentRepository
- PricingService
- Domain DTO mappers for customer views

### TechnicianHandler redesign

#### Current responsibilities
- Technician appointment reads
- Completion updates
- Feedback CRUD
- Customer comment updates in appointments file

#### Why problematic
- Two storage concerns mixed (appointment + feedback).
- Status transitions and text persistence mixed in one class.

#### Proposed decomposition
- TechnicianWorkService
- FeedbackService
- AppointmentRepository
- FeedbackRepository

### Large UI class redesign

#### Current classes
- ../CounterStaffPage.java
- ../CustomerPage.java
- ../TechnicianPage.java
- ../ManagerPage.java

#### Current problem
- Each class is both view and use-case controller.

#### Proposed redesign
- Keep JFrame classes as shell/navigation only.
- Extract feature panels/controllers:
  - CounterProfilePanelController
  - CustomerCrudPanelController
  - AppointmentPanelController
  - PaymentPanelController
  - TechnicianAppointmentsPanelController
  - CustomerHistoryPanelController
- Use service interfaces injected into controllers.

## Phase 9 - Implementation Strategy Before Coding

### Safe implementation order
1. Define file schema contracts and parser/serializer tests.
2. Build repositories that preserve exact current txt compatibility.
3. Introduce validation layer and integrate without changing UI flow yet.
4. Create service layer wrappers around old handlers.
5. Migrate authentication flow to AuthService and SessionContext.
6. Migrate appointment/scheduling logic to AppointmentService and SchedulingService.
7. Migrate payment and receipt logic to PaymentService and ReceiptService.
8. Correct customer payment history to use real payment status.
9. Refactor UI classes feature-by-feature to call services only.
10. Remove deprecated handler paths and duplicate screens.

### Dependency plan
- Repositories first, services second, UI migration last.
- Keep old method signatures temporarily via adapter facade to avoid breakage.

### Migration risks
- Existing malformed txt records can fail strict parsing.
- Existing UI code may rely on display-string parsing semantics.
- Payment and report calculations may shift if corrected to true state logic.

### Testing strategy
- Parser compatibility tests using current real files.
- Service-level tests for auth, scheduling overlap, payment eligibility, report totals.
- Scenario tests for role workflows:
  - Manager CRUD
  - Counter create appointment and collect payment
  - Technician complete and feedback
  - Customer history and feedback visibility
- Data integrity tests for atomic write and rollback behavior.

### Non-negotiable compatibility rule
- Preserve readable compatibility of existing txt files during migration.
- If schema enhancement is necessary, support backward read mode and controlled upgrade write mode.

## Final planning outcome

The current system is functionally broad but architecturally fragile. The refactor should not start with UI polishing or random class splitting. The first step must be persistence contracts plus service boundaries, then flow-by-flow migration with strict regression checks. That strategy maximizes grading improvements for architecture quality, OOP design, and operational correctness while minimizing risk of breaking current features.
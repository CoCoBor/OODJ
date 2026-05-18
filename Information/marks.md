# FINAL GRADE SUMMARY

This File was created, written fully by Co-Pilot AI, it serves as a guide on what is wrong with the current system and what could be improved.

| Component | Max Marks | Awarded | Comments |
|---|---:|---:|---|
| Requirement implementation | 16 | 10 | Core workflows exist, but validation is weak, authentication is insecure, and at least one user-facing flow misreports status. |
| OOP implementation | 16 | 5 | Only shallow encapsulation is present. Inheritance is trivial, polymorphism is absent, and the handler classes are procedural utilities rather than proper OOP design. |
| Presentation / system quality | 8 | 4 | The program is usable, but the UI is basic, inconsistent, and technically fragile. It is demoable, not polished. |

**Total implementation marks:** 19 / 40

**Percentage:** 47.5%

**Estimated letter grade:** F / Fail under a strict threshold

## DETAILED STRICT ANALYSIS

### 1) Performance Result Of Operations And Requirement Fulfillment

This submission implements the required feature set only at a functional surface level. That is not enough for strong credit.

#### Deduction 1: Authentication is insecure and duplicated
- Evidence: [MainFrame.validateLogin](../MainFrame.java#L14) reads plaintext credentials from `text/users_id.txt`, while [UserFileHandler.findUser](../UserFileHandler.java#L67) repeats a second credential lookup.
- Why this loses marks: login should be centralised, validated once, and not rely on plaintext passwords. Using a visible `JTextField` for password entry in [MainFrame.java](../MainFrame.java#L37) is also poor practice.
- Requirement impact: The login feature exists, but the implementation is weak and insecure.
- Proper implementation: Use one authentication service, hash passwords, mask password entry, and separate credential verification from user object loading.

#### Deduction 2: Input validation is inconsistent and incomplete
- Evidence: [AddUser](../AddUser.java#L10), [UpdateUser](../UpdateUser.java#L11), [CustomerAddUser](../CustomerAddUser.java#L9), and [CustomerUpdateUser](../CustomerUpdateUser.java#L9) do not check for empty name, TP, or password fields before writing data.
- Why this loses marks: the coursework explicitly requires validation to avoid logical errors. Empty strings are accepted and stored.
- Requirement impact: User registration and profile editing are only partially validated.
- Proper implementation: reject empty fields, normalise whitespace, validate field formats, and stop writing invalid records.

#### Deduction 3: Customer payment history is logically wrong
- Evidence: [CustomerPage.showPaymentHistory](../CustomerPage.java#L149) marks every completed appointment as `Paid` and ignores the actual payment status stored in `appointments.txt`.
- Why this loses marks: the UI misrepresents business state. A completed but unpaid appointment will still be displayed as paid.
- Requirement impact: Payment history is not trustworthy.
- Proper implementation: read the payment status field directly from the appointment record and display `Unpaid` where appropriate.

#### Deduction 4: Appointment scheduling is fragile
- Evidence: [CounterHandler.technicianAvailability](../CounterHandler.java#L189) uses hour-only arithmetic and hard-coded service durations.
- Why this loses marks: the booking logic does not model time properly. It works only because the UI restricts time choices to whole hours.
- Requirement impact: The appointment feature is present, but the scheduling logic is brittle and simplistic.
- Proper implementation: use a real date-time model, compare exact start/end times, and validate all time boundaries.

#### Deduction 5: File handling is highly coupled to fragile text layouts
- Evidence: [CounterHandler.addAppointment](../CounterHandler.java#L133), [TechnicianHandler.saveCustomerComment](../TechnicianHandler.java#L192), and [CounterHandler.getAllAppointmentsForTable](../CounterHandler.java#L223) all depend on fixed column positions in `appointments.txt`.
- Why this loses marks: one malformed row can corrupt downstream logic. The code is not robust to file edits or partial data damage.
- Requirement impact: Text-file persistence is used, but handled poorly.
- Proper implementation: introduce a dedicated parser/serializer or at least a consistent record abstraction.

#### Deduction 6: Several required flows are implemented in a shallow, display-driven way
- Evidence: [CounterHandler.getCustomersOnly](../CounterHandler.java#L91) and [getTechnicians](../CounterHandler.java#L112) return strings formatted as `Name (TP)`, which are later parsed by substring operations in [CounterStaffPage](../CounterStaffPage.java#L118).
- Why this loses marks: the business logic is depending on presentation strings. That is a poor design shortcut.
- Requirement impact: Customer/technician assignment works only as long as the display format never changes.
- Proper implementation: pass structured objects, not formatted strings.

#### Deduction 7: Report generation is too narrow and too dependent on current file state
- Evidence: [UserFileHandler.generateReport](../UserFileHandler.java#L163) only counts appointments with `Completed` and `PAID` status.
- Why this loses marks: the report is limited and fragile. It is not a general analytical report engine; it is a narrow file scan.
- Requirement impact: The report feature exists, but it is basic and lacks depth.
- Proper implementation: separate aggregation logic from file parsing and define the report rules more clearly.

#### Deduction 8: Duplicate and dead code indicate incomplete engineering discipline
- Evidence: [ManagerFeedback](../ManagerFeedback.java#L11) and [Feedback](../Feedback.java#L9) both implement feedback viewing, but only [ManagerFeedback](../ManagerFeedback.java#L11) is actually referenced in the current call chain.
- Why this loses marks: duplicate screens mean the codebase is not cleanly maintained.
- Requirement impact: Not a functional failure by itself, but it weakens the submission quality.
- Proper implementation: delete unused screens or consolidate them into one component.

### 2) Appropriate Design And Implementation Of OOP Concepts

This project uses OOP vocabulary, but mostly in a superficial way. The design is procedural code wrapped inside classes.

#### Deduction 1: Encapsulation is minimal and mostly limited to data holders
- Evidence: [User](../User.java#L1) and [Appointment](../Appointment.java#L1) are simple property containers with getters and setters.
- Why this loses marks: data classes alone do not demonstrate strong OOP design. They are necessary, but they are not sufficient.
- Proper implementation: encapsulate behaviour with the data and enforce invariants inside the model objects.

#### Deduction 2: Inheritance is trivial and not architecturally meaningful
- Evidence: the main screens such as [ManagerPage](../ManagerPage.java#L46), [CounterStaffPage](../CounterStaffPage.java#L11), [TechnicianPage](../TechnicianPage.java#L11), and [CustomerPage](../CustomerPage.java#L12) extend `JFrame` only to obtain window behaviour.
- Why this loses marks: inheriting a GUI container is framework usage, not meaningful domain inheritance. It does not demonstrate object hierarchy design.
- Proper implementation: if inheritance is used, it should model shared application behaviour, not just reuse Swing window plumbing.

#### Deduction 3: Polymorphism is effectively absent
- Evidence: there are no domain interfaces, no overridden business methods, and no polymorphic service hierarchy. The code relies on static method calls in [UserFileHandler](../UserFileHandler.java#L9), [CounterHandler](../CounterHandler.java#L10), [TechnicianHandler](../TechnicianHandler.java#L23), and [CustomerHandler](../CustomerHandler.java#L20).
- Why this loses marks: the coursework expects OOP concepts, yet the implementation is largely procedural.
- Proper implementation: define role-based abstractions or service interfaces and rely on substitutable implementations.

#### Deduction 4: Abstraction is weak
- Evidence: handler classes expose raw file operations and UI classes directly orchestrate them.
- Why this loses marks: the UI layer knows too much about file formats, status strings, and parsing rules.
- Proper implementation: hide persistence behind domain services and keep the screens focused on presentation only.

#### Deduction 5: Coupling is excessive and cohesion is poor
- Evidence: [CounterHandler](../CounterHandler.java#L10) contains user updates, customer updates, vehicle writes, appointment logic, availability checking, payment logic, receipt generation, and customer-feedback extraction in one class.
- Why this loses marks: this is a textbook god-class smell. [CounterStaffPage](../CounterStaffPage.java#L11) is similarly overloaded.
- Proper implementation: split responsibilities into separate classes such as user management, appointment management, payment management, and receipt generation.

#### Deduction 6: Reusability is low
- Evidence: appointment rows are passed around as `String[]` in [TechnicianHandler](../TechnicianHandler.java#L23), [CustomerHandler](../CustomerHandler.java#L20), and [ManagerFeedback](../ManagerFeedback.java#L11).
- Why this loses marks: index-based arrays are not reusable domain objects. They are fragile and hard to maintain.
- Proper implementation: use proper records or dedicated domain objects for appointments and feedback entries.

#### Deduction 7: SOLID principles are not respected
- Evidence: [UserFileHandler](../UserFileHandler.java#L9) handles users, prices, and reports; [CounterHandler](../CounterHandler.java#L10) handles several unrelated responsibilities; UI classes also contain business logic.
- Why this loses marks: the design violates SRP and blurs the layer boundaries.
- Proper implementation: split the responsibilities and isolate business logic from screen code.

### 3) Presentation Readiness And System Quality

The submission is usable, but it is not polished and not professionally hardened.

#### Deduction 1: The UI is basic and inconsistent
- Evidence: the screens in [MainFrame.java](../MainFrame.java#L37), [ManagerPage.java](../ManagerPage.java#L46), [CounterStaffPage.java](../CounterStaffPage.java#L11), [TechnicianPage.java](../TechnicianPage.java#L11), and [CustomerPage.java](../CustomerPage.java#L12) use default Swing components and plain layouts.
- Why this loses marks: the coursework expects presentation quality. This is functional, but it looks like a classroom prototype.
- Proper implementation: use a consistent visual theme, spacing system, and layout strategy across all screens.

#### Deduction 2: Password handling damages presentation and basic security
- Evidence: [MainFrame.java](../MainFrame.java#L37) uses `JTextField` for the login password.
- Why this loses marks: the password is visible on screen. That is unacceptable even in a coursework demo.
- Proper implementation: always use `JPasswordField` for secret input.

#### Deduction 3: Several screens rely on repeated modal dialogs instead of structured workflows
- Evidence: [ManagerPage](../ManagerPage.java#L46), [CounterStaffPage](../CounterStaffPage.java#L11), and [TechnicianPage](../TechnicianPage.java#L11) repeatedly open dialogs and tables without a cohesive navigation model.
- Why this loses marks: the application works, but the user experience is clumsy and inconsistent.
- Proper implementation: structure the interface around a cleaner dashboard/navigation pattern.

#### Deduction 4: System quality is weak because there is no packaging, testing, or build discipline
- Evidence: the workspace contains no build script, no package structure, and no automated tests.
- Why this loses marks: the submission is difficult to maintain and risky to refactor.
- Proper implementation: use packages, add tests, and provide an actual build workflow.

## RUNTIME AND LOGIC CHECKS

- Syntax-level errors were not reported by the workspace error scan, so the project appears to compile in its current state.
- That does not mean the logic is correct. The strongest confirmed logic defect is [CustomerPage.showPaymentHistory](../CustomerPage.java#L149), which mislabels completed unpaid work as paid.
- The code is also vulnerable to bad data because it depends on flat-file row positions throughout [CounterHandler](../CounterHandler.java#L10) and [TechnicianHandler](../TechnicianHandler.java#L23).

## FINAL VERDICT

1. **Would this realistically pass under a strict lecturer?** No. It would be treated as a borderline fail or a resubmission-quality submission because the OOP design is superficial and key business flows are fragile.
2. **Biggest architectural weakness:** The application is a monolithic pile of Swing screens plus static file handlers, with no proper separation of presentation, business logic, and persistence.
3. **Biggest implementation weakness:** The system depends on plaintext flat-file records and frequently hard-codes field positions and status strings, which makes it brittle.
4. **Most impressive technical area:** The appointment lifecycle is the most coherent part of the submission: creation, technician completion, payment status, and receipt generation are all present and connected across multiple classes.
5. **Top 10 refactoring priorities:**
   1. Replace plaintext credential handling in [MainFrame](../MainFrame.java#L14) and [UserFileHandler](../UserFileHandler.java#L67).
   2. Split [CounterHandler](../CounterHandler.java#L10) into focused services.
   3. Replace `String[]` record passing with proper domain objects.
   4. Fix [CustomerPage.showPaymentHistory](../CustomerPage.java#L149) so it reads real payment status.
   5. Add input validation to [AddUser](../AddUser.java#L10), [UpdateUser](../UpdateUser.java#L11), [CustomerAddUser](../CustomerAddUser.java#L9), and [CustomerUpdateUser](../CustomerUpdateUser.java#L9).
   6. Replace fragile time overlap logic in [CounterHandler.technicianAvailability](../CounterHandler.java#L189).
   7. Remove duplicate or dead screens such as [Feedback](../Feedback.java#L9) if unused.
   8. Introduce packages and separate UI from persistence logic.
   9. Add validation for missing price values before receipt/report generation.
   10. Standardise UI presentation across all role dashboards.
6. **Maintainable long-term?** No. It may survive a demo, but it is not maintainable as-is.
7. **Did the student understand OOP or only use it superficially?** Superficially. The code uses classes, getters, setters, and `JFrame` inheritance, but the design does not demonstrate strong object-oriented structure.
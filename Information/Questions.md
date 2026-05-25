# Questions / Mismatches Found During Verification

While going through the documentation and comparing it with the actual implementation, I found a few things that do not fully match what is currently in the repository.

---

## 1. Login / Start Page Naming Mismatch

### What the documentation says
The documentation mentions `MainPage.java` as the main login/start page.

### What is actually in the repository
There is no `MainPage.java` file in the project.

The actual login implementation is inside:
- `MainFrame.java`

This class handles:
- login validation,
- role checking,
- and application startup/navigation.

### Evidence
`MainFrame.java` contains the login validation logic:

```java
private boolean validateLogin(String firstName, String tpNum, String password, String role) {
   try (BufferedReader br = new BufferedReader(new FileReader("text/users_id.txt"))) {
      String line;

      while ((line = br.readLine()) != null) {
         String[] parts = line.split(",");

         if (parts.length == 5) {
            String fileFirstName = parts[0].trim();
            String fileTpNum = parts[1].trim();
            String filePassword = parts[3].trim();
            String fileRole = parts[4].trim();

            if (fileFirstName.equals(firstName)
               && fileTpNum.equals(tpNum)
               && filePassword.equals(password)
               && fileRole.equals(role)) {
               return true;
            }
         }
      }
   } catch (IOException e) {
      e.printStackTrace();
   }

   return false;
}
```

### Notes
This is mostly a documentation inconsistency rather than a functional issue, but it can still create confusion during maintenance or presentation.

---

## 2. Appointment Model Naming Mismatch

### What the documentation says
The documentation references:
- `Appointments.java`

### What is actually in the repository
The actual model class is:
- `Appointment.java`

There is no `Appointments.java` file in the repository.

### Evidence

```java
public class Appointment {
   private String customerName;
   private String customerTP;
   private String plate;
   private String service;

   public Appointment(
      String customerName,
      String customerTP,
      String plate,
      String service,
      String technician,
      String techTP,
      String date,
      String time,
      String status,
      String paymentStatus
   ) {
      this.customerName = customerName;
      this.customerTP = customerTP;
      this.plate = plate;
      this.service = service;
   }
}
```

### Notes
Again, this is not a runtime issue, but the documentation should reflect the actual class names used in the implementation.

---

## 3. Customer Payment History Does Not Always Reflect Actual Payment Status

### What the documentation implies
The payment history feature suggests that customers can view correct Paid/Unpaid payment statuses.

### What is actually happening
In the customer UI, any appointment marked as `"Completed"` is automatically displayed as `"Paid"`.

The stored payment status is not checked before displaying the result.

### Evidence — CustomerPage.java

```java
if (status.equalsIgnoreCase("Completed")) {
   String price = CustomerHandler.getPriceForService(row[3]);

   model.addRow(new Object[]{
      row[3],
      row[6],
      price,
      "Paid"
   });
}
```

### What the counter/payment logic actually does
The real payment status is stored separately inside `appointments.txt`.

`CounterHandler.java` updates payment status using:

```java
parts[10] = "PAID";
```

and later checks:

```java
String paymentStatus = parts[10].trim();

if (status.equalsIgnoreCase("Completed")
   && paymentStatus.equalsIgnoreCase("PAID")) {

   // treated as completed + paid
}
```

### Why this matters
This means:
- a completed appointment can still appear as `"Paid"` even if payment was never collected,
- the customer view may not match the actual stored payment state,
- and the UI is deriving business logic instead of reading the real payment field.

### Notes
The counter staff flow uses the correct payment status field, so the issue mainly affects the customer-side payment history display.

---

# Additional System Design Questions

These are not necessarily “bugs”, but they are implementation/design questions that came up during verification and refactoring review.

---

## 1. CSV Parsing

Currently, records are parsed using:

```java
String[] parts = line.split(",");
```

### Question
Why use manual CSV splitting instead of a safer CSV parser that can properly handle:
- commas inside values,
- escaping,
- quoted text,
- malformed rows?

---

## 2. Date & Time Handling

Dates and times are stored as formatted strings.

### Question
Why not use:
- `LocalDate`
- `LocalTime`
- or standardized ISO date formats

instead of relying on string comparisons?

---

## 3. Service Type Representation

Service types are represented using display strings such as:

```text
"Normal (1 Hour)"
```

### Question
Why not use:
- enums,
- constants,
- or structured service objects

to separate display text from business rules and duration logic?

---

## 4. Hardcoded File Paths

The system directly references paths such as:

```text
text/appointments.txt
```

### Question
Why not centralize file paths into:
- a configuration layer,
- constants class,
- or repository abstraction?

This would make maintenance easier if paths ever change.

---

## 5. File Rewrite Safety

The current approach rewrites files directly.

### Question
Why not use:
- temporary files,
- atomic rename operations,
- or file locking

to reduce the risk of partial writes or file corruption?

---

## 6. Project Structure / Build Tooling

The project currently has:
- no packages,
- no Maven/Gradle setup,
- and no formal build process.

### Question
Why not introduce:
- package organization,
- Maven/Gradle,
- and reproducible builds

to improve maintainability and project structure?

---

## 7. Appointment Availability Logic

The technician availability logic appears to rely on manual hour arithmetic.

### Question
Why not use:
- proper time-range comparisons,
- overlap detection,
- and date/time objects

instead of custom hour extraction logic?

---

#
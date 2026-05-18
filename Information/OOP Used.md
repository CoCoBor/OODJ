**OOP used:**

**Encapsulation**

used in: 

\- User.java

\- Appointment.java



✅Hiding private variable data 

✅Controlling access through getter and setter 



This was used:

* to prevent direct modification
* Let other classes read safely but not destroy data accidentally 



Appointment only have getter, because it can never be changed after it is made (Strictly for reading after creation)



**Abstraction**

Used in :

* UserFileHandler.java
* CounterHandler.java
* TechnicianHandler.java
* CustomerHandler.java



This was used:

* UI doesn't have to deal with logic 
* Easier debugging 
* Cleaner code



**Object Usage (Class-Based model)**

Example used inside:

Appointment appt = new Appointment(...)

User currentUser



This was used:

* Passing arrays would be messy so creating a structured objects helps a lot 
* Prevents index confusion especially in appointments.txt where there's a lot of data 

This was used for payments, receipts (Appointment.java) and getting user details (User.java). 



**Inheritance:**

Only used on JFrame: to inherit all window behavior 





**Some function dictionary** 

List 

List<String \[]> appointments = new ArrayList<>();

✅File grows dynamically and we don't know how long will it get 



List<Appointments>

* When list is shown like this it is because it's more easier to use getter (getDate())



Map<String, int\[]> report = new HashMap();

Map is used for grouping, automatic categorization and fast lookup

\*"Group data by service type"






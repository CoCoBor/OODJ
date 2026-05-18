Functions uses 

**Manager:**

* Able to Create, Read, Update, Delete (CRUD) Counter Staff, Technician and Manager

Click on Add User button to add user. 

Click on the table user if want to update/delete them 

*\*System will check if TP number exists before creating user*

*\*System will verify if phone number is 10 digits long (Only digits, alphabets will be rejected)*

*\*System will not allow to delete the last standing Manager in the list (Must have at least 1 Manager)*

*\*System will not allow anyone to update users' TP Number* 

*\*Will be save and updated to users\_id.txt*



* Able to set price for service (Major and Normal)

Click on Set Service Prices 

\*System will check if price are included or not (Warning will show if price is not included)



* Able to view report

Click on View Reports 

The report view will be total revenue and total completed service done



* Able to view Feedback 

Click on View Feedback 

Feedback will be provided by Technician and Customer 

\*Technician must complete their service and write a feedback for it to show 





**Counter Staff:**

* Able to edit own profile 

Click on edit profile 

*\*System will not allow anyone to update users' TP Number*

*\*System will verify if phone number is 10 digits long (Only digits, alphabets will be rejected)*

*\*Will be save and updated to users\_id.txt*



* Able to CRUD customers only 

First, Click on Manage Customer button 

Click on Add button to add customer.

Click on the table user if want to update/delete them

*\*System will check if TP number exists before creating user*

*\*System will verify if phone number is 10 digits long (Only digits, alphabets will be rejected)*

*\*System will not allow anyone to update users' TP Number*

*\*Will be save and updated to users\_id.txt*



* Able to create appointments and view all appointments created 

Click on Appointments button



Create Appointments:

1. Click on Create Appointments button
2. Choose customer, service type, date, time and technician
3. Click on confirm booking

\*System will check if technician is booked during the time slot. 

\*If technician is unavailable for 3 hours, booking within the 3 hours is not unavailable (A message will pop up)

\*Appointments will be saved in appointments.txt



View All Appointments:

1. Click on View All Appointments button
2. Appointments will appear in table format 

\*Data gathered from appointments.txt



* Able to receive payment and generate receipt 

First, click on Payment \& Receipt button

There will be 2 buttons Unpaid \& Paid 



Unpaid:

1. View completed service yet status Unpaid customers (Customer name, car plate, service, technician name, date, time, price and payment status)
2. If wish to receive payment, click onto specific customer
3. A pop up will appear with a button "Collect Payment"
4. After payment has been collected, customer will be removed from the UNPAID table 

\*This section only allows to collect payment 



Paid:

1. View all completed service AND status paid customer (Customer name, car plate, service, technician name, date, time, price and payment status)
2. If wish to generate receipt, click onto specific customer
3. A pop up will appear with a button "Generate receipt"
4. Click on it, a receipt will appear for confirmation

\*Receipt will be saved into receipt folder

\*This section only allows generate receipt 

\*Data gathered from price.txt, appointments.txt and customer\_vehicle.txt



**Technician:**

* Able to edit own profile

Click on edit profile

*\*System will not allow anyone to update users' TP Number*

*\*System will verify if phone number is 10 digits long (Only digits, alphabets will be rejected)*

*\*Will be save and updated to users\_id.txt*



* Able to view appointments assigned 

Click on My Appointments button

1. Appointments table assigned to this technician will show
2. By double clicking on specific assigned customer, technician is able to mark service complete and leave comment (leave comment first before marking complete)
3. Technician's are also able to view comments leave by customers for the service

\*(pre-populates if feedback already exists)



* View own feedbacks on the service 

Click on View Feedbacks button

1. After leaving a review on the work done, technician's can view their own feedback for the specific service as a review



\*Data gathered from feedback.txt and appointments.txt





**Customer:**

* Able to edit own profile

Click on edit profile

*\*System will not allow anyone to update users' TP Number*

*\*System will verify if phone number is 10 digits long (Only digits, alphabets will be rejected)*

*\*Will be save and updated to users\_id.txt*



* View service history 

Click on view Service History button

1. A table of all service done in history for this customer (Service, Technician, Date, Time, Status)
2. Status of service will show (pending/completed)
3. Double clicking on a specific service will allow customer to leave feedback to technician and manager

\*Data gathered from appointments.txt

\*Customer feedbacks will be saved into appointments.txt arraylist\[10]. It will rewrite the line/file after comment is saved 

\*When customer uses ',' comma, it will auto changes to ';' to prevent clashes with the split code of ','



* View payment history 

Click on Payment History button

1. A table of all payment history for services will show (Service, Date, Price and Status)
2. Status of payment will show (Unpaid/Paid)



* View Feedbacks

Click on view Feedbacks button

1. View feedbacks from technician on service provided to review/understand the summarization of the service 

Table format (Date, Time, Technician and Feedback)





\*System will match customer's and technician details using their TP number(unique) so even with updating their profile, it wouldn't loose their previous data 
















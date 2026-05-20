package frontend;

import frontend.counterstaff.AppointmentManagementPage;
import frontend.counterstaff.CustomerManagementPage;
import frontend.customer.ViewServiceHistoryPage;
import frontend.manager.StaffManagementPage;
import frontend.technician.UpdateAppointmentPage;
import java.awt.*;
import javax.swing.*;
import models.User;
import models.enums.Role;
import repository.UserRepository;
import service.UserService;

public class DashboardPage extends JPanel {
    private final JTabbedPane tabbedPane;
    private final JLabel userStatusLabel;
    private final MainPage app;
    private UserRepository userRepository;
    private service.ServicePriceService servicePriceService;
    private service.PaymentService paymentService;
    private service.FeedbackService feedbackService;

    public DashboardPage(MainPage app) {
        this.app = app;
        setLayout(new BorderLayout());
        
        // 1. Create the Tabbed Pane
        tabbedPane = new JTabbedPane();
        
        // 2. Add a Header with User Info and Logout
        JPanel header = new JPanel(new BorderLayout());
        userStatusLabel = new JLabel(" Logged in as: ");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            app.getAuthService().logout();
            app.showPage("LOGIN");
        });
        header.add(userStatusLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        
        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setServicePriceService(service.ServicePriceService s) { this.servicePriceService = s; }
    public void setPaymentService(service.PaymentService s) { this.paymentService = s; }
    public void setFeedbackService(service.FeedbackService s) { this.feedbackService = s; }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // This method is called by MainPage right after a successful login
    public void setupDashboard(User user, UserService userService) {
        tabbedPane.removeAll(); // Clear previous session's tabs
        userStatusLabel.setText(" Logged in: " + user.getUsername() + " (" + user.getRole() + ")");

        Role role = user.getRole();

        // 3. Role-Based Logic: Add tabs based on permissions
        if (role == Role.MANAGER) {
            tabbedPane.addTab("Staff Management", new StaffManagementPage(userService));
            if (paymentService != null) {
                tabbedPane.addTab("Payment History", new frontend.manager.ViewPaymentHistoryPage(paymentService));
            }
            if (servicePriceService != null) {
                tabbedPane.addTab("Set Prices", new frontend.manager.SetPricePage(servicePriceService));
            }
            if (feedbackService != null) {
                tabbedPane.addTab("View Comments", new frontend.manager.ViewCommentPage(feedbackService));
            }
            // tabbedPane.addTab("Customer Approval", new CustomerApprovalPanel());
            // tabbedPane.addTab("Reports", new ReportsPanel());
        }
        
        if (role == Role.COUNTER_STAFF) {
            tabbedPane.addTab("Customer Management", new CustomerManagementPage(userService));
            tabbedPane.addTab("Appointment Management", new AppointmentManagementPage(app.getAppointmentService(), userRepository));
            if (paymentService != null && servicePriceService != null) {
                tabbedPane.addTab("Payments", new frontend.counterstaff.PaymentPage(paymentService, servicePriceService));
            }
            // tabbedPane.addTab("Customer Records", new CustomerRecordsPanel());
        }

        if (role == Role.TECHNICIAN) {
            tabbedPane.addTab("My Appointments", new UpdateAppointmentPage(app.getAppointmentService(), user.getUserId()));
        }

        if (role == Role.CUSTOMER) {
            tabbedPane.addTab("Service History", new ViewServiceHistoryPage(app.getAppointmentService(), user.getUserId()));
            if (paymentService != null) {
                tabbedPane.addTab("Payment History", new frontend.customer.ViewPaymentHistoryPage(paymentService, user.getUserId()));
            }
            if (feedbackService != null && paymentService != null) {
                tabbedPane.addTab("Feedback", new frontend.customer.FeedbackPage(feedbackService, paymentService, user.getUserId()));
            }
        }

        tabbedPane.addTab("My Profile", new PersonalProfilePage(userService));
    }
}
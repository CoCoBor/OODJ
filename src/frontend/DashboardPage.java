package frontend;

import backend.models.User;
import backend.models.enums.Role;
import backend.repository.UserRepository;
import backend.service.UserService;
import frontend.counterstaff.AppointmentManagementPage;
import frontend.counterstaff.CustomerManagementPage;
import frontend.customer.ViewServiceHistoryPage;
import frontend.manager.AnalyzeReportPage;
import frontend.manager.StaffManagementPage;
import frontend.technician.UpdateAppointmentPage;
import java.awt.*;
import javax.swing.*;

public class DashboardPage extends JPanel {
    private final JTabbedPane tabbedPane;
    private final JLabel userStatusLabel;
    private final MainPage app;
    private UserRepository userRepository;
    private backend.service.ServicePriceService servicePriceService;
    private backend.service.PaymentService paymentService;
    private backend.service.FeedbackService feedbackService;
    private backend.service.GenerateReportService reportService;
    private StaffManagementPage staffManagementPage;
    private boolean staffCommentPopupShown;
    private Timer staffCommentPopupTimer;

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

    public void setServicePriceService(backend.service.ServicePriceService s) { this.servicePriceService = s; }
    public void setPaymentService(backend.service.PaymentService s) { this.paymentService = s; }
    public void setFeedbackService(backend.service.FeedbackService s) { this.feedbackService = s; }
    public void setReportService(backend.service.GenerateReportService s) { this.reportService = s; }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void triggerManagerCommentPopupAfterDelay(int delayMillis) {
        if (staffCommentPopupShown) {
            System.out.println("DEBUG: popup timer skipped because staffCommentPopupShown is already true");
            return;
        }

        if (staffCommentPopupTimer != null && staffCommentPopupTimer.isRunning()) {
            System.out.println("DEBUG: stopping previous popup timer");
            staffCommentPopupTimer.stop();
        }

        System.out.println("DEBUG: scheduling popup timer for " + delayMillis + " ms");
        staffCommentPopupTimer = new Timer(delayMillis, e -> {
            System.out.println("DEBUG: popup timer fired");
            if (!staffCommentPopupShown) {
                staffCommentPopupShown = true;
                showManagerUnreadCommentsPopup();
            }
        });
        staffCommentPopupTimer.setRepeats(false);
        staffCommentPopupTimer.start();
    }

    public void showManagerUnreadCommentsPopup() {
        if (feedbackService == null) {
            return;
        }

        System.out.println("DEBUG: checking unread manager comments now");
        if (app.getAuthService() != null && app.getAuthService() != null) {
            System.out.println("DEBUG: current manager lastActiveTime = " +
                    (app.getAuthService().getClass() != null ? "see AuthService/session state at runtime" : "unknown"));
        }
        int newCommentCount = feedbackService.newCommentPopop();
        System.out.println("DEBUG: unread manager comment count = " + newCommentCount);
        if (newCommentCount > 0) {
            JOptionPane.showMessageDialog(this,
                    "You have " + newCommentCount + " new comment(s) you haven't viewed.",
                    "New Comments",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // This method is called by MainPage right after a successful login
    public void setupDashboard(User user, UserService userService) {
        tabbedPane.removeAll(); // Clear previous session's tabs
        userStatusLabel.setText(" Logged in: " + user.getUsername() + " (" + user.getRole() + ")");
        staffCommentPopupShown = false;

        if (staffCommentPopupTimer != null && staffCommentPopupTimer.isRunning()) {
            staffCommentPopupTimer.stop();
        }

        Role role = user.getRole();

        // 3. Role-Based Logic: Add tabs based on permissions
        if (role == Role.MANAGER) {
            staffManagementPage = new StaffManagementPage(userService, feedbackService);
            tabbedPane.addTab("Staff Management", staffManagementPage);
            if (paymentService != null) {
                tabbedPane.addTab("Payment History", new frontend.manager.ViewPaymentHistoryPage(paymentService));
            }
            if (servicePriceService != null) {
                tabbedPane.addTab("Set Prices", new frontend.manager.SetPricePage(servicePriceService));
            }
            if (feedbackService != null) {
                tabbedPane.addTab("View Comments", new frontend.manager.ViewCommentPage(feedbackService));
            }
            if (reportService != null) {
                tabbedPane.addTab("Reports", new AnalyzeReportPage(reportService));
            }
            tabbedPane.setSelectedIndex(0);
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
            if (feedbackService != null) {
                tabbedPane.addTab("View Comments", new frontend.technician.ViewCommentPage(feedbackService, user.getUserId()));
            }
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
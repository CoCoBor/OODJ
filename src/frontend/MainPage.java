package frontend;

import backend.repository.AppointmentRepository;
import backend.repository.FeedbackRepository;
import backend.repository.PaymentRepository;
import backend.repository.ServicePriceRepository;
import backend.repository.UserRepository;
import backend.service.AppoinmentService;
import backend.service.AuthService;
import backend.service.FeedbackService;
import backend.service.GenerateReportService;
import backend.service.PaymentService;
import backend.service.ServicePriceService;
import backend.service.UserService;
import backend.util.SessionManager;
import frontend.auth.LoginPage;
import java.awt.*;
import javax.swing.*;

public class MainPage extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final AuthService authService;
    private final LoginPage loginPage;
    private final DashboardPage dashboardPage;
    private final UserService userService;
    private final AppoinmentService appointmentService;
    private final UserRepository userRepository;
    private final GenerateReportService generateReportService;

    public MainPage() {
        this.userRepository = new UserRepository();
        AppointmentRepository apptRepo = new AppointmentRepository();
        SessionManager session = SessionManager.getInstance();
        ServicePriceRepository priceRepo = new ServicePriceRepository();
        PaymentRepository paymentRepo = new PaymentRepository();
        FeedbackRepository feedbackRepo = new FeedbackRepository();

        this.authService = new AuthService(userRepository, session);
        this.userService = new UserService(userRepository, session, apptRepo);
        this.appointmentService = new AppoinmentService(apptRepo, userRepository, session);

        ServicePriceService priceService = new ServicePriceService(priceRepo, session);
        PaymentService paymentService = new PaymentService(session, priceRepo, apptRepo, paymentRepo);
        FeedbackService feedbackService = new FeedbackService(feedbackRepo, apptRepo, userRepository, session);
        this.generateReportService = new GenerateReportService(feedbackRepo, apptRepo, userRepository, priceRepo, session);
        setTitle("Car Workshop System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add Pages
        this.loginPage = new LoginPage(this);
        this.dashboardPage = new DashboardPage(this);
        this.dashboardPage.setUserRepository(userRepository);
        this.dashboardPage.setServicePriceService(priceService);
        this.dashboardPage.setPaymentService(paymentService);
        this.dashboardPage.setFeedbackService(feedbackService);
        this.dashboardPage.setReportService(generateReportService);
        mainPanel.add(loginPage, "LOGIN");
        mainPanel.add(dashboardPage, "DASHBOARD");

        add(mainPanel);
        setVisible(true);
    }

    public DashboardPage getDashboardPage() {
        return dashboardPage;
    }

    public void showPage(String pageName) {
        cardLayout.show(mainPanel, pageName);
    }

    public AuthService getAuthService() {
        return authService;
    }

    public UserService getUserService() {
        return userService;
    }

    public AppoinmentService getAppointmentService() {
        return appointmentService;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainPage::new);
    }
}

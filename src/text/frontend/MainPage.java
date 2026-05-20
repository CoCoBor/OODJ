package src.text.frontend;

import java.awt.*;
import javax.swing.*;
import repository.AppointmentRepository;
import repository.FeedbackRepository;
import repository.PaymentRepository;
import repository.ServicePriceRepository;
import repository.UserRepository;
import service.AppoinmentService;
import service.AuthService;
import service.FeedbackService;
import service.PaymentService;
import service.ServicePriceService;
import service.UserService;
import src.text.frontend.auth.LoginPage;
import util.SessionManager;

public class MainPage extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final AuthService authService;
    private final LoginPage loginPage;
    private final DashboardPage dashboardPage;
    private final UserService userService;
    private final AppoinmentService appointmentService;
    private final UserRepository userRepository;

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

package backend.models;

public class Feedback {
    private String customerTP;
    private String date;
    private String time;
    private String techTP; // technician TP
    private String techName;
    private String feedbackText;

    public Feedback() {}

    public Feedback(String customerTP, String date, String time, String techTP, String techName, String feedbackText) {
        this.customerTP = customerTP;
        this.date = date;
        this.time = time;
        this.techTP = techTP;
        this.techName = techName;
        this.feedbackText = feedbackText;
    }

    public String getCustomerTP() { return customerTP; }
    public void setCustomerTP(String customerTP) { this.customerTP = customerTP; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getTechTP() { return techTP; }
    public void setTechTP(String techTP) { this.techTP = techTP; }

    public String getTechName() { return techName; }
    public void setTechName(String techName) { this.techName = techName; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }
}

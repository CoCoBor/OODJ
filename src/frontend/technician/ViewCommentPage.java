package frontend.technician;

import backend.models.CustomerFeedback;
import backend.service.FeedbackService;
import backend.service.ServiceException;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ViewCommentPage extends JPanel {

	private final FeedbackService feedbackService;
	private final String technicianId;
	private JTable table;
	private DefaultTableModel model;

	public ViewCommentPage(FeedbackService feedbackService, String technicianId) {
		this.feedbackService = feedbackService;
		this.technicianId = technicianId;
		setLayout(new BorderLayout(8,8));
		initTable();
		refresh();
	}

	private void initTable() {
		String[] cols = {"Feedback ID", "Appointment ID", "Customer ID", "Rating", "Comments", "Created At"};
		model = new DefaultTableModel(cols, 0) {
			@Override public boolean isCellEditable(int r, int c) { return false; }
		};
		table = new JTable(model);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private void refresh() {
		model.setRowCount(0);
		try {
			List<CustomerFeedback> feedbacks = feedbackService.getTechnicianFeedback(technicianId);
			for (CustomerFeedback f : feedbacks) {
				model.addRow(new Object[]{f.getFeedbackId(), f.getAppointmentId(), f.getCustomerId(), f.getRating(), f.getComment(), f.getcommentDateTime()});
			}
		} catch (ServiceException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}

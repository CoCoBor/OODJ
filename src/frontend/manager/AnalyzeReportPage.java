package frontend.manager;

import backend.service.GenerateReportService;
import backend.service.ServiceException;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AnalyzeReportPage extends JPanel {

	private final GenerateReportService reportService;
	private final JTextArea reportArea;

	public AnalyzeReportPage(GenerateReportService reportService) {
		this.reportService = reportService;
		setLayout(new BorderLayout(8, 8));

		reportArea = new JTextArea();
		reportArea.setEditable(false);
		reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		reportArea.setLineWrap(false);
		add(new JScrollPane(reportArea), BorderLayout.CENTER);

		JButton refreshBtn = new JButton("Refresh Report");
		refreshBtn.addActionListener(e -> refreshReport());
		add(refreshBtn, BorderLayout.SOUTH);

		refreshReport();
	}

	private void refreshReport() {
		try {
			reportArea.setText(reportService.generateReport());
			reportArea.setCaretPosition(0);
		} catch (ServiceException ex) {
			reportArea.setText(ex.getMessage());
		}
	}
}

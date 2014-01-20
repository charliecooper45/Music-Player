package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
@SuppressWarnings("serial")
public class ProgressDialog extends JDialog{
	
	private JButton cancelButton;
	private JProgressBar progressBar;
	private ProgressDialogListener progressDialogListener;
	private SwingWorker<Void, Integer> worker;
	
	public ProgressDialog(JFrame parent, int maximumSize) {
		super(parent, "Getting MP3s...", ModalityType.APPLICATION_MODAL);
		setLayout(new FlowLayout());
		setSize(400, 200);
		setLocationRelativeTo(parent);
		
		init(maximumSize);
	}
	
	private void init(int maximumSize) {
		progressBar = new JProgressBar();
		progressBar.setMaximum(maximumSize);
		add(progressBar);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setName("cancel");
		add(cancelButton);
		
		Dimension size = cancelButton.getPreferredSize();
		size.width = 400;
		progressBar.setPreferredSize(size);
		
		pack();
	}
	
	/**
	 * @param value the current value of the JProgressBar
	 */
	public void setValue(int value) {
		progressBar.setValue(value);
	}

	/**
	 * @param progressDialogListener the listener to set
	 */
	public void setProgressDialogListener(ProgressDialogListener progressDialogListener) {
		this.progressDialogListener = progressDialogListener;
		
		// After the listener is set start to update the GUI
		worker = new SwingWorker<Void, Integer>() {

			@Override
			protected void process(List<Integer> chunks) {
				int value = chunks.get(chunks.size() - 1);
				progressBar.setValue(value);
			}

			@Override
			protected Void doInBackground() throws Exception {
				int value = 0;
				while(value < progressBar.getMaximum() && !isCancelled()) {
					value = ProgressDialog.this.progressDialogListener.getNumberProcessed();
					publish(value);
				}
				if(!isCancelled()) {
					publish(value);
					ProgressDialog.this.dispose();
				}
				return null;
			}
		};
		worker.execute();
		ProgressDialog.this.dispose();
	}
	
	public void disposeDialog() {
		if(worker != null) {
			worker.cancel(true);
			this.dispose();
		}
	}
	
	/**
	 * Adds an ActionListener from the view to the appropriate components
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		// Add listener to the buttons
		cancelButton.addActionListener(listener);
	}
}

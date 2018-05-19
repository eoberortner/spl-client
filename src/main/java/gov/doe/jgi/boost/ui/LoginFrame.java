package gov.doe.jgi.boost.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import gov.doe.jgi.boost.client.constants.BOOSTConstants;
import gov.doe.jgi.boost.client.utils.UIUtils;

public class LoginFrame implements ActionListener{
	
	private JLabel userLabel;
    private JTextField userText;;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
	private JButton submitButton;
	private JButton cancelButton;

	 public LoginFrame(){
		JFrame frame = new JFrame("Login to BOOST");
		frame.setSize(300, 150);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		placeComponents(frame);
        frame.setVisible(true);	
	}
	
	private void placeComponents(JFrame jFrame) {

		GroupLayout layout = new GroupLayout(jFrame.getContentPane());
	    jFrame.getContentPane().setLayout(layout);
	    
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);

		userLabel = new JLabel("User Name");
	    userText = new JTextField(20);
	    passwordLabel = new JLabel("Password");
        passwordText = new JPasswordField(20);
		submitButton = new JButton("Submit");
		cancelButton = new JButton("Cancel");
		
		submitButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(userLabel)
						.addComponent(passwordLabel)
						.addComponent(cancelButton))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING))
			            .addComponent(userText)
			            .addComponent(passwordText)
			            .addComponent(submitButton)
				);
		
		// we would like the buttons to be always the same size
	    layout.linkSize(SwingConstants.HORIZONTAL, cancelButton, submitButton);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userLabel)
				        .addComponent(userText))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(passwordLabel)
			            .addComponent(passwordText))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(cancelButton)
			            .addComponent(submitButton))
			    );
		
		jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(true);			
   }

	@Override
	public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
		if (source == submitButton) {
			System.out.println("Submit button was clicked");
		} else if (source == cancelButton) {
			System.out.println("Cancel button was clicked");
		} 
	}		
}


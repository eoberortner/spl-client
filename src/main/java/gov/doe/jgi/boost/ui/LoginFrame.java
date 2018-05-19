package gov.doe.jgi.boost.ui;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginFrame {

	 public LoginFrame(){
		JFrame frame = new JFrame("Login to BOOST");
		frame.setSize(300, 150);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		placeComponents(frame);
        frame.setVisible(true);	
	}
	
	private static void placeComponents(JFrame jFrame) {

		GroupLayout layout = new GroupLayout(jFrame.getContentPane());
	    jFrame.getContentPane().setLayout(layout);
	    
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);

		JLabel userLabel = new JLabel("User Name");
	    JTextField userText = new JTextField(20);
	    JLabel passwordLabel = new JLabel("Password");
        JPasswordField passwordText = new JPasswordField(20);
		JButton loginButton = new JButton("Submit");
		JButton cancelButton = new JButton("Cancel");
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(userLabel)
						.addComponent(passwordLabel)
						.addComponent(cancelButton))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING))
			            .addComponent(userText)
			            .addComponent(passwordText)
			            .addComponent(loginButton)
				);
		
		// we would like the buttons to be always the same size
	    layout.linkSize(SwingConstants.HORIZONTAL, cancelButton, loginButton);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(userLabel)
				        .addComponent(userText))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(passwordLabel)
			            .addComponent(passwordText))
			    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    		.addComponent(cancelButton)
			            .addComponent(loginButton))
			    );
		
		jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(true);			
   }
}

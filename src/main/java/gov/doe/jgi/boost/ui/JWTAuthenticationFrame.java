package gov.doe.jgi.boost.ui;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class JWTAuthenticationFrame {
	
	 public JWTAuthenticationFrame(){
			JFrame frame = new JFrame("JWTAuthentication");
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

			JLabel jWTTokenText = new JLabel("Please peovide your JWT Token:");
		    JTextField tokenText = new JTextField(25);
			JButton submitButton = new JButton("Submit");
			JButton cancelButton = new JButton("Cancel");
			
			layout.setHorizontalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(jWTTokenText)
							.addComponent(cancelButton))
				    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING))
				            .addComponent(tokenText)
				            .addComponent(submitButton)
					);
			
			// we would like the buttons to be always the same size
		    layout.linkSize(SwingConstants.HORIZONTAL, cancelButton, submitButton);
			
			layout.setVerticalGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(jWTTokenText)
					        .addComponent(tokenText))
				    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    		.addComponent(cancelButton)
				            .addComponent(submitButton))
				    );
			
			jFrame.pack();
	        jFrame.setLocationRelativeTo(null);
	        jFrame.setResizable(true);			
	 }
}

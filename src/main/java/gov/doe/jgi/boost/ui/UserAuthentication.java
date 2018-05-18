package gov.doe.jgi.boost.ui;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;


public class UserAuthentication {

	
	public UserAuthentication() {
		
		JFrame jFrame= new JFrame();  
		jFrame.setTitle("Prepare for Synthesis");        
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        placeComponents(jFrame);
        jFrame.setSize(400, 200);
		
        // Setting the frame visibility to true
		jFrame.setVisible(true);   
	}

	
	private void placeComponents(JFrame jFrame) {
		
		GroupLayout layout = new GroupLayout(jFrame.getContentPane());
	    jFrame.getContentPane().setLayout(layout);
	    
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);
	
        JLabel headingText = new JLabel("Please choose one of the given way to Authenticate your Account:");
        JRadioButton buttonLogin = new JRadioButton("Login");
        JRadioButton buttonJWT = new JRadioButton("Provide your JWT");
        JRadioButton buttonSignUp = new JRadioButton("Sign Up");
        
        layout.setHorizontalGroup(
        		layout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	              .addComponent(headingText)
        	              .addComponent(buttonLogin)
        	              .addComponent(buttonJWT)
        	              .addComponent(buttonSignUp)
        	      );
        	      
        layout.setVerticalGroup(
        		   layout.createSequentialGroup()
        		          .addComponent(headingText)
 	                      .addComponent(buttonLogin)
 	                      .addComponent(buttonJWT)
 	                      .addComponent(buttonSignUp)
 	              );  
        
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
	}
}

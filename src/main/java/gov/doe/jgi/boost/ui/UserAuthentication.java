package gov.doe.jgi.boost.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import gov.doe.jgi.boost.client.constants.BOOSTConstants;
import gov.doe.jgi.boost.client.utils.UIUtils;


public class UserAuthentication implements ActionListener {

	private JLabel headingText;
	private JRadioButton buttonLogin;
    private JRadioButton buttonJWT;
    private JRadioButton buttonSignUp;
    
	public UserAuthentication() {
		
		JFrame jFrame= new JFrame();  
		jFrame.setTitle("Prepare for Synthesis");        
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
	
        headingText = new JLabel("Please choose one of the given way to Authenticate your Account:");
        buttonLogin = new JRadioButton("Login");
        buttonJWT = new JRadioButton("Provide your JWT token");
        buttonSignUp = new JRadioButton("Sign Up");
        
        ButtonGroup group = new ButtonGroup();
        group.add(buttonLogin);
        group.add(buttonJWT);
        group.add(buttonSignUp);
        
        buttonLogin.addActionListener(this);
        buttonJWT.addActionListener(this);
        buttonSignUp.addActionListener(this);
        
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
        jFrame.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		
		if (source == buttonLogin) {
			System.out.println("Login button is clicked");
			new LoginFrame();
		} else if (source == buttonJWT) {
		    new JWTAuthenticationFrame();
		} else if (source == buttonSignUp) {
			UIUtils.openWebPage(BOOSTConstants.SIGNUP_URI);
		}
	}	
}

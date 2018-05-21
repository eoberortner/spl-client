package gov.doe.jgi.boost.ui;
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

public class AvailableTasksBOOST{

	private JFrame frame;
	private JLabel instructionText;
	private JRadioButton reverseTranstationBtn;
	private JRadioButton codonJugglingBtn;
	private JRadioButton sequenceModificationBtn;
	private JRadioButton sequencePartitionBtn;
	private JButton cancelButton;
	private JButton submitButton;

	public AvailableTasksBOOST() {

		JFrame frame = new JFrame("Abailable Operations for Sequence");
		frame.setSize(500, 270);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		placeComponents(frame);
		frame.setVisible(true);
	}

	private void placeComponents(JFrame jFrame) {

		GroupLayout layout = new GroupLayout(jFrame.getContentPane());
	    jFrame.getContentPane().setLayout(layout);
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);
		
		instructionText = new JLabel("Please select the operaton(s) you want to perform with your sequence");
		reverseTranstationBtn = new JRadioButton("Reverse-Translation of protein to DNA sequences");
		codonJugglingBtn = new JRadioButton("Codon-Juggling of protein coding DNA sequences");
		sequenceModificationBtn = new JRadioButton("Modification of protein coding sequences (\"CDS\") for efficient synthesis");
		sequencePartitionBtn = new JRadioButton("Partition of large DNA sequences into synthesizable building blocks");
		
		cancelButton = new JButton("Cancel");
		submitButton = new JButton("Submit");
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
						.addComponent(sequenceModificationBtn)
						.addComponent(sequencePartitionBtn)
						.addComponent(codonJugglingBtn)
						.addComponent(reverseTranstationBtn)
						.addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(Alignment.LEADING, layout.createSequentialGroup()
								.addComponent(cancelButton)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(submitButton))
							.addComponent(instructionText, Alignment.LEADING))));
		
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				    .addGroup(layout.createSequentialGroup()
					    .addGap(26)
					    .addComponent(instructionText)
					    .addGap(28)
					    .addComponent(reverseTranstationBtn)
					    .addPreferredGap(ComponentPlacement.UNRELATED)
					    .addComponent(codonJugglingBtn)
					    .addPreferredGap(ComponentPlacement.UNRELATED)
					    .addComponent(sequenceModificationBtn)
					    .addPreferredGap(ComponentPlacement.UNRELATED)
					    .addComponent(sequencePartitionBtn)
					    .addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
					    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
						    .addComponent(cancelButton)
						    .addComponent(submitButton))
					.addGap(29)));
		
		jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(true);
	}
}

package gov.doe.jgi.boost.exception;

public class BOOSTBackEndException 
		extends Exception {

	private static final long serialVersionUID = 756033214227652213L;
	
	private int status;
	
	public BOOSTBackEndException(int status, String errorMsg) {
		super(errorMsg);
		
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("status-code: ").append(this.getStatus()).append(" ... ")
			.append(this.getMessage());
		return sb.toString();
	}
}

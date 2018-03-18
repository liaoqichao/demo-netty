package netty.demo3.pojo;

import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4713783053518802966L;
	private String username;
	private String psw;
	private String validateCode;
	private String snd;
	private String rcv;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPsw() {
		return psw;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
	public String getValidateCode() {
		return validateCode;
	}
	public void setValidateCode(String validateCode) {
		this.validateCode = validateCode;
	}
	public String getSnd() {
		return snd;
	}
	public void setSnd(String snd) {
		this.snd = snd;
	}
	public String getRcv() {
		return rcv;
	}
	public void setRcv(String rcv) {
		this.rcv = rcv;
	}
	@Override
	public String toString() {
		return "Message [username=" + username + ", psw=" + psw + ", validateCode=" + validateCode + ", snd=" + snd
				+ ", rcv=" + rcv + "]";
	}
}

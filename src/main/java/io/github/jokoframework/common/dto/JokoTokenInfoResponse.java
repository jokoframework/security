package io.github.jokoframework.common.dto;

public class JokoTokenInfoResponse {

	private String userId;
	private String audiencie;
	private Long expiresIn;
	private Boolean revoked;

	public String getAudiencie() {
		return audiencie;
	}

	public void setAudiencie(String audiencie) {
		this.audiencie = audiencie;
	}

	public String getUserId() {
		return userId;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public Boolean getRevoked() {
		return revoked;
	}

	private void setUserId(String userId) {
		this.userId = userId;
	}

	private void setAudience(String audience) {
		this.audiencie = audience;
	}

	private void setRevoked(Boolean revoked) {
		this.revoked = revoked;
	}

	private void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public static class Builder {
		private String userId;
		private String audience;
		private Long expiresIn;
		private Boolean revoked;
	
	
		public Builder userId(String userId) {
			this.userId = userId;	
			return this;
		}
		
		public Builder audience(String audience) {
			this.audience = audience;	
			return this;
		}
		
		public Builder expiresIn(Long expiresIn) {
			this.expiresIn = expiresIn;	
			return this;
		}
		
		public Builder revoked(Boolean revoked) {
			this.revoked = revoked;	
			return this;
		}
		
		public JokoTokenInfoResponse build() {
			return new JokoTokenInfoResponse(this);
		}
	}
	
	public JokoTokenInfoResponse(Builder builder) {
		// TODO Auto-generated constructor stub
		this.setExpiresIn(builder.expiresIn);
		this.setRevoked(builder.revoked);
		this.setAudience(builder.audience);
		this.setUserId(builder.userId);
		
	}


	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "JokoTokenInfoResponse [userId=" + userId + ", audiencie=" + audiencie + ", expiresIn=" + expiresIn
				+ ", revoked=" + revoked + "]";
	}

}

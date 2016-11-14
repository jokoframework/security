package io.github.jokoframework.common.dto;

public class JokoTokenInfoResponse extends JokoBaseResponse {

	private String userId;
	
	private String audiencie;
	
	private Long expiresIn;

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


	private void setUserId(String userId) {
		this.userId = userId;
	}

	private void setAudience(String audience) {
		this.audiencie = audience;
	}

	private void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public static class Builder {
		private String userId;
		private String audience;
		private Long expiresIn;
		private Boolean success;
	
	
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
		

		public Builder success(Boolean success) {
			this.success = success;
			return this;
		}
		
		public JokoTokenInfoResponse build() {
			return new JokoTokenInfoResponse(this);
		}
	}
	
	public JokoTokenInfoResponse(Builder builder) {
		this.setExpiresIn(builder.expiresIn);
		this.setAudience(builder.audience);
		this.setUserId(builder.userId);
		this.setSuccess(builder.success);
		
	}


	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "JokoTokenInfoResponse [userId=" + userId + ", audiencie=" + audiencie + ", expiresIn=" + expiresIn
				+ "]";
	}

}

package com.karry.tool.Bean;

public class QRCodeBean {
	private String reason;
	private int error_code;
	private codeInfo result;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}

	public codeInfo getResult() {
		return result;
	}

	public void setResult(codeInfo result) {
		this.result = result;
	}

	public static class codeInfo {
		private String base64_image;

		public String getBase64_image() {
			return base64_image;
		}

		public void setBase64_image(String base64_image) {
			this.base64_image = base64_image;
		}
	}
}

package com.karry.tool.Bean;

import com.google.gson.annotations.SerializedName;

public class OilBean {
	private String reason;
	private OilInfo[] result;
	private String error_code;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public OilInfo[] getResult() {
		return result;
	}

	public void setResult(OilInfo[] result) {
		this.result = result;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public static class OilInfo {
		private String city;

		@SerializedName("92h")
		private String p_92;

		@SerializedName("95h")
		private String p_95;

		@SerializedName("98h")
		private String p_98;

		@SerializedName("0h")
		private String p_0;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getP_92() {
			return p_92;
		}

		public void setP_92(String p_92) {
			this.p_92 = p_92;
		}

		public String getP_95() {
			return p_95;
		}

		public void setP_95(String p_95) {
			this.p_95 = p_95;
		}

		public String getP_98() {
			return p_98;
		}

		public void setP_98(String p_98) {
			this.p_98 = p_98;
		}

		public String getP_0() {
			return p_0;
		}

		public void setP_0(String p_0) {
			this.p_0 = p_0;
		}
	}
}

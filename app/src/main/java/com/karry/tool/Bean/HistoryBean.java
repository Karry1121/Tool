package com.karry.tool.Bean;

public class HistoryBean {
	private String reason;
	private resultInfo[] result;
	private String error_code;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public resultInfo[] getResult() {
		return result;
	}

	public void setResult(resultInfo[] result) {
		this.result = result;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public static class resultInfo {
		private String day;
		private String date;
		private String title;
		private String e_id;

		public String getDay() {
			return day;
		}

		public void setDay(String day) {
			this.day = day;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getE_id() {
			return e_id;
		}

		public void setE_id(String e_id) {
			this.e_id = e_id;
		}
	}
}

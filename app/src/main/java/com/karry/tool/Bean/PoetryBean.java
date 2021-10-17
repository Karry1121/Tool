package com.karry.tool.Bean;

public class PoetryBean {
	private String status;
	private dataDetail data;
	private String token;
	private String ipAddress;
	private String warning;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public dataDetail getData() {
		return data;
	}

	public void setData(dataDetail data) {
		this.data = data;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public static class originDetail {
		private String title;
		private String dynasty;
		private String author;
		private String[] content;
		private String[] translate;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDynasty() {
			return dynasty;
		}

		public void setDynasty(String dynasty) {
			this.dynasty = dynasty;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String[] getContent() {
			return content;
		}

		public void setContent(String[] content) {
			this.content = content;
		}

		public String[] getTranslate() {
			return translate;
		}

		public void setTranslate(String[] translate) {
			this.translate = translate;
		}
	}
	public static class dataDetail {
		private String id;
		private String content;
		private String popularity;
		private originDetail origin;
		private String[] matchTags;
		private String recommendedReason;
		private String cacheAt;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getPopularity() {
			return popularity;
		}

		public void setPopularity(String popularity) {
			this.popularity = popularity;
		}

		public originDetail getOrigin() {
			return origin;
		}

		public void setOrigin(originDetail origin) {
			this.origin = origin;
		}

		public String[] getMatchTags() {
			return matchTags;
		}

		public void setMatchTags(String[] matchTags) {
			this.matchTags = matchTags;
		}

		public String getRecommendedReason() {
			return recommendedReason;
		}

		public void setRecommendedReason(String recommendedReason) {
			this.recommendedReason = recommendedReason;
		}

		public String getCacheAt() {
			return cacheAt;
		}

		public void setCacheAt(String cacheAt) {
			this.cacheAt = cacheAt;
		}
	}
}

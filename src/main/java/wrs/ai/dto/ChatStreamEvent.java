package wrs.ai.dto;

public record ChatStreamEvent(String type, String content) {

	public static ChatStreamEvent chunk(String content) {
		return new ChatStreamEvent("chunk", content);
	}

	public static ChatStreamEvent done() {
		return new ChatStreamEvent("done", "");
	}

	public static ChatStreamEvent error(String message) {
		return new ChatStreamEvent("error", message);
	}

}

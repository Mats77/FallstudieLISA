package Server;

public class ChatMessage {
	
	private String direction;
	private String avatar;
	private String name;
	private String time;
	private String message;
	
	public ChatMessage(String direction, String avatar, String name, String time, String message) {
		this.direction = direction;
		this.avatar    = avatar;
		this.name      = name;
		this.time      = time;
		this.message   = message;
	}

}

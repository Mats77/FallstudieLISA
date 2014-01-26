package Server;

public class ChatMessage {
	
	private String direction;
	private String avatar;
	private String name;
	private String time;
	private String message;
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ChatMessage(String direction, String avatar, String name, String time, String message) {
		this.direction = direction;
		this.avatar    = avatar;
		this.name      = name;
		this.time      = time;
		this.message   = message;
	}

}

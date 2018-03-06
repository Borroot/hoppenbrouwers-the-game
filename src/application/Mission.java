package application;

public class Mission {
	
	private boolean completed = false;
	private String name;
	private Part[] parts;
	
	public Mission(String name, Part[] parts) {
		this.name = name;
		this.parts = parts;
	}
	
	public String getName() {
		return name;
	}
	
	public Part[] getParts() {
		return parts;
	}
	
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public boolean isCompleted() {
		return completed;
	}
	
}

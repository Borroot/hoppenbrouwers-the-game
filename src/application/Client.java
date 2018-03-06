package application;

public class Client {
	
	private static final String[] feedback = {"Zoveel geld zit er niet in mijn/nspaarvarken, kan het niet anders?", 
			"De kwaliteitswaarde is te laag!", "Er mist naar mijn gevoel nog iets...", "Goed gedaan, ga zo door!"};
	
	private int budget, quality;
	private String picName;
	private String name, description;
	private Mission[] missions = new Mission[5];
	
	public Client(String name, String description, int budget, int quality, Mission[] missions) {
		this.name = name;
		this.description = description;
		this.budget = budget;
		this.quality = quality;
		this.missions = missions;
		this.picName = name.replace(" ", "_");
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getBudget() {
		return budget;
	}
	
	public int getQuality() {
		return quality;
	}
	
	public String getPicName() {
		return picName;
	}
	
	public String getFeedback(int index) {
		return feedback[index];
	}
	
	public Mission[] getMissions() {
		return missions;
	}
	
}

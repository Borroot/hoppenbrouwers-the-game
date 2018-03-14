package application;

import javafx.scene.image.Image;

public class Part {
	
	private int price, value, flap;
	private String pic;
	private String name, description;
	private boolean placed = false;
	
	public Part(String name, String description, int price, int value, int flap) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.value = value;
		this.flap = flap;
	}
	
	public Part(String name, String description, int price, int value, int flap, String pic) {
		this(name, description, price, value, flap);
		this.pic = pic;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public int getFlap() {
		return flap;
	}
	
	public Image getPicAbove() {
		return new Image("images/parts/above/" + pic + ".jpg", 1000, 800, true, false);
	}
	
	public Image getPicNormal() {
		return new Image("images/parts/normal/" + pic + ".png", 250, 250, true, false);
	}
	
	public void setPlaced(boolean placed) {
		this.placed = placed;
	}
	
	public boolean getPlaced() {
		return placed;
	}
	
}

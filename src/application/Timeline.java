package application;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Timeline extends GridPane{
	
	private Text[] progress = {new Text("Opdracht"), new Text("Schetsen"), new Text("Feedback"), new Text("Plannen"), new Text("Bouwen")};			
	
	public Timeline() {
		
		this.setHgap(80);
		this.setAlignment(Pos.BASELINE_CENTER);
		
		for(int i = 0; i < progress.length; i++) {
			progress[i].setFont(Font.font("Verdana", 50));
			this.add(progress[i], i, 0);
		}
	}
	
	public void setTimeline(int index) {
		progress[index].setFill(Color.BLACK);
		
		for(int i = 0; i < progress.length; i++) 
			if(i != index)
				progress[i].setFill(Color.SLATEGREY);
	}

}

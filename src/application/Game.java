package application;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game extends Application {
	
	BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, new CornerRadii(1), new Insets(0, 0, 0, 0));
	Background background = new Background(backgroundFill);
	
	private Stage window;
	private int level;
	private Client client;
	private Progressline timeline = new Progressline();	
	private boolean sketchDone = false;
	
	private Text txtExplanation = new Text();
	private BorderPane sketchPane = new BorderPane();
	private Scene sketchScene = new Scene(sketchPane);
	private VBox missionPane = new VBox(20);
	private GridPane partPanel = new GridPane();
	
	private int costs = 0, quality = 0;
	private StackPane templatePane;
	
	private ArrayList<Part> buildingParts = new ArrayList<>();
	private ArrayList<Integer[]> prices = new ArrayList<>();
	private ArrayList<Integer[]> qualities = new ArrayList<>();
	
	private Text txtTime = new Text("15");
	private Timeline animation = new Timeline(new KeyFrame(Duration.seconds(1), e -> timer()));
	private boolean schemeInteractEntered = false;
	
	private SerialCommunication com;
	private BorderPane buildingPane = new BorderPane();
	private VBox bottomPane = new VBox(20);
	private GridPane gridPaneRight = new GridPane(); // used for the picture of the part while building
	private Rectangle[] boxes = new Rectangle[10];
	boolean[] available;
	private Button btOpenFlap = new Button("Volgende");
	private Button btSubmit = new Button("Verder");
	
	private final Part[] PARTS_VERLICHTING =   {new Part("Lichtbron", "voorwerp dat licht geeft.", 1, 1, 0, "Lichtbron"), 
												new Part("Lichtknopje", "schakelaar die het licht aan laat gaan als hij wordt ingedrukt.", 1, 1, 0, "Lichtknopje")};
	private final Mission VERLICHTING = new Mission("Verlichting", PARTS_VERLICHTING);
	private final Part[] PARTS_CAMERABEVEILIGING =	{new Part("Beveiligingscamera", "camera aan het plafond of aan de muur van een gebouw die filmt wat er gebeurd", 1, 1, 1, "Beveiligingscamera"),
													 new Part("Monitor", "computerscherm waarop de camerabeelden waargenomen kunnen worden", 1, 1, 1, "Monitor")};
	private final Mission CAMERABEVEILIGING = new Mission("Camerabeveiliging", PARTS_CAMERABEVEILIGING);
	private final Part[] PARTS_BRANDMELDINSTALLATIE =	{new Part("Automatische brandmelder", "brandmelder die bij sporen van brand alaram slaat.", 1, 1, 2, "Automatische_brandmelder"),
														 new Part("Handmelder", "kastje aan de muur welke alarm slaat zodra hij ingedrukt wordt.", 1, 1, 2, "Handmelder")};
	private final Mission BRANDMELDINSTALLATIE = new Mission("Brandmeldinstallatie", PARTS_BRANDMELDINSTALLATIE);
	private final Part[] PARTS_PVINSTALLATIE =	{new Part("PV-paneel", "panelen op het dak die de lichtenergie van de zon omzetten in elektriciteit.", 1, 1, 3, "PV_paneel"),
												 new Part("Omvormer", "onderdeel dat de stroom vanuit de PV-panelen omzet in stroo die in huis gebruikt kan worden.", 1, 1, 3, "Omvormer"),
												 new Part("Accu", "voorwerp waarin overtollige stroom geleverd door de PV-panelen in kan worden opgeslagen.", 1, 1, 3, "Accu")};
	private final Mission PVINSTALLATIES = new Mission("PV-installatie", PARTS_PVINSTALLATIE);
	private final Part[] PARTS_VERPLEEGOPROEP =	{new Part("Monitor", "computerscherm waarop de camerabeelden waargenomen kunnen worden.", 1, 1, 4, "Monitor"),
														 new Part("Intercom", "apparaat waardoor gesproken kan worden.", 1, 1, 4, "Intercom"),
														 new Part("Bedieningspaneel", "voorwerp waarmee iets bediend kan worden.", 1, 1, 4, "Bedieningspaneel")};
	private final Mission VERPLEEGOPROEP = new Mission("Verpleegoproepsysteem", PARTS_VERPLEEGOPROEP);
	private final Part[] PARTS_LICHTENKRACHT = {new Part("Stopcontact", "voorwerp aan de muur waarin stekkers gestoken kunnen worden.", 1, 1, 5, "Stopcontact"),
												new Part("Kabelgoot", "een halfopen bak aan het plafond van een ruimte waar kabels liggen.", 1, 1, 5, "Kabelgoot")};
	private final Mission LICHTENKRACHT = new Mission("Licht- en krachtinstallatie", PARTS_LICHTENKRACHT);
	private final Part[] PARTS_INBRAAKDETECTIE =	{new Part("Bedieningspaneel", "voorwerp waarop de inbraakbeveiliging aan en uit gezet kan worden.", 1, 1, 6, "Bedieningspaneel_IDS"),
													 new Part("Magneetsensor", "sensor bij het raam die het openen van het raam waarneemt.", 1, 1, 6, "Magneetsensor"),
													 new Part("Infraroodsensor", "sensor die beweging in een ruimte waarneemt.", 1, 1, 6, "Infraroodsensor")};
	private final Mission INBRAAKDETECTIE = new Mission("Inbraakdetectiesysteem", PARTS_INBRAAKDETECTIE);
	private final Part[] PARTS_GEBOUWBEHEER =	{new Part("Aanraak scherm", "een scherm met daarop de plattegrond van het gebouw en belangrijke informatie.", 1, 1, 7, "Scherm"),
												 new Part("Systeemkast", "kast die bij grote gebouwen gebruikt wordt om data in op te slaan.", 1, 1, 7, "Systeemkast")};
	private final Mission GEBOUWBEHEER = new Mission("Gebouwbeheersysteem", PARTS_GEBOUWBEHEER);
	private final Part[] PARTS_STOFZUIGER = {new Part("Opvangbak", "voorwerp waar al het door de stofzuiger opgezogen vuil in terecht komt.", 1, 1, 8, "Opvangbak"),
									   new Part("Afzuigpunt", "opening in de muur waar de stofzuigerslang aangesloten kan worden.", 1, 1, 8, "Afzuigpunt")};
	private final Mission STOFZUIGER = new Mission("Stofzuigersysteem", PARTS_STOFZUIGER);
	private final Part[] PARTS_NOODVERLICHTING = {new Part("Centrale voeding", "onderdeel dat de stroom levert voor de noodverlichting.", 1, 1, 3, "Centrale_voeding"),
												  new Part("Vluchtwegaanduiding", "groene bordjes met pijlen die naar de (nood)uitgang wijzen.", 1, 1, 3, "Vluchtwegaanduiding"),
												  new Part("Anti-paniekverlichting", "verlichting in het gebouw die aan gaat als de normale verlichting uitvalt.", 1, 1, 3, "Anti_paniekverlichting")};
	private final Mission NOODVERLICHTING = new Mission("Noodverlichting", PARTS_NOODVERLICHTING);
	
	
	
	public static void main(String[] args) {
		Application.launch();
	}

	
	
	@Override
	public void start(Stage window) throws Exception {
		
		this.window = window;
		window.setHeight(1080);
		window.setWidth(1920);
		
		boolean ready = false;
		do {
			try {
				com = new SerialCommunication();
				ready = true;
			} catch (Exception e) {
				System.out.println("Arduino is not plugged in");
				Thread.sleep(1000);
			}
		}while(!ready); 
		
		window.setTitle("Hoppenbrouwers The Game");
		window.show();
		beginScreen();
	}
	
	
	
	private void beginScreen() {
		
		BorderPane beginScreenPane = new BorderPane();
		// create the "Hoppenbrouwers the Game" text
		VBox txtPane = new VBox(20);
		txtPane.setAlignment(Pos.BASELINE_CENTER);
		Text txt1 = new Text("Hoppenbrouwers");
		txt1.setFont(Font.font("Verdana", FontWeight.BOLD, 150));
		Text txt2 = new Text("The Game");
		txt2.setFont(Font.font("Verdana", FontWeight.BOLD, 100));
		txtPane.getChildren().addAll(txt1, txt2);
		beginScreenPane.setTop(txtPane);
		
		// create and show the button
		Button btPlay = new Button("SPEEL");
		btPlay.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
		BorderPane.setAlignment(btPlay, Pos.CENTER);
		beginScreenPane.setCenter(btPlay);
		
		beginScreenPane.setBackground(background);
		Scene beginScreenScene = new Scene(beginScreenPane);
		window.setScene(beginScreenScene); 
		
		btPlay.setOnAction(e -> {
			levelScreen();	
		});
	}
	
	
	
	private void levelScreen() {
		
		BorderPane difficultyScreenPane = new BorderPane();
		// create the "Kies je niveau" text
		Text txtChoose = new Text("Kies je niveau");
		txtChoose.setFont(Font.font("Verdana", FontWeight.BOLD, 120));
		BorderPane.setAlignment(txtChoose, Pos.BOTTOM_CENTER);
		difficultyScreenPane.setTop(txtChoose);
		
		// create the difficulty buttons
		VBox btPane = new VBox(20);
		btPane.setAlignment(Pos.CENTER);
		Button btEasy = new Button("Amateur");
		btEasy.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		Button btNormal = new Button("Vakman");
		btNormal.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		btPane.getChildren().addAll(btEasy, btNormal);
		BorderPane.setAlignment(btPane, Pos.BOTTOM_CENTER);
		difficultyScreenPane.setCenter(btPane);
		
		difficultyScreenPane.setBackground(background);
		Scene difficultyScene = new Scene(difficultyScreenPane);
		window.setScene(difficultyScene);
		
		btEasy.setOnAction(e -> {
			level = 0;
			chooseClientScreen();
		});
		
		btNormal.setOnAction(e -> {
			level = 1;
			chooseClientScreen();
		});		
	}
	
	
	
	private void chooseClientScreen() {
		
		BorderPane chooseClientScreenPane = new BorderPane();
		// create the "Kies je opdrachtgever" text
		Text txtChoose = new Text("Kies je opdrachtgever");
		txtChoose.setFont(Font.font("Verdana", FontWeight.BOLD, 120));
		BorderPane.setAlignment(txtChoose, Pos.BOTTOM_CENTER);
		chooseClientScreenPane.setTop(txtChoose);
		
		// create the images for the clients
		ImageView imgvwHannekeHuizen = new ImageView(new Image("/clients/choose/Hanneke_Huizen.png"));
		ImageView imgvwWimPol = new ImageView(new Image("/clients/choose/Wim_Pol.png"));
		ImageView imgvwDirkTeur = new ImageView(new Image("/clients/choose/Dirk_Teur.png"));
		ImageView imgvwSophieSmit = new ImageView(new Image("/clients/choose/Sophie_Smit.png"));
		
		// create the client buttons
		GridPane clientsPane = new GridPane();
		clientsPane.setAlignment(Pos.CENTER);
		clientsPane.setHgap(20);
		clientsPane.setVgap(20);
		clientsPane.add(imgvwHannekeHuizen, 0, 0);
		clientsPane.add(imgvwWimPol, 1, 0);
		clientsPane.add(imgvwDirkTeur, 0, 1);
		clientsPane.add(imgvwSophieSmit, 1, 1);
		chooseClientScreenPane.setCenter(clientsPane);
		
		chooseClientScreenPane.setBackground(background);
		Scene chooseClientScene = new Scene(chooseClientScreenPane);
		window.setScene(chooseClientScene);
		
		imgvwHannekeHuizen.setOnMouseClicked(e -> {
			Mission[] missions = {VERLICHTING, PVINSTALLATIES, GEBOUWBEHEER, INBRAAKDETECTIE, STOFZUIGER};
			client = new Client("Hanneke Huizen", "Hanneke wilt haar oude landhuis opknappen!", 100, 1, missions);
			descriptionScreen();
		});
		
		imgvwWimPol.setOnMouseClicked(e -> {
			Mission[] missions = {LICHTENKRACHT, BRANDMELDINSTALLATIE, INBRAAKDETECTIE, CAMERABEVEILIGING, GEBOUWBEHEER};
			client = new Client("Wim Pol", "Wim gaat een nieuw hoofdkantoor bouwen!", 100, 1, missions);
			descriptionScreen();
		});
		
		imgvwDirkTeur.setOnMouseClicked(e -> {
			Mission[] missions = {GEBOUWBEHEER, BRANDMELDINSTALLATIE, NOODVERLICHTING, CAMERABEVEILIGING, PVINSTALLATIES};
			client = new Client("Dirk Teur", "Dirk gaat zijn school uitbouwen!", 100, 1, missions);
			descriptionScreen();
		});
		
		imgvwSophieSmit.setOnMouseClicked(e -> {
			Mission[] missions = {STOFZUIGER, NOODVERLICHTING, VERPLEEGOPROEP, BRANDMELDINSTALLATIE, GEBOUWBEHEER};
			client = new Client("Sophie Smit", "Sophie gaat het ziekenhuis renoveren!", 100, 1, missions);
			descriptionScreen();
		});
	}
	
	
	
	private void sketchScreen() {
		
		// set the timeline
		timeline.setTimeline(1);
		sketchPane.setBottom(timeline);
		BorderPane.setMargin(timeline, new Insets(10, 10, 10, 10));
		BorderPane.setAlignment(timeline, Pos.CENTER);
		
		// set the template picture and the explanationPane
		templatePane = new StackPane();
		Image imgTemplate = new Image("/template/template.jpg", 1000, 800, true, false);
		ImageView imgvwTemplate = new ImageView(imgTemplate);
		templatePane.getChildren().add(imgvwTemplate);
		
		StackPane explanationPane = new StackPane();
		explanationPane.setPrefSize(400, 400);
		txtExplanation.setText("Klik hier rechts op het onderdeel\ndat je wilt plaatsen ->");
		txtExplanation.setFont(Font.font("Verdana", 30));
		explanationPane.getChildren().add(txtExplanation);
		templatePane.getChildren().add(explanationPane);
		txtExplanation.setTranslateX(200);
		txtExplanation.setTranslateY(-300);
		
		sketchPane.setCenter(templatePane);
		BorderPane.setAlignment(templatePane, Pos.CENTER);
		
		// set the mission + costs + quality + submit button panel
		updateMissionPane();
		
		// set the part pictures panel
		updatePartPanel();
		
		// create the buildingParts array
		for(int i = 0; i < client.getMissions().length; i++) 
			for(int j = 0; j < client.getMissions()[i].getParts().length; j++) 
				buildingParts.add(client.getMissions()[i].getParts()[j]);
		
		// create the prices, values, budget and quality
		setPrices_Values_Budget_Quality();
		
		sketchPane.setBackground(background);
		window.setScene(sketchScene);
		
		partPanel.setOnMouseClicked(e -> updateMissionPane());
		
	}
	
	
	
	private void updatePartPanel() {
		
		// create a temporary partPanel
		GridPane tempPartPanel = new GridPane();
		tempPartPanel.setPrefWidth(250);
		
		tempPartPanel.setVgap(10);
		tempPartPanel.setAlignment(Pos.CENTER);
		
		for(int j = 0; j < client.getMissions().length; j++)
			if(!client.getMissions()[j].isCompleted()) {
				for(int i = 0; i < client.getMissions()[j].getParts().length; i++) {
					ImageView imgvwPart = new ImageView(client.getMissions()[j].getParts()[i].getPicNormal());
					tempPartPanel.add(imgvwPart, 0, i * 2);
					Text txtPart = new Text(client.getMissions()[j].getParts()[i].getName());
					txtPart.setFont(Font.font("Verdana", 18));
					tempPartPanel.add(txtPart, 0, i * 2 + 1);
					partClicked(imgvwPart, j, i);
				}
				break;
			}
		
		partPanel = tempPartPanel;
		tempPartPanel = null;
		sketchPane.setRight(partPanel);
		BorderPane.setMargin(partPanel, new Insets(20, 80, 20, 20));
	}
	
	
	
	private void updateMissionPane() {
		
		// create a temporary missionPane
		VBox tempMissionPane = new VBox(20);
		
		// set the missions
		Text txtMissions = new Text("Missies:");
		txtMissions.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		tempMissionPane.getChildren().add(txtMissions);
		
		boolean test = true;
		for(int i = 0; i < client.getMissions().length; i++) {
			Text txtMission = new Text(client.getMissions()[i].getName());
			txtMission.setFont(Font.font("Verdana", 25));
			
			if(test && !client.getMissions()[i].isCompleted()) {
				txtMission.setFill(Color.BLACK);
				test = false;
			} else {
				txtMission.setFill(Color.GREY);
			}
			
			if(client.getMissions()[i].isCompleted()) 
				txtMission.setStrikethrough(true);
			
			tempMissionPane.getChildren().add(txtMission);
		}
		
		if(missionsDone() && priceGood() && qualityGood())
			sketchDone = true;
		
		// set the costs
		costs = 0;
		for(int i = 0; i < client.getMissions().length; i++) 
			for(int j = 0; j < client.getMissions()[i].getParts().length; j++) 
				if(client.getMissions()[i].getParts()[j].getPlaced()) {
					costs += client.getMissions()[i].getParts()[j].getPrice();
				}
			
		Text txtTotalCosts = new Text("Totale kosten: ");
		txtTotalCosts.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		Text txtCosts = new Text(costs < 10? "0" + costs + " Hoppenbrouwers munten" : costs + " Hoppenbrouwers munten");
		txtCosts.setFont(Font.font("Verdana", 25));
		tempMissionPane.getChildren().addAll(txtTotalCosts, txtCosts);
		
		// set the quality
		quality = 0;
		for(int i = 0; i < client.getMissions().length; i++) 
			for(int j = 0; j < client.getMissions()[i].getParts().length; j++) 
				if(client.getMissions()[i].getParts()[j].getPlaced()) {
					quality += client.getMissions()[i].getParts()[j].getValue();
				}
		
		Text txtTotalQuality = new Text("Kwaliteit: ");
		txtTotalQuality.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		Text txtQuality = new Text(quality < 10? "0" + quality + " waardigheid" : quality + " waardigheid");
		txtQuality.setFont(Font.font("Verdana", 25));
		tempMissionPane.getChildren().addAll(txtTotalQuality, txtQuality);
		
		// set the submit button
		Button btSubmit = new Button("Voorleggen");
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		tempMissionPane.getChildren().add(btSubmit);
		
		btSubmit.setOnAction(e -> feedbackScreen());
		
		// set the tempMissionPane to the missionPane and reset the tempMissionPane
		missionPane = tempMissionPane;
		sketchPane.setLeft(missionPane);
		BorderPane.setMargin(missionPane, new Insets(150, 10, 10, 40));
		tempMissionPane = null;
	}
	
	
	
	private void partClicked(ImageView image, int mission, int part) {
		
		image.setOnMouseClicked(e -> {
			client.getMissions()[mission].getParts()[part].setPlaced(true);
			Image imgTemplate = client.getMissions()[mission].getParts()[part].getPicAbove();
			ImageView imgvwTemplate = new ImageView(imgTemplate);
			templatePane.getChildren().clear();
			templatePane.getChildren().add(imgvwTemplate);
			
			// if all the parts are placed, if so the mission is completed
			int count = 0;
			for(int i = 0; i < client.getMissions()[mission].getParts().length; i++) 
				if(client.getMissions()[mission].getParts()[i].getPlaced())
					count++;
			
			if(count == client.getMissions()[mission].getParts().length) {
				client.getMissions()[mission].setCompleted(true);
				updatePartPanel();
			}
				
			updateMissionPane();
		});
	}
	
	
	
	private void descriptionScreen() {
		
		BorderPane descriptionPane = new BorderPane();
		
		// show the picture of the client
		ImageView imgvwClient = new ImageView(new Image("/clients/feedback/" + client.getPicName() + "_Standaart.png", 700, 700, true, false));
		descriptionPane.setLeft(imgvwClient);
		BorderPane.setAlignment(imgvwClient, Pos.CENTER);
		BorderPane.setMargin(imgvwClient, new Insets(20, 20, 20, 80));
		
		// create the description text
		Text txtdescription = new Text(client.getDescription());
		txtdescription.setFont(Font.font("Verdana", 50));
		descriptionPane.setCenter(txtdescription);
		BorderPane.setAlignment(txtdescription, Pos.CENTER);
		
		// create the submit button and the timeline
		VBox bottomPane = new VBox(20);
		timeline.setTimeline(0);
		Button btSubmit = new Button("Verder");
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().addAll(btSubmit, timeline);
		descriptionPane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(0, 80, 0, 0));
		VBox.setMargin(timeline, new Insets(10, 10, 10, 10));
		
		descriptionPane.setBackground(background);
		Scene descriptionScene = new Scene(descriptionPane);
		window.setScene(descriptionScene);
		
		btSubmit.setOnAction(e -> {
			sketchScreen();
		});
	}
	
	
	
	private void feedbackScreen() {
		
		BorderPane feedbackPane = new BorderPane();
		
		// show the picture of the client
		ImageView imgvwClient; 
		if(sketchDone && priceGood() && qualityGood())
			imgvwClient = new ImageView(new Image("/clients/feedback/" + client.getPicName() + "_Feedback_Correct.png", 700, 700, true, false));
		else 
			imgvwClient = new ImageView(new Image("/clients/feedback/" + client.getPicName() + "_Feedback_Opnieuw.png", 700, 700, true, false));
		
		feedbackPane.setLeft(imgvwClient);
		BorderPane.setAlignment(imgvwClient, Pos.CENTER);
		BorderPane.setMargin(imgvwClient, new Insets(20, 20, 20, 80));
		
		// create the feedback text
		String feedback;
		
		if(!missionsDone()) 
			feedback = client.getFeedback(2);
		else if(!priceGood()) 
			feedback = client.getFeedback(0);
		else if(!qualityGood())
			feedback = client.getFeedback(1);
		else
			feedback = client.getFeedback(3);
		
		Text txtfeedback = new Text(feedback);
		txtfeedback.setFont(Font.font("Verdana", 50));
		feedbackPane.setCenter(txtfeedback);
		BorderPane.setAlignment(txtfeedback, Pos.CENTER);
		
		// create the submit button and the timeline
		VBox bottomPane = new VBox(20);
		timeline.setTimeline(2);
		Button btSubmit = new Button("Verder");
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().addAll(btSubmit, timeline);
		feedbackPane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(0, 80, 0, 0));
		VBox.setMargin(timeline, new Insets(10, 10, 10, 10));
		
		feedbackPane.setBackground(background);
		Scene feedbackScene = new Scene(feedbackPane);
		window.setScene(feedbackScene);
		
		btSubmit.setOnAction(e -> {
			if(sketchDone)
				schemeScreen();
			else
				sketchScreen();
		});
	}
	
	
	
	private void schemeScreen() {
		
		BorderPane schemePane = new BorderPane();
		
		// create the scrollPane with the scheme
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setStyle("-fx-font-size: 40px;");
		GridPane gridPane = new GridPane();
		gridPane.setHgap(30);
		gridPane.setVgap(30);
		GridPane.setMargin(gridPane, new Insets(20, 20, 20, 20));
		BorderPane.setMargin(scrollPane, new Insets(40, 40, 40, 40));
		
		for(int i = 0; i < client.getMissions().length; i++) {
			int count = 0;
			for(int j = 0; j < client.getMissions()[i].getParts().length; j++) {
				gridPane.add(new ImageView(client.getMissions()[i].getParts()[j].getPicNormal()), j + count, i);
				if(j < client.getMissions()[i].getParts().length - 1) {
					gridPane.add(new ImageView(new Image("/scheme/arrow.png")), j + 1 + count, i);
					count++;
				}
			}
		}
		
		scrollPane.setContent(gridPane);
		schemePane.setCenter(scrollPane);
		
		// create the timer
		GridPane timerPane = new GridPane();
		Text txt1 = new Text("Onthoud de volgorde van de onderdelen goed! Timer: ");
		txt1.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		txtTime.setFont(Font.font("Verdana", FontWeight.BOLD, 60));
		timerPane.add(txt1, 0, 0);
		timerPane.add(txtTime, 1, 0);
		timerPane.setAlignment(Pos.CENTER);
		
		animation.setCycleCount(Timeline.INDEFINITE);
		schemePane.setTop(timerPane);
		
		// create the submit button and the timeline
		VBox bottomPane = new VBox(20);
		timeline.setTimeline(3);
		Button btSubmit = new Button("Verder");
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().addAll(btSubmit, timeline);
		schemePane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(0, 80, 0, 0));
		VBox.setMargin(timeline, new Insets(10, 10, 10, 10));
		
		schemePane.setBackground(background);
		Scene schemeScene = new Scene(schemePane);
		window.setScene(schemeScene);
		
		animation.play();
		
		btSubmit.setOnAction(e -> {
			buildingScreen(); //TODO: change to schemeInteractScreen()
			schemeInteractEntered = true;
		});
		
	}
	
	private void timer() {
		int seconds = Integer.parseInt(txtTime.getText());
		txtTime.setText(String.valueOf((seconds > 0)? seconds - 1 : 0));
		if(seconds == 0) {
			animation.stop();
			if(!schemeInteractEntered)
				buildingScreen(); //TODO: change to schemeInteractScreen()
		}
	}
	
	
	
	private void schemeInteractScreen() {
		
		BorderPane schemeInteractPane = new BorderPane();
		
		// create the submit button and the timeline
		VBox bottomPane = new VBox(20);
		timeline.setTimeline(3);
		Button btSubmit = new Button("Verder");
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().addAll(btSubmit, timeline);
		schemeInteractPane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(0, 80, 0, 0));
		VBox.setMargin(timeline, new Insets(10, 10, 10, 10));
		
		schemeInteractPane.setBackground(background);
		Scene schemeInteractScene = new Scene(schemeInteractPane);
		window.setScene(schemeInteractScene);
		
		btSubmit.setOnAction(e -> buildingScreen());
		
	}
	
	
	
	private void buildingScreen() {
		
		com.closeAll();
		
		// create the middle area with all the boxes
		GridPane boxPane = new GridPane();
		boxPane.setAlignment(Pos.CENTER);
		GridPane.setMargin(boxPane, new Insets(40, 40, 40, 40));
		boxPane.setHgap(30);
		boxPane.setVgap(30);
		for(int i = 0; i < boxes.length; i++) {
			boxes[i] = new Rectangle(200, 200);
			boxes[i].setFill(Color.WHITE);
			boxes[i].setStroke(Color.BLACK);
			boxPane.add(boxes[i], i % 5, (int)(i / 5));
		}
		buildingPane.setCenter(boxPane);
		BorderPane.setAlignment(boxPane, Pos.CENTER);
		
		// create the part pane: who's turn is it?
		available = new boolean[buildingParts.size()];
		for(int i = 0; i < available.length; i++)
			available[i] = true;
		
		openFlapClicked();
		btOpenFlap.setOnAction(e -> openFlapClicked());
		
		// create the submit button and the timeline
		timeline.setTimeline(4);
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().add(timeline);
		buildingPane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(0, 80, 0, 0));
		VBox.setMargin(timeline, new Insets(10, 10, 10, 10));
		
		buildingPane.setBackground(background);
		Scene buildingScene = new Scene(buildingPane);
		window.setScene(buildingScene);
	}
	
	
	
	private void openFlapClicked() {
		
		GridPane tempGridPaneRight = new GridPane();
		
		tempGridPaneRight.setVgap(30);
		tempGridPaneRight.setAlignment(Pos.BASELINE_CENTER);
		Text txtBuildWith = new Text("Bouw met: ");
		txtBuildWith.setFont(Font.font("Verdana", FontWeight.BOLD, 50));
		tempGridPaneRight.add(txtBuildWith, 0, 0);
		
		for(int i = 0; i < available.length; i++)
			if(available[i]) {
				if(i == 0) {
					com.open(buildingParts.get(i).getFlap());
					boxes[buildingParts.get(i).getFlap()].setFill(Color.AQUAMARINE);
				}else if(buildingParts.get(i - 1).getFlap() != buildingParts.get(i).getFlap()) {
					com.close(buildingParts.get(i - 1).getFlap());
					boxes[buildingParts.get(i - 1).getFlap()].setFill(Color.WHITE);
					com.open(buildingParts.get(i).getFlap());
					boxes[buildingParts.get(i).getFlap()].setFill(Color.AQUAMARINE);
				}
				tempGridPaneRight.add(new ImageView(buildingParts.get(i).getPicNormal()), 0, 1);
				Text txtPart = new Text(buildingParts.get(i).getName());
				txtPart.setFont(Font.font("Verdana", 20));
				tempGridPaneRight.add(txtPart, 0, 2);
				available[i] = false;
				break;
			}
		
		if(available[available.length - 1]) {
			btOpenFlap.setFont(Font.font("Verdana", 30));
			tempGridPaneRight.add(btOpenFlap, 0, 3);
		}else {
			bottomPane.getChildren().removeAll(btSubmit, timeline);
			bottomPane.getChildren().addAll(btSubmit, timeline);
			buildingPane.setBottom(bottomPane);
			bottomPane.setAlignment(Pos.CENTER_RIGHT);
			VBox.setMargin(btSubmit, new Insets(0, 80, 0, 0));
			VBox.setMargin(timeline, new Insets(10, 10, 10, 10));
			btSubmit.setOnAction(e -> lastFeedbackScreen());
		}
		
		gridPaneRight = tempGridPaneRight;
		buildingPane.setRight(gridPaneRight);
		BorderPane.setMargin(gridPaneRight, new Insets(150, 30, 30, 30));
		tempGridPaneRight = null;
	}
	
	
	
	private void lastFeedbackScreen() {
		
		com.close(buildingParts.get(buildingParts.size() - 1).getFlap());
		BorderPane lastFeedbackPane = new BorderPane();
		
		ImageView imgvwClient = new ImageView(new Image("/clients/feedback/" + client.getPicName() + "_Feedback_Correct.png", 700, 700, true, false));
		lastFeedbackPane.setLeft(imgvwClient);
		BorderPane.setAlignment(imgvwClient, Pos.CENTER);
		BorderPane.setMargin(imgvwClient, new Insets(20, 20, 20, 80));
		
		// create the feedback text
		String feedback = "Dankjewel, je bent een held!";
		
		Text txtfeedback = new Text(feedback);
		txtfeedback.setFont(Font.font("Verdana", 50));
		lastFeedbackPane.setCenter(txtfeedback);
		BorderPane.setAlignment(txtfeedback, Pos.CENTER);
		
		// create the submit button and the timeline
		VBox bottomPane = new VBox(20);
		Button btSubmit = new Button("Verder");
		btSubmit.setOnAction(e -> endScreen());
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().add(btSubmit);
		lastFeedbackPane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(30, 30, 30, 30));
		
		lastFeedbackPane.setBackground(background);
		Scene lastFeedbackScene = new Scene(lastFeedbackPane);
		window.setScene(lastFeedbackScene);
	}
	
	
	
	private void endScreen() {
		
		com.openAll();
		BorderPane endPane = new BorderPane();
		
		ImageView imgvwClient = new ImageView(new Image("/clients/feedback/" + client.getPicName() + "_Feedback_Correct.png", 700, 700, true, false));
		endPane.setLeft(imgvwClient);
		BorderPane.setAlignment(imgvwClient, Pos.CENTER);
		BorderPane.setMargin(imgvwClient, new Insets(20, 20, 20, 80));
		
		// create the feedback text
		String feedback = "Nu nog opruimen :)";
		
		Text txtfeedback = new Text(feedback);
		txtfeedback.setFont(Font.font("Verdana", 50));
		endPane.setCenter(txtfeedback);
		BorderPane.setAlignment(txtfeedback, Pos.CENTER);
		
		// create the submit button and the timeline
		VBox bottomPane = new VBox(20);
		Button btSubmit = new Button("Verder");
		btSubmit.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
		bottomPane.getChildren().add(btSubmit);
		endPane.setBottom(bottomPane);
		bottomPane.setAlignment(Pos.CENTER_RIGHT);
		VBox.setMargin(btSubmit, new Insets(30, 30, 30, 30));
		
		btSubmit.setOnAction(e -> {
			beginScreen();
			resetVariables();
			com.closeAll();
		});
		
		endPane.setBackground(background);
		Scene endScene = new Scene(endPane);
		window.setScene(endScene);
	}
	
	
	
	private void resetVariables() {
		
		for(int i = 0; i < client.getMissions().length; i++)
			client.getMissions()[i].setCompleted(false);
		
		for(int i = 0; i < buildingParts.size(); i++) {
			buildingParts.get(i).setPlaced(false);
			buildingParts.remove(i);
		}
		
		client = null;
		sketchDone = false;
		
		sketchPane = new BorderPane();
		sketchScene = new Scene(sketchPane);
		missionPane = new VBox(20);
		partPanel = new GridPane();
		
		costs = 0; 
		quality = 0;
		
		txtTime.setText("15");
		schemeInteractEntered = false;
		
		buildingPane = new BorderPane();
		bottomPane = new VBox(20);
		gridPaneRight = new GridPane(); // used for the picture of the part while building
		boxes = new Rectangle[10];
		buildingParts = new ArrayList<>();
		btOpenFlap = new Button("Volgende klep");
		btSubmit = new Button("Verder");
	}
	
	
	
	private void setPrices_Values_Budget_Quality() {
		
		for(int i = 0; i < buildingParts.size(); i++) {
			Integer[] pricearray = {(int)(Math.random() * 3 + 1), (int)(Math.random() * 3 + 1), (int)(Math.random() * 3 + 1)};
			prices.add(pricearray);
			Integer[] qualityarray = {(int)(Math.random() * 3 + 1), (int)(Math.random() * 3 + 1), (int)(Math.random() * 3 + 1)};
			qualities.add(qualityarray);
		}
		
	}
	
	private boolean missionsDone() {
		for(int i = 0; i < client.getMissions().length; i++) 
			if(!client.getMissions()[i].isCompleted())
				return false;
		
		return true;
	}

	private boolean priceGood() {
		return client.getBudget() >= costs;
	}
	
	private boolean qualityGood() {
		return client.getQuality() <= quality;
	}
}

































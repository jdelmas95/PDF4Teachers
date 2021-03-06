package fr.themsou.panel;

import fr.themsou.panel.MainScreen.MainScreen;
import fr.themsou.utils.TR;
import fr.themsou.windows.MainWindow;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

@SuppressWarnings("serial")
public class FooterBar extends AnchorPane {

	public Text leftInfo = new Text("");
	public Text middleInfo = new Text("");
	public Text rightInfo = new Text("");

	public FooterBar(){

		setup();
	}

	public void repaint(){

		leftInfo.textProperty().bind(Bindings.createStringBinding(() -> TR.tr("Zoom") + " : " + (int) (MainWindow.mainScreen.pane.getScaleX()*100) + "% (Ctrl+Scroll)", MainWindow.mainScreen.pane.scaleXProperty()));

		switch (MainWindow.leftBar.getSelectionModel().getSelectedIndex()){
			case 0:
				middleInfo.setText(TR.tr("Mode Fichiers")); break;
			case 1:
				middleInfo.setText(TR.tr("Mode Texte")); break;
			case 2:
				middleInfo.setText(TR.tr("Mode Notes")); break;
			case 3:
				middleInfo.setText(TR.tr("Mode Dessin")); break;
		}

		if(MainWindow.mainScreen.getStatus() == MainScreen.Status.OPEN){

			if(MainWindow.mainScreen.document.getCurrentPage() == -1){
				rightInfo.setText(MainWindow.mainScreen.document.getFileName() + " - " + "?/" + MainWindow.mainScreen.document.totalPages);
			}else{
				rightInfo.setText(MainWindow.mainScreen.document.getFileName() + " - " + (MainWindow.mainScreen.document.getCurrentPage()+1) + "/" + MainWindow.mainScreen.document.totalPages);
			}
		}else{
			rightInfo.setText(TR.tr("Aucun fichier ouvert"));
		}
	}

	public void setup(){

		setPrefHeight(20);

		setBackground(new Background(new BackgroundFill(Color.rgb(43, 43, 43), CornerRadii.EMPTY, Insets.EMPTY)));

		AnchorPane.setLeftAnchor(leftInfo, 10.0);
		AnchorPane.setRightAnchor(rightInfo, 10.0);

		AnchorPane.setBottomAnchor(leftInfo, 3.0);
		AnchorPane.setBottomAnchor(middleInfo, 3.0);
		AnchorPane.setBottomAnchor(rightInfo, 3.0);

		leftInfo.setFill(Color.WHITE);
		middleInfo.setFill(Color.WHITE);
		rightInfo.setFill(Color.WHITE);
		middleInfo.translateXProperty().bind(widthProperty().divide(2).subtract(leftInfo.getLayoutBounds().getWidth()));

		leftInfo.setFont(new Font("FreeSans", 15));
		middleInfo.setFont(new Font("FreeSans", 15));
		rightInfo.setFont(new Font("FreeSans", 15));

		getChildren().add(leftInfo);
		getChildren().add(middleInfo);
		getChildren().add(rightInfo);

		MainWindow.leftBar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> repaint());

	}
}

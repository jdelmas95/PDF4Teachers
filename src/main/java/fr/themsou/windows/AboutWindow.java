package fr.themsou.windows;

import fr.themsou.main.Main;
import fr.themsou.utils.TR;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class AboutWindow extends Stage {

    public AboutWindow(){

        VBox root = new VBox();
        Scene scene = new Scene(root, 400, 720);

        initOwner(Main.window);
        initModality(Modality.WINDOW_MODAL);
        getIcons().add(new Image(getClass().getResource("/logo.png")+""));
        setWidth(400);
        setHeight(670);
        setTitle(TR.tr("PDF4Teachers - À Propos"));
        setResizable(false);
        setScene(scene);
        setOnCloseRequest(e -> close());
        new JMetro(root, Style.LIGHT);

        setupUi(root);

        show();
    }

    private void setupUi(VBox root){

        VBox vBox = new VBox();

            ImageView logo = new ImageView(getClass().getResource("/logo.png")+"");
            logo.setFitWidth(200);
            logo.setPreserveRatio(true);

            Label name = new Label("PDF4Teachers");
            name.setFont(new Font(23));
            name.setAlignment(Pos.CENTER);

            Label version = new Label(TR.tr("Version") + " " + Main.VERSION);
            version.setFont(new Font(15));
            version.setAlignment(Pos.CENTER);

            Button newVersion = null;
            if(UpdateWindow.newVersion){
                newVersion = new Button(TR.tr("Une nouvelle version est disponible !"));
                newVersion.setAlignment(Pos.CENTER);
                newVersion.setStyle("-fx-background-color: #ba6800;");

                newVersion.setOnAction(event -> {
                    new UpdateWindow();
                });
            }

            HBox devInfo = new HBox();
                Label dev = new Label(TR.tr("Développeur :") + " ");
                dev.setFont(new Font(17));

                Hyperlink devName = new Hyperlink("Clément G.");
                devName.setFont(new Font(17));
                devName.setOnAction(t -> Main.hostServices.showDocument("https://github.com/themsou"));
            devInfo.getChildren().addAll(dev, devName);
            devInfo.setAlignment(Pos.CENTER);

            HBox consInfo = new HBox();
                Label cons = new Label(TR.tr("Concepteur :") + " ");
                cons.setFont(new Font(17));

                Hyperlink consName = new Hyperlink("Vincent G.");
                consName.setFont(new Font(17));
                consName.setOnAction(t -> Main.hostServices.showDocument("https://github.com/grensv"));
                consInfo.getChildren().addAll(cons, consName);
            consInfo.setAlignment(Pos.CENTER);

            HBox transInfo = new HBox();
                if(!TR.tr("Traducteur : <Votre nom>").equals("Traducteur : <Votre nom>")){
                    Label trans = new Label(TR.tr("Traducteur : <Votre nom>"));
                    trans.setFont(new Font(17));
                    transInfo.getChildren().add(trans);
                }
            transInfo.setAlignment(Pos.CENTER);

            HBox gitInfo = new HBox();
                Label git = new Label(TR.tr("Projet GitHub :") + " ");
                git.setFont(new Font(17));

                Hyperlink gitName = new Hyperlink("themsou/PDF4Teachers");
                gitName.setFont(new Font(17));
                gitName.setOnAction((ActionEvent t) -> Main.hostServices.showDocument("https://github.com/themsou/PDF4Teachers"));
            gitInfo.getChildren().addAll(git, gitName);
            gitInfo.setAlignment(Pos.CENTER);

            VBox apiInfo = new VBox();
                Label api = new Label(TR.tr("Dépendances :"));

                Hyperlink javaFx = new Hyperlink("Java FX 14");
                javaFx.setOnAction((ActionEvent t) -> {
                    Main.hostServices.showDocument("https://openjfx.io/");
                });

                Hyperlink pdfBox = new Hyperlink("Apache PDFBox 2.0.17");
                pdfBox.setOnAction((ActionEvent t) -> {
                    Main.hostServices.showDocument("https://pdfbox.apache.org/");
                });

                Hyperlink jMetro = new Hyperlink("JMetro 11.5.10");
                jMetro.setOnAction((ActionEvent t) -> Main.hostServices.showDocument("https://pixelduke.com/java-javafx-theme-jmetro/"));

                Hyperlink json = new Hyperlink("Jackson Streaming API 2.10.3");
                json.setOnAction((ActionEvent t) -> Main.hostServices.showDocument("https://github.com/FasterXML/jackson-core"));

            apiInfo.getChildren().addAll(api, javaFx, pdfBox, jMetro, json);
            apiInfo.setAlignment(Pos.CENTER);

            HBox issueInfo = new HBox();
                Hyperlink issueName = new Hyperlink(TR.tr("Demander de l'aide ou signaler un Bug"));
                issueName.setFont(new Font(17));
                issueName.setOnAction((ActionEvent t) -> Main.hostServices.showDocument("https://github.com/themsou/PDF4Teachers/issues/new"));
            issueInfo.getChildren().addAll(issueName);
            issueInfo.setAlignment(Pos.CENTER);

        vBox.getChildren().addAll(logo, name, version);
        if(newVersion != null) vBox.getChildren().add(newVersion);
        vBox.getChildren().addAll(devInfo, consInfo, transInfo, gitInfo, issueInfo, apiInfo);
        vBox.setAlignment(Pos.CENTER);

        VBox.setMargin(logo, new Insets(20, 0, 0, 0));
        VBox.setMargin(name, new Insets(5, 0, 0, 0));
        VBox.setMargin(version, new Insets(0, 0, 7, 0));
        VBox.setMargin(devInfo, new Insets(7, 0, 0, 0));
        VBox.setMargin(issueInfo, new Insets(10, 0, 15, 0));

        root.getChildren().addAll(vBox);
    }
}

package fr.themsou.document.render.export;

import fr.themsou.document.editions.Edition;
import fr.themsou.document.editions.elements.Element;
import fr.themsou.document.editions.elements.TextElement;
import fr.themsou.utils.Builders;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class ExportRenderer {

    public int exportFile(File file, String directory, String prefix, String suffix, String replace, String by, String customName,
                          boolean erase, boolean mkdirs, boolean textElements, boolean notesElements, boolean drawElements) throws Exception {

        File editFile = Edition.getEditFile(file);
        editFile.createNewFile();

        PDDocument doc = PDDocument.load(file);
        PDPage page = doc.getPage(0);

        PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, false);

        PDRectangle pageSize = page.getBleedBox();


        for(Element element : Edition.simpleLoad(editFile)){

            if(element instanceof TextElement){
                if(!textElements) continue;

                TextElement txtElement = (TextElement) element;

                contentStream.beginText();
                contentStream.newLineAtOffset((float) (txtElement.getRealX() / 500.0 * pageSize.getWidth()),
                                              (float) (pageSize.getHeight() - txtElement.getRealY() / 800.0 * pageSize.getHeight()) );

                Color color = (Color) txtElement.getFill();
                contentStream.setNonStrokingColor(new java.awt.Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity()));

                boolean bold = false;
                if(TextElement.getFontWeight(txtElement.getFont()) == FontWeight.BOLD) bold = true;
                boolean italic = false;
                if(TextElement.getFontPosture(txtElement.getFont()) == FontPosture.ITALIC) italic = true;

                InputStream fontFile = getClass().getResourceAsStream("/fonts/" + TextElement.getFontPath(txtElement.getFont().getFamily(), italic, bold));
                contentStream.setFont(PDTrueTypeFont.loadTTF(doc, fontFile), (float) txtElement.getRealFont().getSize());

                contentStream.showText(txtElement.getText());

                contentStream.endText();

            }/*else if(element instanceof NoteElement){
                if(!notesElements) continue;

            }else if(element instanceof DrawElement){
                if(!drawElements) continue;

            }*/

        }

        contentStream.close();

        if(!new File(directory).exists()){
            if(mkdirs){
                new File(directory).mkdirs();
            }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                new JMetro(alert.getDialogPane(), Style.LIGHT);
                alert.setTitle("Dossier introuvable");
                alert.setHeaderText("Le dossier d'exportation n'est pas existant.");
                alert.setContentText("Voulez-vous en le créer ou modifier la destination ?");
                ButtonType yesButton = new ButtonType("Créer le dossier", ButtonBar.ButtonData.YES);
                ButtonType cancelButton = new ButtonType("Modifier la destination", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(yesButton, cancelButton);
                Builders.secureAlert(alert);
                Optional<ButtonType> option = alert.showAndWait();
                if(option.get() == yesButton){
                    new File(directory).mkdirs();
                }else{
                    return 0;
                }
            }
        }

        String uri = directory + "/" + customName;
        if(customName.isEmpty()){
            uri = directory + "/" + prefix + file.getName().substring(0, file.getName().length() - 4).replaceAll(replace, by) + suffix + ".pdf";
        }

        if(new File(uri).exists() && !erase){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            new JMetro(alert.getDialogPane(), Style.LIGHT);
            alert.setTitle("Fichier existant");
            alert.setHeaderText("Le fichier de destination \"" + uri.replace(directory + "/", "") + "\" existe déjà");
            alert.setContentText("Voulez-vous l'écraser ?");
            ButtonType yesButton = new ButtonType("Écraser", ButtonBar.ButtonData.YES);
            ButtonType yesAlwaysButton = new ButtonType("Toujours écraser", ButtonBar.ButtonData.YES);
            ButtonType renameButton = new ButtonType("Renomer", ButtonBar.ButtonData.OTHER);
            ButtonType cancelButton = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType cancelAllButton = new ButtonType("Tout Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(yesButton, yesAlwaysButton, renameButton, cancelButton, cancelAllButton);
            Builders.secureAlert(alert);
            Optional<ButtonType> option = alert.showAndWait();
            if(option.get() == cancelAllButton){
                return 0;
            }else if(option.get() == cancelButton){
                return 1;
            }else if(option.get() == yesAlwaysButton){
                mkdirs = true;
            }else if(option.get() == renameButton){
                int i = 1;
                String tmpUri = uri;
                while(new File(tmpUri).exists()){
                    tmpUri = uri.substring(0, uri.length() - 4) + " (" + i + ").pdf";
                    i++;
                }
                uri = tmpUri;
            }
        }

        if(!new File(directory).exists()){
            if(mkdirs){
                new File(directory).mkdirs();
            }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                new JMetro(alert.getDialogPane(), Style.LIGHT);
                alert.setTitle("Dossier introuvable");
                alert.setHeaderText("Le dossier d'exportation n'est pas existant.");
                alert.setContentText("Voulez-vous en le créer ou modifier la destination ?");
                ButtonType yesButton = new ButtonType("Créer le dossier", ButtonBar.ButtonData.YES);
                ButtonType cancelButton = new ButtonType("Modifier la destination", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(yesButton, cancelButton);
                Builders.secureAlert(alert);
                Optional<ButtonType> option = alert.showAndWait();
                if(option.get() == yesButton){
                    new File(directory).mkdirs();
                }else{
                    return 0;
                }
            }
        }
        System.out.println("save with uri : " + uri);
        doc.save(uri);
        doc.close();
        return 1;
    }
}

package fr.themsou.main;
import fr.themsou.utils.StringUtils;
import fr.themsou.windows.LanguageWindow;
import fr.themsou.windows.MainWindow;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import java.io.*;
import java.util.ArrayList;

public class Settings {

    private String settingsVersion = "";

    // PREFS
    private BooleanProperty restoreLastSession = new SimpleBooleanProperty();
    private BooleanProperty checkUpdates = new SimpleBooleanProperty();
    private int defaultZoom;
    private BooleanProperty zoomAnimations = new SimpleBooleanProperty();

    private BooleanProperty autoSave = new SimpleBooleanProperty();
    private int regularSaving;

    private BooleanProperty removeElementInPreviousListWhenAddingToFavorites = new SimpleBooleanProperty();
    private BooleanProperty showOnlyStartInTextsList = new SimpleBooleanProperty();
    private BooleanProperty smallFontInTextsList = new SimpleBooleanProperty();

    private StringProperty language = new SimpleStringProperty("");

    // OTHER
    private ArrayList<File> openedFiles;
    private File openedFile;

    public Settings(){

        restoreLastSession.set(true); openedFiles = new ArrayList<>(); openedFile = null;
        checkUpdates.set(true);
        defaultZoom = 130;
        if(System.getProperty("os.name").equals("Mac OS X")) zoomAnimations.set(false);
        else zoomAnimations.set(true);

        autoSave.set(false);
        regularSaving = -1;

        removeElementInPreviousListWhenAddingToFavorites.set(true);
        showOnlyStartInTextsList.set(true);
        smallFontInTextsList.set(false);

        loadSettings();

        restoreLastSessionProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
        });
        checkUpdatesProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
        });
        zoomAnimationsProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
        });

        /////

        autoSavingProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
        });

        /////

        removeElementInPreviousListWhenAddingToFavoritesProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
        });
        showOnlyStartInTextsListProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
            if(MainWindow.lbTextTab != null) MainWindow.lbTextTab.updateListsGraphic();
        });
        smallFontInTextsListProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) -> {
            saveSettings();
            if(MainWindow.lbTextTab != null) MainWindow.lbTextTab.updateListsGraphic();
        });

        /////

        languageProperty().addListener((observable, oldValue, newValue) -> {
            saveSettings();
        });
    }
    public void loadSettings(){

        new Thread(() -> {

            new File(Main.dataFolder).mkdirs();
            File settings = new File(Main.dataFolder + "settings.yml");
            try{

                ArrayList<File> lastFiles = new ArrayList<>();
                File lastFile = null;

                if(settings.createNewFile()){ //file was created
                    saveSettings();
                }else{ // file already exist
                    BufferedReader reader = new BufferedReader(new FileReader(settings));

                    String line;
                    while((line = reader.readLine()) != null) {

                        String value = StringUtils.removeBefore(line, ": ");

                        switch(line.split(": ")[0]){

                            case "version":
                                settingsVersion = value;
                                break;
                            case "language":
                                language.set(value);
                            break;
                            case "restoreLastSession":
                                try{
                                    restoreLastSession.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;
                            case "openedFiles":
                                try{
                                    for(String filePath : value.split(";/;")){
                                        if(new File(filePath).exists()){
                                            lastFiles.add(new File(filePath));
                                        }
                                    }

                                }catch(Exception ignored){}
                                break;
                            case "openedFile":
                                try{
                                    if(new File(value).exists()) lastFile = new File(value);
                                }catch(Exception ignored){}
                            break;
                            case "checkUpdates":
                                try{
                                    checkUpdates.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;
                            case "defaultZoom":
                                try{
                                    defaultZoom = Integer.parseInt(value);
                                }catch(Exception ignored){}
                            break;
                            case "zoomAnimations":
                                try{
                                    zoomAnimations.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;

                                /////

                            case "autoSave":
                                try{
                                    autoSave.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;
                            case "regularSaving":
                                try{
                                    regularSaving = Integer.parseInt(value);
                                }catch(Exception ignored){}
                            break;

                                /////

                            case "removeElementInPreviousListWhenAddingToFavorites":
                                try{
                                    removeElementInPreviousListWhenAddingToFavorites.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;
                            case "showOnlyStartInTextsList":
                                try{
                                    showOnlyStartInTextsList.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;
                            case "smallFontInTextsList":
                                try{
                                    smallFontInTextsList.set(Boolean.parseBoolean(value));
                                }catch(Exception ignored){}
                            break;
                        }
                    }
                    reader.close();

                    if(restoreLastSession.get()){
                        openedFiles = lastFiles;
                        openedFile = lastFile;
                    }
                }
            }catch (IOException e){ e.printStackTrace(); }

        }, "settingsLoader").start();
    }

    public void saveSettings(){

        new Thread(() -> {

            new File(Main.dataFolder).mkdirs();
            File settings = new File(Main.dataFolder + "settings.yml");

            try{
                settings.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(settings, false));

                /////

                writer.write("version: " + Main.VERSION);

                writer.newLine();
                writer.write("language: " + language.get());

                writer.newLine();
                writer.write("restoreLastSession: " + restoreLastSession.get());

                writer.newLine();
                writer.write("openedFiles: ");
                for(File file : openedFiles) writer.write(file.getAbsolutePath() + ";/;");

                writer.newLine();
                if(openedFile != null) writer.write("openedFile: " + openedFile.getAbsolutePath());
                else writer.write("openedFile: ");

                writer.newLine();
                writer.write("checkUpdates: " + checkUpdates.get());

                writer.newLine();
                writer.write("defaultZoom: " + defaultZoom);

                writer.newLine();
                writer.write("zoomAnimations: " + zoomAnimations.get());

                /////

                writer.newLine();
                writer.write("autoSave: " + autoSave.get());

                writer.newLine();
                writer.write("regularSaving: " + regularSaving);

                /////

                writer.newLine();
                writer.write("removeElementInPreviousListWhenAddingToFavorites: " + removeElementInPreviousListWhenAddingToFavorites.get());

                writer.newLine();
                writer.write("showOnlyStartInTextsList: " + showOnlyStartInTextsList.get());

                writer.newLine();
                writer.write("smallFontInTextsList: " + smallFontInTextsList.get());

                /////


                writer.flush();
                writer.close();

            }catch(IOException e){
                e.printStackTrace();
            }
        }, "settingsSaver").start();

    }

    public String getSettingsVersion() {
        return settingsVersion;
    }

    public boolean isCheckUpdates() {
        return checkUpdates.get();
    }
    public BooleanProperty checkUpdatesProperty() {
        return checkUpdates;
    }
    public void setCheckUpdates(boolean checkUpdates) {
        this.checkUpdates.set(checkUpdates);
        saveSettings();
    }

    public boolean isZoomAnimations() {
        return zoomAnimations.get();
    }
    public BooleanProperty zoomAnimationsProperty() {
        return zoomAnimations;
    }
    public void setZoomAnimations(boolean zoomAnimations) {
        this.zoomAnimations.set(zoomAnimations);
        saveSettings();
    }

    public int getDefaultZoom(){
        return defaultZoom;
    }
    public boolean isAutoSave(){
        return autoSave.get();
    }
    public void setDefaultZoom(int zoom){
        this.defaultZoom = zoom;
        saveSettings();
    }

    public void setAutoSaving(boolean autoSave){
        this.autoSave.set(autoSave);
        saveSettings();
    }
    public BooleanProperty autoSavingProperty(){
        return this.autoSave;
    }

    public int getRegularSaving() {
        return regularSaving;
    }
    public void setRegularSaving(int regularSaving) {
        this.regularSaving = regularSaving;
        saveSettings();
    }

    public boolean isRestoreLastSession() {
        return restoreLastSession.get();
    }
    public BooleanProperty restoreLastSessionProperty() {
        return restoreLastSession;
    }
    public void setRestoreLastSession(boolean restoreLastSession) {
        this.restoreLastSession.set(restoreLastSession);
        saveSettings();
    }

    public ArrayList<File> getOpenedFiles() {
        return openedFiles;
    }
    public void setOpenedFiles(ArrayList<File> openedFiles) {
        this.openedFiles = openedFiles;
        saveSettings();
    }
    public void addOpenedFiles(File file) {
        openedFiles.add(file);
        saveSettings();
    }
    public void removeOpenedFiles(File file) {
        openedFiles.remove(file);
        saveSettings();
    }
    public File getOpenedFile() {
        return openedFile;
    }
    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
        saveSettings();
    }

    public boolean isRemoveElementInPreviousListWhenAddingToFavorites() {
        return removeElementInPreviousListWhenAddingToFavorites.get();
    }
    public BooleanProperty removeElementInPreviousListWhenAddingToFavoritesProperty() {
        return removeElementInPreviousListWhenAddingToFavorites;
    }
    public void setRemoveElementInPreviousListWhenAddingToFavorites(boolean removeElementInPreviousListWhenAddingToFavorites) {
        this.removeElementInPreviousListWhenAddingToFavorites.set(removeElementInPreviousListWhenAddingToFavorites);
        saveSettings();
    }

    public boolean isShowOnlyStartInTextsList() {
        return showOnlyStartInTextsList.get();
    }
    public BooleanProperty showOnlyStartInTextsListProperty() {
        return showOnlyStartInTextsList;
    }
    public void setShowOnlyStartInTextsList(boolean showOnlyStartInTextsList) {
        this.showOnlyStartInTextsList.set(showOnlyStartInTextsList);
        saveSettings();
    }

    public boolean isSmallFontInTextsList() {
        return smallFontInTextsList.get();
    }
    public BooleanProperty smallFontInTextsListProperty() {
        return smallFontInTextsList;
    }
    public void setSmallFontInTextsList(boolean smallFontInTextsList) {
        this.smallFontInTextsList.set(smallFontInTextsList);
        saveSettings();
    }

    public String getLanguage() {
        return language.get();
    }
    public StringProperty languageProperty() {
        return language;
    }
    public void setLanguage(String language) {
        this.language.set(language);
        saveSettings();
    }
}

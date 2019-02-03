package database.DataBase.src.main;

/**
 * Enum containing the tables of our database.
 * Each enum's value correspond to one table name.
 */
public enum Table {
    GROUNDTRUTH ("table_groundTruth"),
    USERANNOTATION ("table_userAnnotation"),
    TRANSCRIPTION ("table_transcription"),
    RECOGNIZER ("table_recognizer"),
    EXEMPLE ("table_exemple"),
    OPERATINGMODE("table_operatingMode"),
    DOCUMENT ("table_document"),
    TYPE ("table_type"),
    AUTHOR ("table_author");

    private String table;

    Table(String table) {this.table = table;}

    public String toString() {return this.table;}
}
package database.DataBase.src.main;

import org.sqlite.SQLiteException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DataBase {

	private Connection c;
	private String folderPathToStorePictures;
	private String dataBaseName;

	/**
	 * DataBase constructor.
	 * @param dataBaseName the name of the data base you want to create.
	 * @param folderPath the path to the folder where the imagettes will be stored.
	 */
	public DataBase(String dataBaseName, String folderPath) {
		super();
		this.c = null;
		this.dataBaseName = dataBaseName;
		this.folderPathToStorePictures = folderPath + File.separator;
		createFolder(folderPathToStorePictures);

		//Database and tables creation
		createBase();
		createTable(Table.AUTHOR, "id INTEGER PRIMARY KEY NOT NULL , name VARCHAR(256)");
		createTable(Table.TYPE, "id INTEGER PRIMARY KEY NOT NULL , name VARCHAR(256)");
		createTable(Table.OPERATINGMODE, "id INTEGER PRIMARY KEY NOT NULL , name VARCHAR(256)");
		createTable(Table.RECOGNIZER, "id INTEGER PRIMARY KEY NOT NULL , name VARCHAR(256)");
		createTable(Table.DOCUMENT, "id INTEGER PRIMARY KEY NOT NULL , name VARCHAR(256), idAuthor INTEGER, idType INTEGER, idMode INTEGER, dateAdd DATETIME, FOREIGN KEY(idAuthor) REFERENCES " + Table.AUTHOR + "(id), FOREIGN KEY(idType) REFERENCES " + Table.TYPE + "(id), FOREIGN KEY(idMode) REFERENCES " + Table.OPERATINGMODE + "(id)");
		createTable(Table.EXEMPLE, "id INTEGER PRIMARY KEY NOT NULL ,idOwner INTEGER, picture VARCHAR(256), FOREIGN KEY(idOwner) REFERENCES " + Table.DOCUMENT + "(id)");
		createTable(Table.GROUNDTRUTH, "id INTEGER PRIMARY KEY NOT NULL , transcript VARCHAR(1024), idExample INTEGER, FOREIGN KEY(idExample) REFERENCES " + Table.EXEMPLE + "(id)");
		createTable(Table.USERANNOTATION, "id INTEGER PRIMARY KEY NOT NULL , transcript VARCHAR(256), idExample INTEGER, creationDate DATETIME, FOREIGN KEY(idExample) REFERENCES " + Table.EXEMPLE + "(id)");
		createTable(Table.TRANSCRIPTION, "id INTEGER PRIMARY KEY NOT NULL , transcript VARCHAR(256), idExample INTEGER, idRecognizer INTEGER, creationDate DATETIME, FOREIGN KEY(idExample) REFERENCES " + Table.EXEMPLE + "(id), FOREIGN KEY(idRecognizer) REFERENCES " + Table.RECOGNIZER + "(id)");
	}


	//-------------------------------------- Connection/Disconnection methods ------------------------------------------
	/**
	 * Open a connection to the data base.
	 */
	public void openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			this.c = DriverManager.getConnection("jdbc:sqlite:" + this.dataBaseName + ".db");
			this.c.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Close the connection from the data base.
	 */
	public void closeConnection() {
		try {
			c.commit();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}
	//------------------------------------------------------------------------------------------------------------------


	//---------------------------------------- Insertion methods -------------------------------------------------------
	/**
	 * Insert an Example into the database.
	 * @param imagette the picture to add.
	 * @return true if success, false otherwise.
	 */
	public boolean insertImagette(Imagette imagette) {
		return insertImagette(imagette, null);
	}

	/**
	 * Insert an Example into the database.
	 * @param imagette the picture to add.
	 * @param  document the document where the picture comes.
	 * @return true if success, false otherwise.
	 */
	public boolean insertImagette(Imagette imagette, Document document) {
		String imagetteName = imagette.getName();
		File file = new File(folderPathToStorePictures + imagetteName);

		//Check if file exists
		if(file.exists()) {
			System.err.println("A file named \"" + imagetteName + "\" already exists.");
			return false;
		}

		try {
			//Update the base
			String sql;
			PreparedStatement pstmt;

			if(!(document == null)) {
				sql = "INSERT INTO " + Table.EXEMPLE + "  (idOwner, picture) VALUES (?, ?);";
				pstmt = c.prepareStatement(sql);
				pstmt.setString(1, String.valueOf(document.getId()));
				pstmt.setString(2, folderPathToStorePictures + imagetteName);
			} else {
				sql = "INSERT INTO " + Table.EXEMPLE + "  (picture) VALUES (?);";
				pstmt = c.prepareStatement(sql);
				pstmt.setString(1, folderPathToStorePictures + imagetteName);
			}

			pstmt.executeUpdate();

			//Save the picture
			File outputfile = new File(folderPathToStorePictures + imagetteName);
			ImageIO.write((BufferedImage) imagette.getImage(), "jpg", outputfile);

			return true;
		} catch (SQLException e) {
			System.err.println("Error occured while adding " + imagetteName + ".");
		} catch (IOException e) {
			System.err.println("Error occured while saving the picture.");
		}
		return false;
	}

	/**
	 * Insert a record into the table.
	 * @param tableName the name of the table where you want to add a record.
	 * @param imagetteName the name of the imagette you want to add.
	 * @param img the imagette file.
	 * @param transcription the imagette transcription.
	 * @return true if success, false otherwise.
	 */
    /*public boolean insertImagetteWithGroundTruth(Imagette imagette, Document document, String groundTruth) {
        try {
            String imagetteName = imagette.getName();
            File file = new File(folderPathToStorePictures + imagetteName);

            if(file.exists()) {
                System.err.println("A file named \"" + imagetteName + "\" already exists.");
                return false;
            }

            //Update the base
            String sql = "INSERT INTO " + tableName + "  (IMAGETTE, TRANSCRIPTION) VALUES (?, ?);";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, folderPath + imagetteName);
            pstmt.setString(2, transcription);
            pstmt.executeUpdate();

            //Update the folder
            File outputfile = new File(folderPath + imagetteName);
            ImageIO.write((BufferedImage) img, "jpg", outputfile);

            return true;
        } catch (Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }*/

	/**
	 * Insert a record into the table.
	 * @param imagetteName the name of the imagette you want to add.
	 * @param  img the imagette file.
	 * @param result the imagette result.
	 * @return true if success, false otherwise.
	 */
    /*public boolean insertImagetteWithResult(String imagetteName, Image img, String result) {
        try {
            String folderPath = folderPath(Table.RESULTATS);
            File file = new File(folderPath + imagetteName);
            if(file.exists()) {
                System.err.println("A file named \"" + imagetteName + "\" already exists.");
                return false;
            }
            //Update the base
            String sql = "INSERT INTO " + Table.RESULTATS + "  (IMAGETTE, RESULTAT) VALUES (?, ?);";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, folderPath + imagetteName);
            pstmt.setString(2, result);
            pstmt.executeUpdate();

            //Update the folder
            File outputfile = new File(folderPath + imagetteName);
            ImageIO.write((BufferedImage) img, "jpg", outputfile);

            return true;
        } catch (Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }*/

	/**
	 * Insert a record into the table.
	 * @param imagetteName the name of the imagette you want to add.
	 * @param img the imagette file.
	 * @param transcription the transcription of the imagette.
	 * @param result the imagette result.
	 * @return true if success, false otherwise.
	 */
    /*public boolean insertImagetteWithTranscriptionAndResult(String imagetteName, Image img, String transcription, String result) {
        try {
            String folderPath = folderPath(Table.RESULTATS);
            File file = new File(folderPath + imagetteName);
            if(file.exists()) {
                System.err.println("A file named \"" + imagetteName + "\" already exists.");
                return false;
            }
            //Update the base
            String sql = "INSERT INTO " + Table.RESULTATS + "  (IMAGETTE, TRANSCRIPTION, RESULTAT) VALUES (?, ?, ?);";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, folderPath + imagetteName);
            pstmt.setString(2, transcription);
            pstmt.setString(3, result);
            pstmt.executeUpdate();

            //Update the folder
            File outputfile = new File(folderPath + imagetteName);
            ImageIO.write((BufferedImage) img, "jpg", outputfile);

            return true;
        } catch (Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }*/
	//------------------------------------------------------------------------------------------------------------------


	//---------------------------------------- Selection methods -------------------------------------------------------
	/**
	 * Select imagette from a table
	 * @param tableName the name of the table where you want to get the imagette.
	 * @param id the id of the record.
	 * @return an Image.
	 */
    /*public Image selectImagette(Table tableName, int id) {
        try {
            String sql = "SELECT IMAGETTE FROM "+ tableName + " WHERE ID=?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Returning the picture : " + rs.getString("IMAGETTE"));
                return  ImageIO.read(new File(rs.getString("IMAGETTE")));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.err.println("No picture found ! Maybe the id " + id + " isn't correct ?");
        return null;
    }*/

	/**
	 * Select all the imagettes in a given table.
	 * @param tableName the name of the table.
	 * @return a list of Image.
	 */
   /* public List<Image> selectAllImagettes(Table tableName) {
        List<Image> imagettes = new ArrayList<>();

        try{
            String sql = "SELECT IMAGETTE FROM "+ tableName + ";";
            PreparedStatement pstmt = c.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                imagettes.add(ImageIO.read(new File(rs.getString("IMAGETTE"))));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return imagettes;
    }*/

	/**
	 * Select a transcription from a table.
	 * @param tableName the name of the table where you want to get the transcription.
	 * @param id the id of the record.
	 * @return a transcription.
	 */
   /* public String selectTranscription(Table tableName, int id) {
        try {
            String sql = "SELECT TRANSCRIPTION FROM " + tableName + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Returning the transcription \"" + rs.getString("TRANSCRIPTION") + "\".");
                return rs.getString("TRANSCRIPTION");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.err.println("No transcription found ! Maybe the id " + id + " isn't correct ?");
        return null;
    }*/

	/**
	 * Select transcriptions from a table.
	 * @param tableName the name of the table where you want to get the transcriptions.
	 * @return a list of transcriptions.
	 */
    /*public List<String> selectAllTranscriptions(Table tableName) {
        List<String> transcriptions = new ArrayList<>();
        try {
            String sql = "SELECT TRANSCRIPTION FROM " + tableName + ";";
            PreparedStatement pstmt = c.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                transcriptions.add(rs.getString("TRANSCRIPTION"));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return transcriptions;
    }
*/
	/**
	 * Select a result from a table.
	 * @param id the id of the record.
	 * @return a result.
	 */
    /*public String selectResult(int id) {
        try {
            String sql = "SELECT RESULTAT FROM " + Table.RESULTATS + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Returning the result \"" + rs.getString("RESULTAT") + "\".");
                return rs.getString("RESULTAT");
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        System.err.println("No result found ! Maybe the id " + id + " isn't correct ?");
        return null;
    }*/

	/**
	 * Select results from a table.
	 * @return a list of results.
	 */
//    public List<String> selectAllResults() {
//        List<String> results = new ArrayList<>();
//        try {
//            String sql = "SELECT RESULTAT FROM " + Table.RESULTATS + ";";
//            PreparedStatement pstmt = c.prepareStatement(sql);
//            ResultSet rs = pstmt.executeQuery();
//
//            while (rs.next()) {
//                results.add(rs.getString("RESULTAT"));
//            }
//        } catch (Exception e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//        }
//        return results;
//    }

	/**
	 * Select a couple imagette and transcription from a table.
	 * @param tableName the name of the table.
	 * @param id the id of the record.
	 * @return a map composed of one element containing an imagette and its transcription.
	 */
   /* public Map<Image, String> selectImagetteAndTranscription(Table tableName, int id) {
        Map<Image, String> record = new HashMap<>();
        try {
            String sql = "SELECT IMAGETTE, TRANSCRIPTION FROM " + tableName + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                record.put(ImageIO.read(new File(rs.getString("IMAGETTE"))), rs.getString("TRANSCRIPTION"));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return Collections.unmodifiableMap(record);
    }*/

	/**
	 * Select the couples imagette and transcription from a table.
	 * @param tableName the name of the table.
	 * @return a map composed of imagettes and their transcription.
	 */
    /*public Map<Image, String> selectAllImagetteAndTranscription(Table tableName) {
        Map<Image, String> record = new HashMap<>();
        try {
            String sql = "SELECT IMAGETTE, TRANSCRIPTION FROM " + tableName + ";";
            PreparedStatement pstmt = c.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                record.put(ImageIO.read(new File(rs.getString("IMAGETTE"))), rs.getString("TRANSCRIPTION"));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return Collections.unmodifiableMap(record);
    }*/

	/**
	 * Select the couple imagette and result.
	 * @param id the id of the record.
	 * @return a map containing the imagette and its result.
	 */
    /*public Map<Image, String> selectImagetteAndResult(int id) {
        Map<Image, String> record = new HashMap<>();
        try {
            String sql = "SELECT IMAGETTE, RESULTAT FROM " + Table.RESULTATS + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                record.put(ImageIO.read(new File(rs.getString("IMAGETTE"))), rs.getString("RESULTAT"));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return Collections.unmodifiableMap(record);
    }*/

	/**
	 * Select the couples imagette and result.
	 * @return a map containing the imagettes and their result.
	 */
//    public Map<Image, String> selectAllImagetteAndResult() {
//        Map<Image, String> record = new HashMap<>();
//        try {
//            String sql = "SELECT IMAGETTE, RESULTAT FROM " + Table.RESULTATS + ";";
//            PreparedStatement pstmt = c.prepareStatement(sql);
//            ResultSet rs = pstmt.executeQuery();
//
//            while(rs.next()) {
//                record.put(ImageIO.read(new File(rs.getString("IMAGETTE"))), rs.getString("RESULTAT"));
//            }
//        } catch (Exception e) {
//            System.err.println(e.getClass().getName() + ": " + e.getMessage());
//        }
//        return Collections.unmodifiableMap(record);
//    }

	/**
	 * Select an imagette, its transcription and its result.
	 * @param id the record to select.
	 * @return a map containing the imagette and a list containing the transcription at the index 0 and the result at the index 1.
	 */
    /*public Map<Image, List<String>> selectImagetteAndTranscriptionAndResult(int id) {
        Map<Image, List<String>> record = new HashMap<>();
        try {
            String sql = "SELECT IMAGETTE, TRANSCRIPTION, RESULTAT FROM " + Table.RESULTATS + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                List<String> tmp = new ArrayList<>();
                tmp.add(rs.getString("TRANSCRIPTION"));
                tmp.add(rs.getString("RESULTAT"));
                record.put(ImageIO.read(new File(rs.getString("IMAGETTE"))), tmp);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return Collections.unmodifiableMap(record);

    }*/

	/**
	 * Select all the imagettes, their transcription and their result.
	 * @return a map containing the imagettes and lists containing the transcription at the index 0 and the result at the index 1.
	 */
    /*public Map<Image, List<String>> selectAllImagetteAndTranscriptionAndResult() {
        Map<Image, List<String>> record = new HashMap<>();
        try {
            String sql = "SELECT IMAGETTE, TRANSCRIPTION, RESULTAT FROM " + Table.RESULTATS + ";";
            PreparedStatement pstmt = c.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            List<String> tmp;
            while(rs.next()) {
                tmp = new ArrayList<>();
                tmp.add(rs.getString("TRANSCRIPTION"));
                tmp.add(rs.getString("RESULTAT"));
                record.put(ImageIO.read(new File(rs.getString("IMAGETTE"))), tmp);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return Collections.unmodifiableMap(record);
    }*/

	//------------------------------------------------------------------------------------------------------------------


	//----------------------------------------- Update methods ---------------------------------------------------------
	/**
	 * Update the path of an imagette with the given id.
	 * @param tableName the name of the table where you want to update the imagette.
	 * @param id the id of the record.
	 * @param imagetteName the name of the new imagette.
	 * @param img the imagette
	 * @return true if success, false otherwise.
	 */
/*
    public boolean updateImagette(Table tableName, int id, String imagetteName, Image img) {
        try {
            //Delete old path
            String folderPath = folderPath(tableName);
            String sql = "SELECT IMAGETTE FROM " + tableName + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            //Delete old file
            if (rs.next()) {
                File oldFile = new File(rs.getString("IMAGETTE"));
                File existingFile = new File(folderPath + imagetteName);
                if(existingFile.exists() && !oldFile.getPath().equals(existingFile.getPath()) && imagetteName.equals(existingFile.getName())) {
                    System.err.println("A file named \"" + imagetteName + "\" already exists.");
                    return false;
                }

                if(!oldFile.delete()) {
                    System.err.println("Error occured while deleting old file !");
                    return false;
                }
            } else {
                System.err.println("No picture found ! Maybe the id " + id + " isn't correct ?");
                return false;
            }

            //Create new path
            sql = "UPDATE " + tableName + " SET IMAGETTE = ? WHERE ID = ?;";
            PreparedStatement pstmt2 = c.prepareStatement(sql);
            pstmt2.setString(1,folderPath + imagetteName);
            pstmt2.setInt(2, id);
            pstmt2.executeUpdate();

            //Create new file
            File outputfile = new File(folderPath + imagetteName);
            ImageIO.write((BufferedImage) img, "jpg", outputfile);

            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.err.println("No record with the id " + id + " found.");
        return false;
    }
*/

	/**
	 * Update the transcription of an imagette with the given id.
	 * @param tableName the name of the table where you want to update the imagette.
	 * @param id the id of the record.
	 * @param transcription the new transcription.
	 * @return true if success, false otherwise.
	 */
/*
    public boolean updateTranscription(Table tableName, int id, String transcription) {
        try {
            String sql = "SELECT ID FROM " + tableName + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                sql = "UPDATE " + tableName + " SET TRANSCRIPTION = ? WHERE ID= ?;";
                PreparedStatement pstmt2 = c.prepareStatement(sql);
                pstmt2.setString(1, transcription);
                pstmt2.setInt(2, id);
                pstmt2.executeUpdate();

                return true;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.err.println("No record with the id " + id + " found.");
        return false;
    }
*/

	/**
	 * Update the result of an imagette with the given id.
	 * @param id the id of the record.
	 * @param result the new result.
	 * @return true if success, false otherwise.
	 */
/*
    public boolean updateResult(int id, String result) {
        try {
            String sql = "SELECT ID FROM " + Table.RESULTATS + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()) {
                sql = "UPDATE " + Table.RESULTATS + " SET RESULTAT = ? WHERE ID= ?;";
                PreparedStatement pstmt2 = c.prepareStatement(sql);
                pstmt2.setString(1, result);
                pstmt2.setInt(2, id);
                pstmt2.executeUpdate();

                return true;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.err.println("No record with the id " + id + " found.");
        return false;
    }
*/

	/**
	 * Update both imagette and transcription.
	 * @param tableName the name of the table.
	 * @param id the id of the record to update.
	 * @param imagetteName the new imagette name.
	 * @param img the imagette file.
	 * @param transcription the new transcription.
	 * @return true if success, false otherwise.
	 */
/*
    public boolean updateImagetteAndTranscription(Table tableName, int id, String imagetteName, Image img, String transcription) {
        return updateImagette(tableName, id, imagetteName, img) && updateTranscription(tableName, id, transcription);
    }
*/
	//------------------------------------------------------------------------------------------------------------------


	//---------------------------------------- Deletion methods --------------------------------------------------------
	/**
	 * Delete a record linked to the given id.
	 * @param tableName the name of the table.
	 * @param id the id of the record to delete.
	 * @return true if success, false otherwise.
	 */
/*
    public boolean delete(Table tableName, int id) {
        try {
            String sql = "SELECT IMAGETTE FROM "+ tableName + " WHERE ID = ?;";
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                File file = new File(rs.getString("IMAGETTE"));
                if (!file.delete()) System.err.println("Error occured while deleting old file !");
            } else {
                System.err.println("No record found with the id " + id + ".");
                return false;
            }

            sql = "DELETE from " + tableName + " where ID = ?;";
            PreparedStatement pstmt2 = c.prepareStatement(sql);
            pstmt2.setInt(1, id);
            pstmt2.executeUpdate();

            System.out.println("The delete operation on the table " + tableName + " was a success.");
            return true;
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return false;
    }
*/
	//------------------------------------------------------------------------------------------------------------------


	//------------------------------------------ Other methods ---------------------------------------------------------
	/**
	 * Return the number of records in the table.
	 * @param tableName the table you want to know the size.
	 * @return the number of records.
	 */
	public int getTableSize(Table tableName) {
		try {
			String sql = "SELECT Count(id) FROM "+ tableName + ";";
			PreparedStatement pstmt = c.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			if(rs.next()) return Integer.valueOf(rs.getString(1));

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
		return 0;
	}
	//------------------------------------------------------------------------------------------------------------------



	//-------------------- Private methods used to build the data base -------------------------------------------------
	/**
	 * Create a new database with the name baseName and a folder.
	 * If the base already exists, does nothing.
	 */
	private void createBase() {
		try {
			Class.forName("org.sqlite.JDBC");
			this.c = DriverManager.getConnection("jdbc:sqlite:" + this.dataBaseName + ".db");
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	/**
	 * Create a table which name is tableName
	 * The structure of the table is defined inside the method because it will only be used by
	 * the database team.
	 * The rest of the group does not need to use this.
	 * @param tableName the name of the table to create.
	 * @param query the SQL Query for database creation
	 */
	private void createTable(Table tableName, String query){
		try {
			openConnection();

			String sql = "CREATE TABLE IF NOT EXISTS " + tableName.toString() + "(" + query + ")";
			//System.out.println(sql);
			PreparedStatement pstmt = c.prepareStatement(sql);
			pstmt.executeUpdate();

			closeConnection();

			System.out.println("main.Table " + tableName +" created successfully.");
		} catch (SQLiteException e) {
			System.out.println("main.Table " + tableName +" has already been created.");
			e.printStackTrace();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	/**
	 * Create a directory to store the pictures.
	 * @param folderPath the directory's path.
	 */
	private void createFolder(String folderPath) {
		Path path = Paths.get(folderPath);
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//------------------------------------------------------------------------------------------------------------------
}

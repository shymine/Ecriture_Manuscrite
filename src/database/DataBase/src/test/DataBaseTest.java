package test;

import static org.junit.Assert.*;
import main.DataBase;
import main.Document;
import main.Imagette;
import main.Table;
import org.junit.Before;
import org.junit.Test;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class DataBaseTest {

    private DataBase d;
    private Imagette img1;
    private Imagette img2;
    private Document doc1;
    private Document doc2;

    @Before
    public void before() {

        String path = "imagettesFolder";
        String baseName = "base";
        img1 = new Imagette("imagette1.jpg", openImage("./pictures/bertrand.jpg"));
        img2 = new Imagette("imagette2.jpg", openImage("./pictures/pascal.jpg"));
        doc1 = new Document(1);
        doc2 = new Document(2);

        //Deleting existing base
        deleteFiles(baseName, path);

        // Database initialization
        d = new DataBase(baseName, path);
    }

    @Test
    public void testInsertImagette() {
        d.openConnection();

        assertTrue(d.insertImagette(img1));
        assertTrue(d.insertImagette(img2, doc1));
        assertFalse(d.insertImagette(img1, doc1));
        assertFalse(d.insertImagette(img2, doc2));

        d.closeConnection();

    }
/*
    @Test
    public void testInsertWithTranscription() {
        d.openConnection();

        assertTrue(d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "def.jpg", img2,"J'aime les chèvres."));
        assertTrue(d.insertImagetteWithTranscription(Table.RESULTATS, "potatoes.jpg", img1, "blablabla"));
        assertFalse(d.insertImagetteWithTranscription(Table.RESULTATS, "potatoes.jpg", img2, "false here"));

        d.closeConnection();
    }

    @Test
    public void testInsertImagetteWithResult() {
        d.openConnection();

        assertTrue(d.insertImagetteWithResult("def.jpg", img2,"J'aime les chèvres."));
        assertTrue(d.insertImagetteWithResult("potatoes.jpg", img1, "blablabla"));
        assertFalse(d.insertImagetteWithResult("potatoes.jpg", img2, "false here."));

        d.closeConnection();
    }

    @Test
    public void testInsertImagetteWithTranscriptionAndResult() {
        d.openConnection();

        assertTrue(d.insertImagetteWithTranscriptionAndResult("def.jpg", img2,"J'aime les chèvres.", "J'aime les chèvres."));
        assertTrue(d.insertImagetteWithTranscriptionAndResult("potatoes.jpg", img1, "blablabla", "blabla2"));
        assertFalse(d.insertImagetteWithTranscriptionAndResult("potatoes.jpg", img2, "false here.", "yes, here."));

        d.closeConnection();
    }

    @Test
    public void testGetTableSize() {
        d.openConnection();

        assertEquals(d.getTableSize(Table.APPRENTISSAGE), 0);
        assertEquals(d.getTableSize(Table.RESULTATS), 0);

        d.insertImagette(Table.APPRENTISSAGE, "img1.jpg", img1);
        assertEquals(d.getTableSize(Table.APPRENTISSAGE), 1);
        assertEquals(d.getTableSize(Table.RESULTATS), 0);

        d.insertImagette(Table.APPRENTISSAGE, "img2.jpg", img2);
        assertEquals(d.getTableSize(Table.APPRENTISSAGE), 2);
        assertEquals(d.getTableSize(Table.RESULTATS), 0);

        d.insertImagette(Table.RESULTATS, "img2.jpg", img2);
        assertEquals(d.getTableSize(Table.APPRENTISSAGE), 2);
        assertEquals(d.getTableSize(Table.RESULTATS), 1);

        d.closeConnection();
    }

    @Test
    public void testSelectImagette() {
        d.openConnection();

        d.insertImagette(Table.APPRENTISSAGE, "img1.jpg", img1);
        assertEquals(d.selectImagette(main.Table.APPRENTISSAGE, 1).getClass(), BufferedImage.class);

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img2.jpg", img2, "ma transcription");
        assertEquals(d.selectImagette(main.Table.APPRENTISSAGE, 2).getClass(), BufferedImage.class);

        assertNull(d.selectImagette(main.Table.APPRENTISSAGE, 3));

        d.closeConnection();
    }

    @Test
    public void testSelectAllImagettes() {
        d.openConnection();

        assertEquals(d.selectAllImagettes(main.Table.APPRENTISSAGE).size(), 0);

        d.insertImagette(Table.APPRENTISSAGE, "img1.jpg", img1);
        assertEquals(d.selectAllImagettes(main.Table.APPRENTISSAGE).size(), 1);

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img2.jpg", img2, "ma transcription");
        assertEquals(d.selectAllImagettes(main.Table.APPRENTISSAGE).size(), 2);

        d.delete(Table.APPRENTISSAGE, 2);
        assertEquals(d.selectAllImagettes(main.Table.APPRENTISSAGE).size(), 1);

        d.closeConnection();
    }

    @Test
    public void testSelectTranscriptionn() {
        d.openConnection();

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img1.jpg", img1, "ma transcription");
        assertEquals(d.selectTranscription(main.Table.APPRENTISSAGE, 1), "ma transcription");

        d.insertImagette(Table.APPRENTISSAGE, "img2.jpg", img2);
        assertNull(d.selectTranscription(main.Table.APPRENTISSAGE, 2));

        d.closeConnection();
    }

    @Test
    public void testSelectAllTranscriptions() {
        d.openConnection();

        assertEquals(d.selectAllResults().size(), 0);

        d.insertImagetteWithTranscription(Table.RESULTATS, "img1.jpg", img1, "abc");
        assertEquals(d.selectAllTranscriptions(Table.RESULTATS).size(), 1);

        d.insertImagetteWithTranscriptionAndResult("img2.jpg", img2, "ma transcription", "le resultat");
        assertEquals(d.selectAllTranscriptions(Table.RESULTATS).size(), 2);

        d.delete(Table.RESULTATS, 2);
        assertEquals(d.selectAllResults().size(), 1);

        d.closeConnection();
    }

    @Test
    public void testSelectImagetteAndTranscription() {
        d.openConnection();

        d.insertImagette(Table.APPRENTISSAGE, "img1.jpg", img1);
        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img2.jpg", img2, "ma transcription");

        Map record1 = d.selectImagetteAndTranscription(Table.APPRENTISSAGE, 1);
        Map record2 = d.selectImagetteAndTranscription(Table.APPRENTISSAGE, 2);
        Map record3 = d.selectImagetteAndTranscription(Table.APPRENTISSAGE, 3);

        assertNull(record1.get(record1.keySet().iterator().next()));
        assertEquals(record2.get(record2.keySet().iterator().next()), "ma transcription");
        assertEquals(record3.size(), 0);

        d.closeConnection();
    }

    @Test
    public void testSelectAllImagetteAndTranscription() {
        d.openConnection();

        Map record1 = d.selectAllImagetteAndTranscription(Table.APPRENTISSAGE);
        assertEquals(record1.size(), 0);

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img2.jpg", img2, "ma transcription");
        Map record2 = d.selectAllImagetteAndTranscription(Table.APPRENTISSAGE);
        assertEquals(record2.get(record2.keySet().iterator().next()), "ma transcription");

        d.insertImagette(Table.APPRENTISSAGE, "img1.jpg", img1);
        Map record3 = d.selectAllImagetteAndTranscription(Table.APPRENTISSAGE);
        assertEquals(record3.size(), 2);

        d.closeConnection();
    }

    @Test
    public void testSelectResult() {
        d.openConnection();

        d.insertImagetteWithTranscriptionAndResult("img1.jpg", img1, "ma transcription", "le resultat");
        assertEquals(d.selectResult(1), "le resultat");

        d.insertImagette(Table.RESULTATS, "img2.jpg", img2);
        assertNull(d.selectResult(2));

        d.closeConnection();
    }

    @Test
    public void testSelectAllResults() {
        d.openConnection();

        assertEquals(d.selectAllTranscriptions(main.Table.APPRENTISSAGE).size(), 0);

        d.insertImagette(Table.APPRENTISSAGE, "img1.jpg", img1);
        assertEquals(d.selectAllTranscriptions(main.Table.APPRENTISSAGE).size(), 1);

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img2.jpg", img2, "ma transcription");
        assertEquals(d.selectAllTranscriptions(main.Table.APPRENTISSAGE).size(), 2);

        d.delete(Table.APPRENTISSAGE, 2);
        assertEquals(d.selectAllTranscriptions(main.Table.APPRENTISSAGE).size(), 1);

        d.closeConnection();
    }

    @Test
    public void testSelectImagetteAndResult() {
        d.openConnection();

        d.insertImagetteWithTranscriptionAndResult("img2.jpg", img2, "ma transcription", "le resultat");
        d.insertImagetteWithResult("img1.jpg", img1, "le resultat2");

        Map record1 = d.selectImagetteAndResult(1);
        Map record2 = d.selectImagetteAndResult(2);
        Map record3 = d.selectImagetteAndResult(3);

        assertEquals(record1.get(record1.keySet().iterator().next()), "le resultat");
        assertEquals(record2.get(record2.keySet().iterator().next()), "le resultat2");
        assertEquals(record3.size(), 0);

        d.closeConnection();
    }

    @Test
    public void testSelectAllImagetteAndResult() {
        d.openConnection();

        Map record1 = d.selectAllImagetteAndTranscription(Table.APPRENTISSAGE);
        assertEquals(record1.size(), 0);

        d.insertImagetteWithTranscriptionAndResult("img2.jpg", img2, "ma transcription", "le resultat");
        Map record2 = d.selectAllImagetteAndResult();
        assertEquals(record2.get(record2.keySet().iterator().next()), "le resultat");

        d.insertImagetteWithResult("img1.jpg", img1, "resultat");
        Map record3 = d.selectAllImagetteAndResult();
        assertEquals(record3.size(), 2);

        d.closeConnection();
    }

    @Test
    public void testSelectImagetteAndTranscriptionAndResult() {
        d.openConnection();

        d.insertImagetteWithTranscriptionAndResult("img2.jpg", img2, "ma transcription", "le resultat");
        d.insertImagetteWithResult("img1.jpg", img1, "le resultat2");
        d.insertImagetteWithTranscription(Table.RESULTATS, "img2bis.jpg", img2, "transcription");

        Map record1 = d.selectImagetteAndTranscriptionAndResult(1);
        Map record2 = d.selectImagetteAndTranscriptionAndResult(2);
        Map record3 = d.selectImagetteAndTranscriptionAndResult(3);
        Map record4 = d.selectImagetteAndTranscriptionAndResult(4);

        assertEquals(((List) record1.get(record1.keySet().iterator().next())).get(1), "le resultat");
        assertEquals(((List) record1.get(record1.keySet().iterator().next())).get(0), "ma transcription");
        assertEquals(((List) record2.get(record2.keySet().iterator().next())).get(1), "le resultat2");
        assertEquals(record3.size(), 1);
        assertEquals(record4.size(), 0);

        d.closeConnection();
    }

    @Test
    public void testSelectAllImagetteAndTranscriptionAndResult() {
        d.openConnection();

        Map record1 = d.selectAllImagetteAndTranscriptionAndResult();
        assertEquals(record1.size(), 0);

        d.insertImagetteWithTranscriptionAndResult("img2.jpg", img2, "ma transcription", "le resultat");
        Map record2 = d.selectAllImagetteAndTranscriptionAndResult();
        assertEquals(((List) record2.get(record2.keySet().iterator().next())).get(1), "le resultat");
        assertEquals(((List) record2.get(record2.keySet().iterator().next())).get(0), "ma transcription");


        d.insertImagetteWithResult("img1.jpg", img1, "resultat");
        Map record3 = d.selectAllImagetteAndResult();
        assertEquals(record3.size(), 2);

        d.closeConnection();
    }

    @Test
    public void testUpdateImagette() {
        d.openConnection();

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img1.jpg", img1, "ma transcription");
        d.insertImagette(Table.APPRENTISSAGE, "img2.jpg", img2);

        assertTrue(d.updateImagette(Table.APPRENTISSAGE, 1, "img1bis.jpg", img1));
        assertTrue(d.updateImagette(Table.APPRENTISSAGE, 2, "img2.jpg", img2));
        assertFalse(d.updateImagette(Table.APPRENTISSAGE, 1, "img2.jpg", img2));
        assertTrue(d.updateImagette(Table.APPRENTISSAGE, 1, "img1bid.jpg", img2));
        assertFalse(d.updateImagette(Table.RESULTATS, 3, "bug.jpg", img1));

        d.closeConnection();
    }

    @Test
    public void testUpdateTranscription() {
        d.openConnection();

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img1.jpg", img1, "ma transcription");
        d.insertImagette(Table.APPRENTISSAGE, "img2.jpg", img2);

        assertTrue(d.updateTranscription(Table.APPRENTISSAGE, 1, "nouvelle transcription"));
        assertTrue(d.updateTranscription(Table.APPRENTISSAGE, 2, "blablabla2"));
        assertFalse(d.updateTranscription(Table.APPRENTISSAGE, 3, "here false !"));

        d.closeConnection();
    }

    @Test
    public void testUpdateResult() {
        d.openConnection();

        d.insertImagetteWithTranscriptionAndResult("img1.jpg", img1, "ma transcription", "le resultat");
        d.insertImagetteWithResult("img2.jpg", img2, "resultat");

        assertTrue(d.updateResult(1, "nouveau resultat"));
        assertTrue(d.updateResult(2, "blablabla2"));
        assertFalse(d.updateResult(3, "here false !"));

        d.closeConnection();
    }

    @Test
    public void testUpdateImagetteAndTranscription() {
        d.openConnection();

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img1.jpg", img1, "ma transcription");
        d.insertImagette(Table.APPRENTISSAGE, "img2.jpg", img2);

        assertTrue(d.updateImagetteAndTranscription(Table.APPRENTISSAGE, 1, "img1bis.jpg", img1, "nouvelle transcription"));
        assertTrue(d.updateImagetteAndTranscription(Table.APPRENTISSAGE, 2, "img2.jpg", img2, "blablabla2"));
        assertFalse(d.updateImagetteAndTranscription(Table.APPRENTISSAGE, 1, "img2.jpg", img2,"here false !"));
        assertFalse(d.updateImagetteAndTranscription(Table.APPRENTISSAGE, 3, "img2.jpg", img2,"here false !"));

        d.closeConnection();
    }

    @Test
    public void testDelete() {
        d.openConnection();

        d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img1.jpg", img1, "ma transcription");
        d.insertImagette(Table.APPRENTISSAGE, "img2.jpg", img2);
        d.insertImagetteWithTranscriptionAndResult("img1.jpg", img2, "transcription", "resultat");

        assertTrue(d.delete(Table.APPRENTISSAGE, 1));
        assertFalse(d.delete(Table.APPRENTISSAGE, 1));
        assertNull(d.selectImagette(Table.APPRENTISSAGE, 1));
        assertEquals(d.getTableSize(Table.APPRENTISSAGE), 1);

        assertNotNull(d.selectImagette(Table.APPRENTISSAGE, 2));

        assertTrue(d.insertImagetteWithTranscription(Table.APPRENTISSAGE, "img1.jpg", img1, "ma transcription"));


        d.closeConnection();
    }



*/
    //------------------------------------------------------------------------------------------------------------------
    private Image openImage(String name) {
        try {
            return ImageIO.read(new File(name));
        }
        catch (IOException e)
        {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
        return null;
    }

    private void deleteFiles(String baseName, String path) {
        File file = new File(baseName + ".db");
        file.delete();

        file = new File(path);
        String[] subFolders = file.list();
        if(subFolders != null) {
            for (String folderName : subFolders) {
                File folder = new File(file.getPath(), folderName);
                String[] pictures = folder.list();
                if(pictures != null) {
                    for (String pictureName : pictures) {
                        File picture = new File(folder.getPath(), pictureName);
                        picture.delete();
                    }
                }
                folder.delete();
            }
        }
        file.delete();
    }
    //------------------------------------------------------------------------------------------------------------------
}

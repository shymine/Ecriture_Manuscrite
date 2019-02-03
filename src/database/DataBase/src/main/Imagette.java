package database.DataBase.src.main;

import java.awt.*;

public class Imagette {
    private String name;
    private Image image;

    public Imagette(final String name, Image image) {
        this.name = name;
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setImage(final Image image) {
        this.image = image;
    }
}

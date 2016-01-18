package de.lmu.ifi.bouncingbash.app.game.views;

/**
 * Created by Michi on 16.01.2016.
 */
public class CustomUserData {

    private boolean isFlaggedForDelete=false;

    public  CustomUserData(boolean isFlaggedForDelete)
    {
        this.isFlaggedForDelete = isFlaggedForDelete;
    }

    public boolean isFlaggedForDelete() {
        return isFlaggedForDelete;
    }

    public void setIsFlaggedForDelete(boolean isFlaggedForDelete) {
        this.isFlaggedForDelete = isFlaggedForDelete;
    }

}

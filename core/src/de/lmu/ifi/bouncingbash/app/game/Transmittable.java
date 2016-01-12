package de.lmu.ifi.bouncingbash.app.game;

import com.eclipsesource.json.JsonObject;

import java.util.HashMap;

/**
 * Created by Leon on 08.01.2016.
 */
public interface Transmittable {

    public String getId();
    public void setId(String _id);

    public JsonObject toJson();
    public void fromJson(JsonObject obj);
}

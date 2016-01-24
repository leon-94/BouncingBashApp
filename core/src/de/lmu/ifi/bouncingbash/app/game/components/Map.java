package de.lmu.ifi.bouncingbash.app.game.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.Iterator;

import de.lmu.ifi.bouncingbash.app.game.Game;

/**
 * Created by Leon on 23.01.2016.
 */
public class Map {

    Game game;
    World world;

    public String id;
    public String creator;
    public double lat;
    public double lng;

    public Wall[] walls;
    public Switch switches[];

    public Vector2 spawnPoints[];


    // create from Objects
    public Map(Game g, World w, Wall[] ws, Switch[] ss, Vector2[] sps) {
        game = g;
        world = w;

        walls = ws;
        switches = ss;
        spawnPoints = sps;
    }

    // create from JSON
    public Map(Game g, World w, String data) {
        game = g;
        world = w;

        JsonObject jsonMap = (JsonObject) Json.parse(data);

        JsonArray jsonWalls = (JsonArray) jsonMap.get("walls");
        walls = new Wall[jsonWalls.size()];
        for (int i = 0; i < jsonWalls.size(); i++){
            walls[i] = new Wall(game, world, (JsonObject)jsonWalls.get(i));
        }

        JsonArray jsonSwitches = (JsonArray) jsonMap.get("switches");
        switches = new Switch[jsonSwitches.size()];
        for (int i = 0; i < jsonSwitches.size(); i++){
            switches[i] = new Switch(game, world, (JsonObject)jsonSwitches.get(i));
        }

        JsonArray jsonSpawnPoints = (JsonArray) jsonMap.get("spawnPoints");
        spawnPoints = new Vector2[jsonSpawnPoints.size()];
        for (int i = 0; i < jsonSpawnPoints.size(); i++){
            JsonObject sp = (JsonObject)jsonSpawnPoints.get(i);
            spawnPoints[i] = new Vector2(sp.getFloat("x", 0), sp.getFloat("y", 0));
        }
    }

    public JsonObject toJson() {
        JsonObject jsonMap = new JsonObject();
        jsonMap.add("id", "mapfromobjects");

        JsonArray jsonWalls = new JsonArray();
        for(int i = 0; i < walls.length; i++) {
            jsonWalls.add(walls[i].toJson());
        }
        jsonMap.add("walls", jsonWalls);

        JsonArray jsonSwitches = new JsonArray();
        for(int i = 0; i < switches.length; i++) {
            jsonSwitches.add(switches[i].toJson());
        }
        jsonMap.add("switches", jsonSwitches);

        JsonArray jsonSps = new JsonArray();
        for(int i = 0; i < spawnPoints.length; i++) {
            JsonObject sp = new JsonObject();
            sp.add("x", spawnPoints[i].x);
            sp.add("y", spawnPoints[i].y);
            jsonSps.add(sp);
        }
        jsonMap.add("spawnPoints", jsonSps);

        return jsonMap;
    }


    public Vector2[] getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(Vector2[] spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
}

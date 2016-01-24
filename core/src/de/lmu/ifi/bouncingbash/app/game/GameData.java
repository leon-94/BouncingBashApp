package de.lmu.ifi.bouncingbash.app.game;

/**
 * Created by Leon on 10.01.2016.
 */
public class GameData {

    public static long gameStartTime = 0;
    public static long meanLatency = 0;
    public static boolean isHost = false;
    public static boolean won = false;
    public static boolean postgame = false;
    public static boolean debug_sp = false;
    public static String map;
    //    public static String defaultMap = "{\"id\":\"mapfromobjects\",\"walls\":[{\"x\":710,\"y\":0,\"width\":500,\"height\":30},{\"x\":710,\"y\":1050,\"width\":500,\"height\":30},{\"x\":0,\"y\":300,\"width\":500,\"height\":30},{\"x\":1420,\"y\":300,\"width\":500,\"height\":30},{\"x\":0,\"y\":750,\"width\":500,\"height\":30},{\"x\":1420,\"y\":750,\"width\":500,\"height\":30}]}";
    public static String defaultMap = "{\"id\":\"mapfromobjects\",\"walls\":[{\"x\":710,\"y\":0,\"width\":500,\"height\":30},{\"x\":710,\"y\":1050,\"width\":500,\"height\":30},{\"x\":0,\"y\":300,\"width\":500,\"height\":30},{\"x\":1420,\"y\":300,\"width\":500,\"height\":30},{\"x\":0,\"y\":750,\"width\":500,\"height\":30},{\"x\":1420,\"y\":750,\"width\":500,\"height\":30}],\"switches\":[{\"x\":895,\"y\":475}],\"spawnPoints\":[{\"x\":100,\"y\":476},{\"x\":1756,\"y\":476},{\"x\":960,\"y\":296}]}";
}

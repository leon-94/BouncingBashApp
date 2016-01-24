package de.lmu.ifi.bouncingbash.app;

import com.eclipsesource.json.JsonObject;

public class Session {

    private String hostId;
    private String otherId;
    private String hostMac;
    private String otherMac;
    private double lat;
    private double lng;
    private String mapData;

    public Session(String hostId, String hostMac, double lat, double lng, String m) {
        this.hostId = hostId;
        this.hostMac = hostMac;
        this.lat = lat;
        this.lng = lng;
        this.mapData = m;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.add("hostId", hostId);
        json.add("hostMac", hostMac);
        json.add("otherId", otherId);
        json.add("otherMac", otherMac);
        json.add("lat", lat);
        json.add("lng", lng);
        json.add("mapData", mapData);

        return json;
    }

    public static Session fromJson(JsonObject json) {

        String hostId = json.getString("hostId", null);
        String hostMac = json.getString("hostMac", null);
        String otherId = json.getString("otherId", null);
        String otherMac = json.getString("otherMac", null);
        double lat = json.getDouble("lat", 1000);
        double lng = json.getDouble("lng", 1000);
        String mapData = json.getString("mapData", "");

        Session session = new Session(hostId, hostMac, lat, lng, mapData);
        session.setOtherId(otherId);
        session.setOtherMac(otherMac);

        return session;
    }

    public String getMapData() {
        return mapData;
    }

    public void setMapData(String mapData) {
        this.mapData = mapData;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String getHostMac() {
        return hostMac;
    }

    public void setHostMac(String hostMac) {
        this.hostMac = hostMac;
    }

    public String getOtherMac() {
        return otherMac;
    }

    public void setOtherMac(String otherMac) {
        this.otherMac = otherMac;
    }
}

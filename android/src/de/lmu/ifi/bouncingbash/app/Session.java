package de.lmu.ifi.bouncingbash.app;

import com.eclipsesource.json.JsonObject;

public class Session {

    private String hostId;
	private String otherId;
	private String hostMac;
	private String otherMac;
	private double lat;
    private double lng;
	
	public Session(String hostId, String hostMac, double lat, double lng) {
		this.hostId = hostId;
		this.hostMac = hostMac;
		this.lat = lat;
		this.lng = lng;
	}
	
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		
		json.add("hostId", hostId);
		json.add("hostMac", hostMac);
		json.add("otherId", otherId);
		json.add("otherMac", otherMac);
		json.add("lat", lat);
		json.add("lng", lng);
		
		return json;
	}

    public static Session fromJson(JsonObject json) {

        String hostId = json.getString("hostId", null);
        String hostMac = json.getString("hostMac", null);
        String otherId = json.getString("otherId", null);
        String otherMac = json.getString("otherMac", null);
        double lat = json.getDouble("lat", 1000);
        double lng = json.getDouble("lng", 1000);

        Session session = new Session(hostId, hostMac, lat, lng);
        session.setOtherId(otherId);
        session.setOtherMac(otherMac);

        return session;
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

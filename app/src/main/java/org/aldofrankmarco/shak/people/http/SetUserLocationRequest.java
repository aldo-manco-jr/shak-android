package org.aldofrankmarco.shak.people.http;

public class SetUserLocationRequest {

    private String city;

    private String country;

    public SetUserLocationRequest(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}

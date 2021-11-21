package Fann.weather;

import java.util.List;

public class Province {
    private String name="";
    private List<CitySet> city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CitySet> getCity() {
        return city;
    }

    public void setCity(List<CitySet> city) {
        this.city = city;
    }
}

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Route implements Comparable<Route>{
    Double distance = 0d;
    private ArrayList<City> city_order;

    private static final DecimalFormat decimal_format = new DecimalFormat("#.##");

    public Route(ArrayList<City> city_order) {
        this.city_order = city_order;
        this.distance = calculate_total_distance(city_order);
    }

    private Double calculate_total_distance(ArrayList<City> cities){
        Double distance_total = 0d;

        for (int i = 0; i < cities.size()-1; i++)
            distance_total += cities.get(i).get_distance(cities.get(i+1));

        distance_total += cities.get(cities.size()-1).get_distance(cities.get(0));

        distance_total = (double) Math.round(distance_total * 10000);

        return  distance_total / 10000;
    }

    public ArrayList<City> getCity_order() {
        return city_order;
    }

    @Override
    public int compareTo(Route o) {
        return Double.compare(this.distance,o.distance);
    }

    @Override
    public String toString() {
        return city_order.toString();
    }
}

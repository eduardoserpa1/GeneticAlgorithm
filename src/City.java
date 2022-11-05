public class City {
    String name = "";
    Double x = 0d;
    Double y = 0d;

    public City(String name, Double x, Double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public Double get_distance(City c){
        Double module_x = Math.pow((this.x - c.x),2);
        Double module_y = Math.pow((this.y - c.y),2);
        return Math.sqrt(module_x + module_y);
    }

    @Override
    public String toString() {
        return x+","+y+","+name;
    }
}

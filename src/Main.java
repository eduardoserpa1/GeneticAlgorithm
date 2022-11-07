import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static Integer actual_it = 0;
    static Long amount_generated_units = 0L;
    static Integer evolution_counter = 0;

    static int first_population_length = 10000;
    static int first_population_amount_of_mutations = 1;
    static int iterations = 10000000;
    static int elite_amount = 5;
    static int amount_avaible_for_elite = 50;
    static int amount_mutations = 1;
    static int save_interval = 10;

    public static void main(String[] args) throws IOException{
        if(args.length == 1){
            if(!args[0].equals("-h")){
                start_routine(args[0],"");
                return;
            }
        }else if(args.length == 2){
            start_routine(args[0],args[1]);
            return;
        }
        System.out.println("Execução:\n"+
                            "java Main <caminho_do_arquivo>\n"+
                            "java Main <caminho_do_arquivo> <caminho_do_arquivo_checkpoint>\n"+
                            "java Main -h\n"+
                            "As configurações do algoritmo podem ser aleteradas através das variaveis globais no arquivo fonte, modificando:\n" +
                            "Quantidade de individuos na primeira população gerada;\n" +
                            "Quantidade de mutações nos individuos da primeira população gerada;\n" +
                            "Quantidade de iterações;\n" +
                            "Quantidade de individuos selecionados para elite;\n" +
                            "Quantidade de individuos disponíveis para cruzamento com a elite;\n" +
                            "Quantidade de mutações feitas em cada individuo resultante dos cruzamentos;\n"+
                            "Periodicidade da persistencia dos resultados em gerações;\n");
    }

    static void save_state(ArrayList<Route> actual_population, String generation, String amount_generated_units, String amount_evolution) throws IOException {
        try{
            File file = new File("backup.txt");
            FileWriter writer = new FileWriter(file,false);
            BufferedWriter buffer = new BufferedWriter(writer);
            file.createNewFile();

            buffer.write(generation);
            buffer.newLine();
            buffer.write(amount_generated_units);
            buffer.newLine();
            buffer.write(amount_evolution);
            buffer.newLine();

            for (Route r:actual_population){
                buffer.write(r.toString());
                buffer.newLine();
            }

            buffer.close();
            writer.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    static ArrayList<Route> load_state(String filename){
        ArrayList<Route> population = new ArrayList<>();

        Scanner sc = null;
        File file = new File(filename);
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.err.println("file " + filename + " not found");
            System.exit(2);
        }

        ArrayList<String> cities_list = new ArrayList<>();

        actual_it = Integer.parseInt(sc.nextLine());
        amount_generated_units = Long.parseLong(sc.nextLine());
        evolution_counter = Integer.parseInt(sc.nextLine());

        while(sc.hasNext()){
            String line = sc.nextLine();

            String list_cities_format = line.replaceAll("\\[","");
            list_cities_format = list_cities_format.replaceAll("]","");
            list_cities_format = list_cities_format.replaceAll(", ","-");

            cities_list.add(list_cities_format);
        }

        for(String s:cities_list){
            String[] cities_list_split = s.split("-");

            ArrayList<City> cities_of_route = new ArrayList<>();

            for (int i = 0; i < cities_list_split.length; i++) {
                String[] data = cities_list_split[i].split(",");

                Double x = Double.valueOf(data[0]);
                Double y = Double.valueOf(data[1]);

                City c = new City(data[2],x,y);

                cities_of_route.add(c);
            }
            population.add(new Route(cities_of_route));
        }

        return population;
    }

    static void start_routine(String filename, String checkpoint_filename) throws IOException{
        ArrayList<City> cities = loadCities(filename);

        Route greedy = get_greedy_route(cities);

        System.out.println("Solução com algoritmo guloso: "+greedy.distance);

        ArrayList<Route> population = new ArrayList<>();

        population.add(greedy);

        amount_generated_units = (long)first_population_length;

        if(!checkpoint_filename.isEmpty()){
            population = load_state(checkpoint_filename);
        }else{
            for (int i = 1; i < first_population_length; i++) {
                population.add(make_mutation(greedy,first_population_amount_of_mutations,true));
            }

            Collections.sort(population);

            System.out.println("Melhor solução com "+ first_population_amount_of_mutations +
                                " mutações em uma população de "+first_population_length+
                                " individuos: "+population.get(0).distance);
        }

        Route best_route = population.get(0);

        while(actual_it < iterations + actual_it){
            if(actual_it % save_interval == 0 && actual_it != 0){
                save_state(population,actual_it.toString(),amount_generated_units.toString(),evolution_counter.toString());

            }
            if(!best_route.equals(population.get(0))){
                best_route = population.get(0);
                ++evolution_counter;
            }

            if(actual_it % 5 == 0)
                System.out.println("geração: "+actual_it+" : melhor distancia = "+
                        best_route.distance+
                        " : quantidade de individuos gerados = "+amount_generated_units+
                        " : vezes que evoluiu = "+evolution_counter);

            ArrayList<Route> elite = new ArrayList<>();

            for (int j = 0; j < elite_amount; j++) {
                elite.add(population.get(j));
            }

            ArrayList<Route> avaible_to_elite = new ArrayList<>();

            for (int j = elite_amount; j < (amount_avaible_for_elite + elite_amount); j++) {
                avaible_to_elite.add(population.get(j));
            }

            population = new ArrayList<>();

            population.addAll(elite);

            for (int j = 0; j < elite_amount; j++) {
                for (int k = 0; k < amount_avaible_for_elite; k++) {
                    Route child1 = make_child(elite.get(j), avaible_to_elite.get(k));
                    Route child2 = make_child(avaible_to_elite.get(k), elite.get(j));
                    boolean sequencial = false;
                    if(actual_it % 2 == 0){
                        sequencial = true;
                    }
                    population.add(make_mutation(child1,amount_mutations,sequencial));
                    population.add(make_mutation(child2,amount_mutations,sequencial));
                    amount_generated_units += 2;
                }
            }

            Collections.sort(population);

            ++actual_it;
        }

        System.out.println("\nGERAÇÃO ENCERRADA:\n");
        System.out.println("última geração: "+actual_it+" : melhor distancia = "+
                best_route.distance+
                " : quantidade de individuos gerados = "+amount_generated_units+
                " : vezes que evoluiu = "+evolution_counter+
                " : ordem das cidades = "+best_route.getCity_order());
    }

    static Route make_child(Route route_left, Route route_right){
        if(route_left.getCity_order().size() != route_right.getCity_order().size())
            return null;

        int middle = route_left.getCity_order().size() / 2;

        Random r = new Random();

        ArrayList<City> cities = new ArrayList<>();

        for (int i = 0; i < route_left.getCity_order().size(); i++) {
            if (i <= middle){
                if(cities.contains(route_left.getCity_order().get(i))){
                    if(cities.contains(route_right.getCity_order().get(i))){
                        boolean contains = true;

                        while (contains){
                            City test = route_left.getCity_order().get(r.nextInt(route_left.getCity_order().size()));
                            if (!cities.contains(test)){
                                contains = false;
                                cities.add(test);
                            }else{
                                test = route_right.getCity_order().get(r.nextInt(route_right.getCity_order().size()));
                                if (!cities.contains(test)){
                                    contains = false;
                                    cities.add(test);
                                }
                            }
                        }
                    }else{
                        cities.add(route_right.getCity_order().get(i));
                    }
                }else{
                    cities.add(route_left.getCity_order().get(i));
                }
            }else{
                if(cities.contains(route_right.getCity_order().get(i))){
                    if(cities.contains(route_left.getCity_order().get(i))){
                        boolean contains = true;

                        while (contains){
                            City test = route_right.getCity_order().get(r.nextInt(route_right.getCity_order().size()));
                            if (!cities.contains(test)){
                                contains = false;
                                cities.add(test);
                            }else{
                                test = route_left.getCity_order().get(r.nextInt(route_left.getCity_order().size()));
                                if (!cities.contains(test)){
                                    contains = false;
                                    cities.add(test);
                                }
                            }
                        }
                    }else{
                        cities.add(route_left.getCity_order().get(i));
                    }
                }else{
                    cities.add(route_right.getCity_order().get(i));
                }
            }
        }


        return new Route(cities);
    }

    static Route make_mutation(Route route, int amount, boolean is_sequencial){
        ArrayList<City> cities = new ArrayList<>();
        cities.addAll(route.getCity_order());

        if(cities.size() < 2)
            return new Route(cities);

        Random r = new Random();

        int i = 0;
        while (i < amount) {
            int n1 = r.nextInt(cities.size());
            int n2 = n1 + 1;

            if(n2 > route.getCity_order().size()-1)
                n2 = 0;

            if(!is_sequencial){
                n2 = r.nextInt(cities.size());
            }

            if(n1 != n2){
                City c1 = cities.get(n1);
                City c2 = cities.get(n2);

                cities.remove(n1);
                cities.add(n1,c2);
                cities.remove(n2);
                cities.add(n2,c1);

                i++;
            }
        }

        return new Route(cities);
    }

    static Route get_greedy_route(ArrayList<City> cities){
        ArrayList<City> cities_visited = new ArrayList();

        ArrayList<City> cities_avaible = new ArrayList<>();

        cities_avaible.addAll(cities);

        cities_visited.add(cities_avaible.get(0));
        cities_avaible.remove(0);

        int index_cities_avaible = 0;

        while(cities_avaible.size() > 0){
            double min_distance = 99999999999d;
            int index_of_city = -1;
            for (int i = 0; i < cities_avaible.size(); i++) {
                double actual_distance = cities_visited.get(index_cities_avaible).get_distance(cities_avaible.get(i));
                if(actual_distance < min_distance){
                    min_distance = actual_distance;
                    index_of_city = i;
                }
            }
            cities_visited.add(cities_avaible.get(index_of_city));
            cities_avaible.remove(index_of_city);
            ++index_cities_avaible;
        }

        return new Route(cities_visited);
    }

    static ArrayList<City> loadCities(String filename){
        ArrayList<City> list = new ArrayList<>();

        Scanner sc = null;
        File file = new File(filename);
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.err.println("file " + filename + " not found");
            System.exit(2);
        }

        sc.nextLine();

        while(sc.hasNext()){
            String[] data = sc.nextLine().split(" ");

            Double x = Double.parseDouble(data[0]);
            Double y = Double.parseDouble(data[1]);
            String name =data[2];

            City c = new City(name,x,y);
            list.add(c);
        }

        return list;
    }


}
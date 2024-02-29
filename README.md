# Genetic Algorithm solution for Traveling Salesman Problem variation 
you can see the problem description [here](extsp_1.pdf)

## how to execute
you have to compile the java files (Main,City,Route) with the comand line below on **/src** directory 
```bash
   javac *.java 
```

then, you can run the algorithm in three different ways:
- run file entry from scratch
```bash
    java Main <file_path>
```
- run file entry with a checkpoint file (generated doing the first method)
```bash
    java Main <file_path> <checkpoint_file_path>
```
- run the instructions to execute in comand line (will print a string explaining how to manipulate and execute)
```bash
    java Main -h
```

you can read more about the traveling salesman problem [here](https://pt.wikipedia.org/wiki/Problema_do_caixeiro-viajante#:~:text=O%20problema%20do%20caixeiro%2Dviajante,retornando%20%C3%A0%20cidade%20de%20origem.)

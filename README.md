Voronoi Hospital — PGL Project ING1-GI CYTech 2025-2026

A hospital management application based on the Voronoi diagram and Delaunay triangulation. 
Each hospital defines an influence zone in which patients are automatically assigned to it.
If a hospital is saturated, the patient is redirected to the next closest available one.

## Installation

```bash
git clone https://github.com/anailafah/ProjetGenieLogicielGI5-D.git
cd ProjetGenieLogicielGI5-D
```
## to run the application :
Graphical interface

```bash
mvn compile
mvn javafx:run
```
Command line interface

```bash
mvn compile
mvn exec:java -Dexec.mainClass="CommandLineMain"
```

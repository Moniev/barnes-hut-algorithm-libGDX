# Barnes-Hut Simulation

This project simulates gravitational interactions of particles in a two-dimensional space using the Barnes-Hut algorithm, an efficient approximation method for N-body simulations. Instead of computing gravitational forces between all particle pairs, which has an O(N²) complexity, the Barnes-Hut algorithm groups distant particles into clusters and approximates their collective gravitational effect. This is achieved using a quadtree, which hierarchically divides the 2D simulation space into square regions, enabling fast force calculations. The algorithm is ideal for simulating large-scale systems, such as planetary systems or galaxy dynamics, in two dimensions. Main problem is java's garbage collector.

## Examples
![Simulation Image](https://imgur.com/J9AfgSW.jpg)
![Simulation Image](https://imgur.com/0jsq9Bq.jpg)

The quadtree structure significantly reduces the computational cost of force calculations, making it possible to simulate thousands of particles efficiently. By organizing particles into a hierarchical grid, the system minimizes the number of distance calculations required, achieving a complexity of O(N log N).

## Features
- **Quadtree-based spatial partitioning**: Reduces computational complexity from O(N²) to O(N log N) for force calculations.
- **Barnes-Hut approximation**: Efficiently handles gravitational interactions by clustering distant particles.
- **Scalable for large systems**: Capable of simulating over 30,000 particles smoothly in 2D.
- **Collisions of bodies beings**: Resolved between bodies being present in the same nodes of the tree.

## Installation

- **Install libGDX to build the Maven project.**
- **Clone the libgdx-maven-archetype repository:**
  - `git clone git://github.com/libgdx/libgdx-maven-archetype.git`
  - `cd libgdx-maven-archetype`
  - `mvn install`
- **Clone project repository:**
  - `git clone https://github.com/Moniev/barnes-hut-libGDX`
  - `cd barnes-hut-libGDX`
- **Run the Maven clean and install command:** (Also starts program after successful installation)
  - `mvn clean install -Pdesktop`
- **After the above steps, you should be able to run the program with:**
  - `mvn integration-test -Pdesktop`


package com.example.tictactoe;

import java.util.Random;

public class GameOfLife {
    private int width;
    private int height;
    private boolean[][] grid;
    private boolean[][] previousGrid;
    private int iteration;
    private Random random;

    public GameOfLife(int width, int height, float density) {
        this.width = width;
        this.height = height;
        this.grid = new boolean[height][width];
        this.previousGrid = new boolean[height][width];
        this.random = new Random();
        this.iteration = 0;
        reset(density);
    }

    public void reset(float density) {
        iteration = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = random.nextFloat() < density;
            }
        }
    }

    public int nextGeneration() {
        // Sauvegarder l'état précédent pour détection de périodicité
        for (int y = 0; y < height; y++) {
            System.arraycopy(grid[y], 0, previousGrid[y], 0, width);
        }

        boolean[][] newGrid = new boolean[height][width];
        boolean hasChanged = false;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int liveNeighbors = countLiveNeighbors(x, y);

                // Appliquer les règles du jeu de la vie
                if (grid[y][x]) {
                    // Cellule vivante
                    newGrid[y][x] = liveNeighbors == 2 || liveNeighbors == 3;
                } else {
                    // Cellule morte
                    newGrid[y][x] = liveNeighbors == 3;
                }

                if (newGrid[y][x] != grid[y][x]) {
                    hasChanged = true;
                }
            }
        }

        grid = newGrid;
        iteration++;

        // Vérifier les conditions d'arrêt
        if (!hasChanged) {
            return iteration; // Configuration statique
        }

        if (iteration > 1 && checkPeriodicity()) {
            return iteration; // Configuration périodique
        }

        if (iteration >= 1000) {
            return 1000; // Limite d'itérations atteinte
        }

        return -1; // Continuer
    }

    private int countLiveNeighbors(int x, int y) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;

                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height && grid[ny][nx]) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean checkPeriodicity() {
        // Vérifier si la configuration actuelle est égale à une précédente
        // (périodicité de période 1)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] != previousGrid[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean[][] getGrid() {
        return grid;
    }

    public int getIteration() {
        return iteration;
    }
}
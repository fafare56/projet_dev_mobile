package com.example.tictactoe;

import java.util.Random;

public class AI {
    public char[][] grille;

    public AI(char[][] grille) {
        this.grille = grille;
    }

    public int[] mouvementIA() {
        Random rd = new Random();
        int x, y;

        // Rechercher un mouvement valide (case vide)
        do {
            x = rd.nextInt(grille.length); // Choisir une colonne aléatoire
            y = rd.nextInt(grille.length); // Choisir une ligne aléatoire
        } while (grille[y][x] != ' '); // Refaire le tirage si la case est déjà prise

        return new int[]{x, y};
    }
}

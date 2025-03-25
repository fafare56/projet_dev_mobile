package com.example.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI
{
    private char[][] grille;
    private char symboleIA = 'O';
    private char symboleJoueur = 'X';

    public AI(char[][] grille)
    {
        this.grille = grille;
    }

    public int[] mouvementIA() {
        System.out.println("IA joue...");
        System.out.println("L'IA cherche un mouvement...");

        List<int[]> coupsPossibles = new ArrayList<>();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (grille[y][x] == ' ') {  // Vérifie si la case est vide
                    coupsPossibles.add(new int[]{x, y});
                }
            }
        }

        System.out.println("Coups possibles pour l'IA : " + coupsPossibles.size());

        if (coupsPossibles.isEmpty()) {
            System.out.println("L'IA ne peut plus jouer, plateau plein.");
            return null;  // Partie terminée
        }

        // Choix aléatoire d'un coup parmi ceux disponibles
        Random rand = new Random();
        int[] coupChoisi = coupsPossibles.get(rand.nextInt(coupsPossibles.size()));

        int x = coupChoisi[0];
        int y = coupChoisi[1];

        System.out.println("L'IA joue en : " + x + "," + y);

        if (grille[y][x] != ' ') {
            System.out.println("ERREUR : L'IA essaie de jouer sur une case occupée !");
            return null;  // Ne pas jouer si la case est occupée (ce qui ne devrait jamais arriver)
        }

        // Met à jour le plateau avec le coup de l'IA
        grille[y][x] = 'O';
        System.out.println("IA a joué en (" + x + "," + y + ")");

        // Afficher l'état du board après le coup
        //afficherBoard();

        return coupChoisi;
    }


    /**
     * Vérifie si un joueur peut gagner au prochain coup.
     * Si oui, retourne les coordonnées du coup gagnant.
     */
    private int[] trouverCoupGagnant(char joueur)
    {
        for (int i = 0; i < grille.length; i++)
            for (int j = 0; j < grille.length; j++)
                if (grille[i][j] == ' ')
                {
                    grille[i][j] = joueur;
                    if (verifierGagnant(joueur))
                    {
                        grille[i][j] = ' ';
                        return new int[]{j, i};
                    }
                    grille[i][j] = ' ';
                }
        return null;
    }

    /**
     * Cherche un coup stratégique (centre ou coins)
     */
    private int[] coupStratégique()
    {
        int milieu = grille.length / 2;

        // jouer au centre si possible
        if (grille[milieu][milieu] == ' ')
            return new int[]{milieu, milieu};

        // sinon jouer dans un des coins
        int[][] coins = {{0, 0}, {0, grille.length - 1}, {grille.length - 1, 0}, {grille.length - 1, grille.length - 1}};
        for (int[] coin : coins)
            if (grille[coin[1]][coin[0]] == ' ')

                return new int[]{coin[0], coin[1]};
        return null;
    }

    /**
     * Retourne un mouvement aléatoire si aucun coup stratégique n'est trouvé.
     */
    private int[] mouvementAleatoire()
    {
        for (int i = 0; i < grille.length; i++)
            for (int j = 0; j < grille.length; j++)
                if (grille[i][j] == ' ')
                    return new int[]{j, i};
        return null;
    }

    /**
     * Vérifie si un joueur a gagné (même logique que dans TicTacToeView).
     */
    private boolean verifierGagnant(char joueur)
    {
        int taille = grille.length;

        for (int i = 0; i < taille; i++)
            if (verifierAlignement(grille[i], joueur) || verifierAlignement(getColonne(i), joueur))
                return true;
        return verifierAlignement(getDiagonalePrincipale(), joueur) || verifierAlignement(getDiagonaleSecondaire(), joueur);
    }

    private boolean verifierAlignement(char[] ligne, char joueur)
    {
        int count = 0;
        for (char cell : ligne)
        {
            count = (cell == joueur) ? count + 1 : 0;
            if (count == grille.length) return true;
        }
        return false;
    }

    private char[] getColonne(int index)
    {
        char[] colonne = new char[grille.length];
        for (int i = 0; i < grille.length; i++)
            colonne[i] = grille[i][index];
        return colonne;
    }

    private char[] getDiagonalePrincipale()
    {
        char[] diagonale = new char[grille.length];
        for (int i = 0; i < grille.length; i++)
            diagonale[i] = grille[i][i];
        return diagonale;
    }

    private char[] getDiagonaleSecondaire()
    {
        char[] diagonale = new char[grille.length];
        for (int i = 0; i < grille.length; i++)
            diagonale[i] = grille[i][grille.length - i - 1];
        return diagonale;
    }
}

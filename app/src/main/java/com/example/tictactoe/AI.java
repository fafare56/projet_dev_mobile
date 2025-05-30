
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

    public void setGrille(char[][] nouvelleGrille)
    {
        this.grille = nouvelleGrille;
    }

    public int[] mouvementIA()
    {
        // 1. Vérifier le coup gagnant
        int[] coupGagnant = trouverCoupGagnant(symboleIA);
        if (coupGagnant != null) return coupGagnant;

        // 2. Vérifier le coup bloquant
        int[] coupBloquant = trouverCoupGagnant(symboleJoueur);
        if (coupBloquant != null) return coupBloquant;

        // 3. Jouer stratégiquement
        int[] coupStrategique = coupStrategique();
        if (coupStrategique != null) return coupStrategique;

        // 4. Mouvement aléatoire
        return mouvementAleatoire();
    }

    private int[] coupStrategique()
    {
        int milieu = grille.length / 2;

        // jouer au centre si possible
        if (grille[milieu][milieu] == ' ')
            return new int[]{milieu, milieu};

        // sinon jouer dans un des coins
        int[][] coins = {{0, 0}, {0, grille.length - 1},
                {grille.length - 1, 0}, {grille.length - 1, grille.length - 1}};
        for (int[] coin : coins)
            if (grille[coin[1]][coin[0]] == ' ')
                return new int[]{coin[0], coin[1]};
        return null;
    }

    public int[] mouvementAleatoire()
    {
        List<int[]> casesLibres = new ArrayList<>();

        for (int i = 0; i < grille.length; i++)
            for (int j = 0; j < grille.length; j++)
                if (grille[i][j] == ' ')
                    casesLibres.add(new int[]{j, i});

        if (casesLibres.isEmpty())
            return null;

        Random random = new Random();
        return casesLibres.get(random.nextInt(casesLibres.size()));
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

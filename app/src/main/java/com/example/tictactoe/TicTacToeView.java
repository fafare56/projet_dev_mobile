package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class TicTacToeView extends View
{
    private Paint paint;
    private char[][] board;
    private char currentPlayer;
    private AI iaJeu;
    private boolean gameOver = false;
    private boolean isPlayerTurn = true; // Flag pour savoir si c'est le tour du joueur


    private static int TAILLEGRILLE = 3;
    private static final int ALIGNEMENT_VICTOIRE = TAILLEGRILLE;

    public TicTacToeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(10);

        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        currentPlayer = 'X';
        resetBoard();

        iaJeu = new AI(board);
    }

    public void resetBoard() {
        // Réinitialiser le plateau
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                board[y][x] = ' ';  // Vide la grille
            }
        }

        // Remettre la main au joueur
        isPlayerTurn = true;  // IMPORTANT : S'assurer que c'est au joueur de commencer

        System.out.println("Le jeu a été réinitialisé. C'est au joueur de jouer.");
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int cellSize = width / TAILLEGRILLE;

        // Réduire l'épaisseur des lignes de la grille
        paint.setStrokeWidth(8);

        // Dessiner les lignes de la grille
        for (int i = 1; i < TAILLEGRILLE; i++) {
            int pos = i * cellSize;
            canvas.drawLine(pos, 0, pos, height, paint);
            canvas.drawLine(0, pos, width, pos, paint);
        }

        // Augmenter la taille du texte pour les symboles X et O
        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(150);  // Augmenter la taille du texte
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Dessiner les X et O dans chaque case
        for (int i = 0; i < TAILLEGRILLE; i++) {
            for (int j = 0; j < TAILLEGRILLE; j++) {
                if (board[i][j] != ' ') {
                    float x = j * cellSize + cellSize / 2f;
                    float y = i * cellSize + cellSize / 2f + 35;
                    canvas.drawText(String.valueOf(board[i][j]), x, y, textPaint);
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver || !isPlayerTurn) return true;  // Empêcher l'utilisateur de jouer si ce n'est pas son tour

        int width = getWidth();
        int cellSize = width / TAILLEGRILLE;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int xPos = (int) event.getX() / cellSize;
            int yPos = (int) event.getY() / cellSize;

            if (board[yPos][xPos] == ' ') {
                // Marquer la case avec le coup du joueur
                board[yPos][xPos] = currentPlayer;
                invalidate();

                // Vérifier si le joueur a gagné
                if (verifierGagnant(currentPlayer)) {
                    afficherVainqueur(currentPlayer);
                    return true;
                }

                // Changer de joueur
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

                // Si c'est le tour de l'IA, faire jouer l'IA dans un thread séparé
                if (currentPlayer == 'O') {
                    isPlayerTurn = false; // Bloquer l'utilisateur pendant que l'IA joue
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            jouerIA();
                        }
                    }).start();
                }
            }
        }
        return true;
    }




    public void setTailleGrille(int taille)
    {
        TAILLEGRILLE = taille;
        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        resetBoard();
    }


    private void jouerIA() {
        if (gameOver) return;

        System.out.println("IA joue...");

        int[] mouvement = iaJeu.mouvementIA();

        if (mouvement == null) {
            System.out.println("L'IA ne trouve aucun coup possible !");
            isPlayerTurn = true;
            return;
        }

        int x = mouvement[0];
        int y = mouvement[1];

        System.out.println("L'IA veut jouer en (" + x + "," + y + ")");

        // Vérifier si la case est bien vide
        if (board[y][x] != ' ') {
            System.out.println("ERREUR : L'IA essaie de jouer sur une case occupée !");
            return;
        }

        // Appliquer le coup
        board[y][x] = 'O';

        post(new Runnable() {
            @Override
            public void run() {
                System.out.println("IA a joué en (" + x + "," + y + ")");
                invalidate();

                if (verifierGagnant('O')) {
                    afficherVainqueur('O');
                    return;
                }

                currentPlayer = 'X';
                isPlayerTurn = true;
                System.out.println("Tour du joueur.");
            }
        });
    }





    private boolean verifierGagnant(char joueur)
    {
        for (int i = 0; i < TAILLEGRILLE; i++)
            if (verifierAlignement(board[i], joueur) || verifierAlignement(getColonne(i), joueur))
                return true;
        return verifierAlignement(getDiagonalePrincipale(), joueur) || verifierAlignement(getDiagonaleSecondaire(), joueur);
    }

    private boolean verifierAlignement(char[] ligne, char joueur)
    {
        int count = 0;
        for (char cell : ligne)
        {
            count = (cell == joueur) ? count + 1 : 0;
            if (count == ALIGNEMENT_VICTOIRE) return true;
        }
        return false;
    }

    private char[] getColonne(int index)
    {
        char[] colonne = new char[TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++)
            colonne[i] = board[i][index];
        return colonne;
    }

    private char[] getDiagonalePrincipale()
    {
        char[] diagonale = new char[TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++)
            diagonale[i] = board[i][i];
        return diagonale;
    }

    private char[] getDiagonaleSecondaire()
    {
        char[] diagonale = new char[TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++)
            diagonale[i] = board[i][TAILLEGRILLE - i - 1];
        return diagonale;
    }

    private void afficherVainqueur(char gagnant)
    {
        gameOver = true;
        Toast.makeText(getContext(), "Le joueur " + gagnant + " a gagné !", Toast.LENGTH_LONG).show();
        postDelayed(this::resetBoard, 2000);
    }
}

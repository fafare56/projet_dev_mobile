package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.Arrays;

public class TicTacToeView extends View {
    private Paint paint;
    private char[][] board;
    private char currentPlayer;
    private AI iaJeu;
    private boolean gameOver = false;

    private static int TAILLEGRILLE = 3;
    private static final int ALIGNEMENT_VICTOIRE = TAILLEGRILLE;

    public TicTacToeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(10);

        // Initialisation du tableau board
        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        currentPlayer = 'X';
        resetBoard();  // Initialiser le plateau

        iaJeu = new AI(board);
    }

    public void resetBoard() {
        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++) {
            for (int j = 0; j < TAILLEGRILLE; j++) {
                board[i][j] = ' ';
            }
        }
        currentPlayer = 'X';
        gameOver = false;
        invalidate();
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
        if (gameOver) return true;

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

                // Si c'est le tour de l'IA, faire jouer l'IA
                if (currentPlayer == 'O') {
                    jouerIA();
                }
            }
        }
        return true;
    }

    public void setTailleGrille(int taille) {
        TAILLEGRILLE = taille;
        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        resetBoard();
        iaJeu = new AI(board); // Réinitialiser l'IA avec le nouveau plateau
    }

    private void jouerIA() {
        if (gameOver) return;

        // Mettre à jour le plateau de l'IA
        iaJeu.setGrille(board);

        new Thread(() -> {
            int[] mouvement = iaJeu.mouvementIA();
            if (mouvement == null) {
                // Aucun mouvement possible (plateau plein)
                return;
            }

            int x = mouvement[0];
            int y = mouvement[1];

            // Vérifier à nouveau que la case est libre (au cas où)
            if (board[y][x] != ' ') {
                // Si la case n'est pas libre, trouver une autre case
                mouvement = iaJeu.mouvementAleatoire();
                if (mouvement == null) return;
                x = mouvement[0];
                y = mouvement[1];
            }

            // Marquer la case
            board[y][x] = 'O';

            post(() -> {
                invalidate();
                if (verifierGagnant('O')) {
                    afficherVainqueur('O');
                } else if (estMatchNul()) {
                    afficherMatchNul();
                } else {
                    currentPlayer = 'X';
                }
            });
        }).start();
    }

    private boolean estMatchNul() {
        for (int i = 0; i < TAILLEGRILLE; i++) {
            for (int j = 0; j < TAILLEGRILLE; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void afficherMatchNul() {
        gameOver = true;
        Toast.makeText(getContext(), "Match nul !", Toast.LENGTH_LONG).show();
        postDelayed(this::resetBoard, 2000);
    }

    private boolean verifierGagnant(char joueur) {
        for (int i = 0; i < TAILLEGRILLE; i++) {
            if (verifierAlignement(board[i], joueur) || verifierAlignement(getColonne(i), joueur)) {
                return true;
            }
        }
        return verifierAlignement(getDiagonalePrincipale(), joueur) || verifierAlignement(getDiagonaleSecondaire(), joueur);
    }

    private boolean verifierAlignement(char[] ligne, char joueur) {
        int count = 0;
        for (char cell : ligne) {
            count = (cell == joueur) ? count + 1 : 0;
            if (count == ALIGNEMENT_VICTOIRE) return true;
        }
        return false;
    }

    private char[] getColonne(int index) {
        char[] colonne = new char[TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++) {
            colonne[i] = board[i][index];
        }
        return colonne;
    }

    private char[] getDiagonalePrincipale() {
        char[] diagonale = new char[TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++) {
            diagonale[i] = board[i][i];
        }
        return diagonale;
    }

    private char[] getDiagonaleSecondaire() {
        char[] diagonale = new char[TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++) {
            diagonale[i] = board[i][TAILLEGRILLE - i - 1];
        }
        return diagonale;
    }

    private void afficherVainqueur(char gagnant) {
        gameOver = true;
        Toast.makeText(getContext(), "Le joueur " + gagnant + " a gagné !", Toast.LENGTH_LONG).show();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                resetBoard();
            }
        }, 2000);
    }
}

package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class TicTacToeView extends View {
    private char[][] board;
    private char currentPlayer;
    private AI iaJeu;
    private boolean gameOver = false;

    private Paint gridPaint;
    private Paint xPaint;
    private Paint oPaint;
    private float strokeWidth = 12f;

    private int TAILLEGRILLE = 3; // Valeur par défaut
    private ScoreUpdateListener scoreUpdateListener;

    public TicTacToeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGame();
    }

    private void initGame() {
        // Initialisation des Paint
        gridPaint = new Paint();
        gridPaint.setColor(Color.WHITE);
        gridPaint.setStrokeWidth(strokeWidth);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);

        xPaint = new Paint(gridPaint);
        xPaint.setStrokeWidth(strokeWidth * 1.5f);
        xPaint.setColor(Color.WHITE);

        oPaint = new Paint(gridPaint);
        oPaint.setStrokeWidth(strokeWidth * 1.2f);
        oPaint.setColor(Color.WHITE);
        oPaint.setStyle(Paint.Style.STROKE);

        // Initialisation du plateau
        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        resetBoard();
        iaJeu = new AI(board);
    }

    public void resetBoard() {
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

        if (board == null) {
            Log.e("TicTacToe", "Board is null in onDraw");
            return;
        }

        int width = getWidth();
        int height = getHeight();
        int margin = (int)(width * 0.1f);
        int gridSize = Math.min(width, height) - 2 * margin;
        int cellSize = gridSize / TAILLEGRILLE;

        // Dessin de la grille
        for (int i = 1; i < TAILLEGRILLE; i++) {
            int pos = margin + i * cellSize;
            canvas.drawLine(pos, margin, pos, margin + gridSize, gridPaint);
            canvas.drawLine(margin, pos, margin + gridSize, pos, gridPaint);
        }

        // Dessin des symboles
        for (int i = 0; i < TAILLEGRILLE; i++) {
            for (int j = 0; j < TAILLEGRILLE; j++) {
                if (i >= board.length || j >= board[i].length) continue;

                int centerX = margin + j * cellSize + cellSize / 2;
                int centerY = margin + i * cellSize + cellSize / 2;
                int radius = (int)(cellSize * 0.4f);

                if (board[i][j] == 'X') {
                    canvas.drawLine(centerX - radius, centerY - radius,
                            centerX + radius, centerY + radius, xPaint);
                    canvas.drawLine(centerX + radius, centerY - radius,
                            centerX - radius, centerY + radius, xPaint);
                } else if (board[i][j] == 'O') {
                    canvas.drawCircle(centerX, centerY, radius, oPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver || currentPlayer != 'X') return true;

        int width = getWidth();
        int margin = (int)(width * 0.1f);
        int gridSize = width - 2 * margin;
        int cellSize = gridSize / TAILLEGRILLE;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            // Vérifier que le toucher est dans la grille
            if (x < margin || x > margin + gridSize ||
                    y < margin || y > margin + gridSize) {
                return true;
            }

            int xPos = (x - margin) / cellSize;
            int yPos = (y - margin) / cellSize;

            if (xPos >= 0 && xPos < TAILLEGRILLE &&
                    yPos >= 0 && yPos < TAILLEGRILLE &&
                    board[yPos][xPos] == ' ') {

                board[yPos][xPos] = currentPlayer;
                invalidate();

                if (verifierGagnant(currentPlayer)) {
                    afficherVainqueur(currentPlayer);
                } else if (estMatchNul()) {
                    afficherMatchNul();
                } else {
                    currentPlayer = 'O';
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
            if (count == TAILLEGRILLE) return true; // Utilisation de TAILLEGRILLE au lieu de ALIGNEMENT_VICTOIRE
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
        if (scoreUpdateListener != null) {
            scoreUpdateListener.onScoreUpdated(gagnant);
        }

        Toast.makeText(getContext(), "Le joueur " + gagnant + " a gagné !", Toast.LENGTH_LONG).show();
        postDelayed(this::resetBoard, 2000);
    }

    public interface ScoreUpdateListener {
        void onScoreUpdated(char winner);
    }

    public void setScoreUpdateListener(ScoreUpdateListener listener) {
        this.scoreUpdateListener = listener;
    }


}

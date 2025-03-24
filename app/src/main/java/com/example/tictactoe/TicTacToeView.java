package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TicTacToeView extends View {
    private Paint paint;
    private char[][] board;
    private char currentPlayer;
    private AI iaJeu;
    private static int TAILLEGRILLE = 5;
    private static int ALIGNEMENTGAGNANT = TAILLEGRILLE; // Nombre de symboles alignés pour gagner

    public TicTacToeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(0xFF000000);
        paint.setStrokeWidth(10);

        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        currentPlayer = 'X';

        for (int i = 0; i < TAILLEGRILLE; i++) {
            for (int j = 0; j < TAILLEGRILLE; j++) {
                board[i][j] = ' ';
            }
        }

        iaJeu = new AI(board);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int cellSize = width / TAILLEGRILLE;

        for (int i = 1; i < TAILLEGRILLE; i++) {
            int pos = i * cellSize;
            canvas.drawLine(pos, 0, pos, height, paint);
            canvas.drawLine(0, pos, width, pos, paint);
        }

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(100);

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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int cellSize = getWidth() / TAILLEGRILLE;
            int xPos = (int) event.getX() / cellSize;
            int yPos = (int) event.getY() / cellSize;

            if (board[yPos][xPos] == ' ') {
                board[yPos][xPos] = currentPlayer;
                if (verifierGagnant(yPos, xPos)) {
                    // Gagnant détecté, on peut ajouter une logique pour afficher le vainqueur
                    return true;
                }
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                invalidate();

                if (currentPlayer == 'O') {
                    jouerIA();
                }
            }
        }
        return true;
    }

    private void jouerIA() {
        int[] mouvement = iaJeu.mouvementIA();
        int x = mouvement[0];
        int y = mouvement[1];

        while (board[y][x] != ' ') {
            mouvement = iaJeu.mouvementIA();
            x = mouvement[0];
            y = mouvement[1];
        }

        board[y][x] = 'O';
        if (!verifierGagnant(y, x)) {
            currentPlayer = 'X';
        }
        invalidate();
    }

    private boolean verifierGagnant(int row, int col) {
        char symbol = board[row][col];
        return verifierDirection(row, col, 1, 0, symbol) || // Horizontal
                verifierDirection(row, col, 0, 1, symbol) || // Vertical
                verifierDirection(row, col, 1, 1, symbol) || // Diagonale descendante
                verifierDirection(row, col, 1, -1, symbol);  // Diagonale montante
    }

    private boolean verifierDirection(int row, int col, int deltaRow, int deltaCol, char symbol) {
        int count = 1;
        count += compterAlignes(row, col, deltaRow, deltaCol, symbol);
        count += compterAlignes(row, col, -deltaRow, -deltaCol, symbol);
        return count >= ALIGNEMENTGAGNANT;
    }

    private int compterAlignes(int row, int col, int deltaRow, int deltaCol, char symbol) {
        int count = 0;
        int i = row + deltaRow;
        int j = col + deltaCol;
        while (i >= 0 && i < TAILLEGRILLE && j >= 0 && j < TAILLEGRILLE && board[i][j] == symbol) {
            count++;
            i += deltaRow;
            j += deltaCol;
        }
        return count;
    }
}
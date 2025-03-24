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

    public void resetBoard()
    {
        board = new char[TAILLEGRILLE][TAILLEGRILLE];
        for (int i = 0; i < TAILLEGRILLE; i++)
            for (int j = 0; j < TAILLEGRILLE; j++)
                board[i][j] = ' ';
        currentPlayer = 'X';
        gameOver = false;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int cellSize = width / TAILLEGRILLE;

        for (int i = 1; i < TAILLEGRILLE; i++)
        {
            int pos = i * cellSize;
            canvas.drawLine(pos, 0, pos, height, paint);
            canvas.drawLine(0, pos, width, pos, paint);
        }

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < TAILLEGRILLE; i++)
            for (int j = 0; j < TAILLEGRILLE; j++)
                if (board[i][j] != ' ')
                {
                    float x = j * cellSize + cellSize / 2f;
                    float y = i * cellSize + cellSize / 2f + 35;
                    canvas.drawText(String.valueOf(board[i][j]), x, y, textPaint);
                }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (gameOver) return true;

        int width = getWidth();
        int cellSize = width / TAILLEGRILLE;

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            int xPos = (int) event.getX() / cellSize;
            int yPos = (int) event.getY() / cellSize;

            if (board[yPos][xPos] == ' ')
            {
                board[yPos][xPos] = currentPlayer;
                invalidate();

                if (verifierGagnant(currentPlayer))
                {
                    afficherVainqueur(currentPlayer);
                    return true;
                }

                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

                if (currentPlayer == 'O')
                    jouerIA();
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


    private void jouerIA()
    {
        if (gameOver) return;

        int[] mouvement = iaJeu.mouvementIA();
        int x = mouvement[0];
        int y = mouvement[1];

        while (board[y][x] != ' ')
        {
            mouvement = iaJeu.mouvementIA();
            x = mouvement[0];
            y = mouvement[1];
        }

        board[y][x] = 'O';
        invalidate();

        if (verifierGagnant('O'))
        {
            afficherVainqueur('O');
            return;
        }

        currentPlayer = 'X';
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

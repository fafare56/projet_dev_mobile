package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameOfLifeView extends View {
    private GameOfLife game;
    private Paint alivePaint;
    private Paint deadPaint;
    private Paint gridPaint;

    public GameOfLifeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init() ;
    }

    public GameOfLifeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        alivePaint = new Paint();
        alivePaint.setColor(Color.BLACK);

        deadPaint = new Paint();
        deadPaint.setColor(Color.WHITE);

        gridPaint = new Paint();
        gridPaint.setColor(Color.GRAY);
        gridPaint.setStrokeWidth(1);
    }

    public void setGame(GameOfLife game) {
        this.game = game;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (game == null) return;

        boolean[][] grid = game.getGrid();
        int width = getWidth();
        int height = getHeight();

        int cellSize = Math.min(width / grid[0].length, height / grid.length);

        // Dessiner les cellules
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                Paint paint = grid[y][x] ? alivePaint : deadPaint;
                canvas.drawRect(x * cellSize, y * cellSize,
                        (x + 1) * cellSize, (y + 1) * cellSize,
                        paint);
            }
        }

        // Dessiner la grille
        for (int x = 0; x <= grid[0].length; x++) {
            canvas.drawLine(x * cellSize, 0, x * cellSize, grid.length * cellSize, gridPaint);
        }
        for (int y = 0; y <= grid.length; y++) {
            canvas.drawLine(0, y * cellSize, grid[0].length * cellSize, y * cellSize, gridPaint);
        }
    }
}
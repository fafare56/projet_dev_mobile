package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GameView extends View {
    private static final float GRAVITY_SCALE = 5f; // Accélération
    private static final float MAX_VELOCITY = 10f; // Vitesse max
    private Paint paintShape, paintCursor, paintPath;
    private float cursorX, cursorY;
    private float velocityX = 0, velocityY = 0; // Vitesse de la bille
    private float centerX, centerY;
    private int canvasWidth, canvasHeight;
    private List<PointF> pathPoints = new ArrayList<>();
    private float totalDistance = 0; // Distance totale parcourue
    private float shapeLength = 0; // Longueur totale de la forme
    private int currentSegment = 0; // Segment actuel pour polygones
    private float segmentDistance = 0; // Distance dans le segment actuel
    private boolean tourCompleted = false;
    private int goodTicks = 0;
    private int totalTicks = 0;
    private int strokeWidth = 10;
    private String currentShape;
    private Path shapePath;
    private List<PointF> shapePoints = new ArrayList<>();
    private PointF lastPosition;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        paintShape = new Paint();
        paintShape.setColor(getResources().getColor(R.color.blue_clair)); // Couleur par défaut
        paintShape.setStyle(Paint.Style.STROKE);
        paintShape.setStrokeWidth(strokeWidth);

        paintCursor = new Paint();
        paintCursor.setColor(Color.RED);
        paintCursor.setStyle(Paint.Style.FILL);

        paintPath = new Paint();
        paintPath.setColor(Color.WHITE);
        paintPath.setStyle(Paint.Style.STROKE);
        paintPath.setStrokeWidth(5f);

        setBackgroundColor(getResources().getColor(R.color.blue_foncé));
    }

    public void setupGame(String difficulte, String shape) {
        switch (difficulte) {
            case "Facile": strokeWidth = 20; break;
            case "Moyen": strokeWidth = 10; break;
            case "Difficile": strokeWidth = 5; break;
        }
        paintShape.setStrokeWidth(strokeWidth);
        this.currentShape = shape;
        resetGame();
    }

    private void resetGame() {
        pathPoints.clear();
        goodTicks = 0;
        totalTicks = 0;
        totalDistance = 0;
        currentSegment = 0;
        segmentDistance = 0;
        tourCompleted = false;
        shapePoints.clear();
        velocityX = 0;
        velocityY = 0;

        if (canvasWidth > 0 && canvasHeight > 0) {
            float size = Math.min(canvasWidth, canvasHeight) / 3f;
            shapePath = new Path();

            switch (currentShape) {
                case "Cercle":
                    cursorX = centerX + size; // Début à droite
                    cursorY = centerY;
                    shapePath.addCircle(centerX, centerY, size, Path.Direction.CW);
                    shapeLength = (float) (2 * Math.PI * size);
                    break;
                case "Carré":
                    cursorX = centerX - size; // Début en haut-gauche
                    cursorY = centerY - size;
                    shapePath.moveTo(centerX - size, centerY - size);
                    shapePath.lineTo(centerX + size, centerY - size);
                    shapePath.lineTo(centerX + size, centerY + size);
                    shapePath.lineTo(centerX - size, centerY + size);
                    shapePath.close();
                    shapePoints.add(new PointF(centerX - size, centerY - size)); // 0
                    shapePoints.add(new PointF(centerX + size, centerY - size)); // 1
                    shapePoints.add(new PointF(centerX + size, centerY + size)); // 2
                    shapePoints.add(new PointF(centerX - size, centerY + size)); // 3
                    shapeLength = 4 * size * 2;
                    break;
                case "Triangle":
                    cursorX = centerX; // Début en haut
                    cursorY = centerY - size;
                    shapePath.moveTo(centerX, centerY - size);
                    shapePath.lineTo(centerX + size, centerY + size);
                    shapePath.lineTo(centerX - size, centerY + size);
                    shapePath.close();
                    shapePoints.add(new PointF(centerX, centerY - size)); // 0
                    shapePoints.add(new PointF(centerX + size, centerY + size)); // 1
                    shapePoints.add(new PointF(centerX - size, centerY + size)); // 2
                    shapeLength = (float) (3 * Math.hypot(size, size * 2));
                    break;
            }
            pathPoints.add(new PointF(cursorX, cursorY));
            lastPosition = new PointF(cursorX, cursorY);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasWidth = w;
        canvasHeight = h;
        centerX = w / 2f;
        centerY = h / 2f;
        resetGame();
    }

    public void updateCursor(float rotY, float rotX) {
        // Appliquer la gravité en continu
        float accelerationX = rotY * GRAVITY_SCALE;
        float accelerationY = rotX * GRAVITY_SCALE;

        // Ajouter l'accélération à la vitesse (pas de friction)
        velocityX += accelerationX;
        velocityY += accelerationY;

        // Limiter la vitesse maximale
        velocityX = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocityX));
        velocityY = Math.max(-MAX_VELOCITY, Math.min(MAX_VELOCITY, velocityY));

        // Pas de friction : on n'applique plus velocityX *= FRICTION ou velocityY *= FRICTION

        // Mettre à jour la position
        cursorX += velocityX;
        cursorY += velocityY;

        // Limites de l'écran
        cursorX = Math.max(0, Math.min(cursorX, canvasWidth));
        cursorY = Math.max(0, Math.min(cursorY, canvasHeight));

        pathPoints.add(new PointF(cursorX, cursorY));
        invalidate();
    }

    public void updateGameState() {
        totalTicks++;
        if (isCursorOnPath()) {
            goodTicks++;
            updateProgression();
            // Changer la couleur de la forme quand la bille est dessus
            paintShape.setColor(getResources().getColor(R.color.blue_foncé_moins));
        } else {
            // Revenir à la couleur par défaut quand la bille n'est pas dessus
            paintShape.setColor(getResources().getColor(R.color.blue_clair));
        }
    }

    private void updateProgression() {
        if (tourCompleted) return;

        float deltaX = cursorX - lastPosition.x;
        float deltaY = cursorY - lastPosition.y;
        float distanceMoved = (float) Math.hypot(deltaX, deltaY);

        if (currentShape.equals("Cercle")) {
            if (isCursorOnPath()) {
                totalDistance += distanceMoved;
                if (totalDistance >= shapeLength) {
                    tourCompleted = true;
                }
            }
        } else {
            PointF p1 = shapePoints.get(currentSegment);
            PointF p2 = shapePoints.get((currentSegment + 1) % shapePoints.size());
            float segmentLength = (float) Math.hypot(p2.x - p1.x, p2.y - p1.y);

            if (isCursorOnPath()) {
                segmentDistance += distanceMoved;
                totalDistance += distanceMoved;
                if (segmentDistance >= segmentLength) {
                    segmentDistance = 0;
                    currentSegment = (currentSegment + 1) % shapePoints.size();
                    if (currentSegment == 0 && totalDistance >= shapeLength * 0.9f) { // Tolérance à 90%
                        tourCompleted = true;
                    }
                }
            }
        }

        lastPosition.set(cursorX, cursorY);
        Log.d("GameView", "Total Distance: " + totalDistance + ", Shape Length: " + shapeLength + ", Tour Completed: " + tourCompleted);
    }

    public boolean isTourComplet() {
        return tourCompleted;
    }

    public int calculateScore() {
        if (totalTicks == 0) return 0;
        return (int) ((goodTicks * 100.0f) / totalTicks);
    }

    public String getProgression() {
        if (tourCompleted) return "100.0";
        float progress = (totalDistance / shapeLength) * 100;
        return String.format("%.1f", Math.min(progress, 100));
    }

    private boolean isCursorOnPath() {
        float size = Math.min(canvasWidth, canvasHeight) / 3f;
        if (currentShape.equals("Cercle")) {
            float distance = (float) Math.hypot(cursorX - centerX, cursorY - centerY);
            return Math.abs(distance - size) <= strokeWidth;
        } else {
            for (int i = 0; i < shapePoints.size(); i++) {
                PointF p1 = shapePoints.get(i);
                PointF p2 = shapePoints.get((i + 1) % shapePoints.size());
                if (distanceToSegment(p1, p2, new PointF(cursorX, cursorY)) <= strokeWidth) {
                    return true;
                }
            }
            return false;
        }
    }

    private float distanceToSegment(PointF p1, PointF p2, PointF p) {
        float l2 = (float) (Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        if (l2 == 0) return (float) Math.hypot(p.x - p1.x, p.y - p1.y);
        float t = Math.max(0, Math.min(1, ((p.x - p1.x) * (p2.x - p1.x) + (p.y - p1.y) * (p2.y - p1.y)) / l2));
        float projX = p1.x + t * (p2.x - p1.x);
        float projY = p1.y + t * (p2.y - p1.y);
        return (float) Math.hypot(p.x - projX, p.y - projY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(shapePath, paintShape);

        if (pathPoints.size() > 1) {
            for (int i = 1; i < pathPoints.size(); i++) {
                PointF prev = pathPoints.get(i - 1);
                PointF curr = pathPoints.get(i);
                canvas.drawLine(prev.x, prev.y, curr.x, curr.y, paintPath);
            }
        }

        canvas.drawCircle(cursorX, cursorY, 15, paintCursor);
    }
}
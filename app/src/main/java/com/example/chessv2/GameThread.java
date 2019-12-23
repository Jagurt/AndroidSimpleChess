package com.example.chessv2;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;
    Canvas canvas;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.gameSurface= gameSurface;
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run()  {
        long startTime = System.nanoTime();



        while(running)  {
            canvas= null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                // Synchronized
                synchronized (canvas) {
                    this.gameSurface.update();
                    if (gameSurface.activeSquareRef.squareRef == gameSurface.emptySquareRef.squareRef) {
                        for (int i = 0; i < 8; i++) {
                            for (int j = 0; j < 8; j++) {
                                if ((i + j + 1) % 2 == 0) {
                                    if (gameSurface.ALL_SQUARES_HITBOXES[gameSurface.widthDrawStartIndex + i][gameSurface.heightDrawStartIndex + j].squareRef.bitmap != gameSurface.white) {
                                        gameSurface.ALL_SQUARES_HITBOXES[gameSurface.widthDrawStartIndex + i][gameSurface.heightDrawStartIndex + j].squareRef.bitmap = gameSurface.white;
                                        gameSurface.ALL_SQUARES_HITBOXES[gameSurface.widthDrawStartIndex + i][gameSurface.heightDrawStartIndex + j].squareRef.setDrawSquare(true);
                                    }
                                }
                                else if (gameSurface.ALL_SQUARES_HITBOXES[gameSurface.widthDrawStartIndex + i][gameSurface.heightDrawStartIndex + j].squareRef.bitmap != gameSurface.black) {
                                    gameSurface.ALL_SQUARES_HITBOXES[gameSurface.widthDrawStartIndex + i][gameSurface.heightDrawStartIndex + j].squareRef.bitmap = gameSurface.black;
                                    gameSurface.ALL_SQUARES_HITBOXES[gameSurface.widthDrawStartIndex + i][gameSurface.heightDrawStartIndex + j].squareRef.setDrawSquare(true);
                                }
                            }
                        }
                    }
                    this.gameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                // Do nothing.
            } finally {
                if(canvas!= null)  {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime() ;
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            long waitTime = (now - startTime)/1000000;
            if(waitTime < 10)  {
                waitTime= 10; // Millisecond.
            }
            System.out.print(" Wait Time="+ waitTime);

            try {
                // Sleep.
                this.sleep(waitTime);
            } catch(InterruptedException e)  {

            }
            startTime = System.nanoTime();
            System.out.print(".");
        }
    }

    public void setRunning(boolean running)  {
        this.running= running;
    }
}

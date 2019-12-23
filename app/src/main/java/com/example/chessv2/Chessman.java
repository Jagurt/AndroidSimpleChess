package com.example.chessv2;

import android.graphics.Bitmap;

public class Chessman {

    public Bitmap bitmap;
    private boolean white;
    private char name;

    Chessman() {

    }

    Chessman(Bitmap bitmap, boolean white, char name)
    {
        this.bitmap = bitmap;
        this.white = white;
        this.name = name;
    }

    Bitmap GetBitmap()
    {
        return bitmap;
    }

    boolean IsWhite()
    {
        return this.white;
    }

    char GetName()
    {
        return this.name;
    }
}

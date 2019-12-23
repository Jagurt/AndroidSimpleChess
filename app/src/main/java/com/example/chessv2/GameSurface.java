package com.example.chessv2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    GameThread gameThread;

    int WIDTH;
    int HEIGHT;
    int bitmapSize;

    int widthDrawStartPoint;
    int heightDrawStartPoint;
    int widthDrawStartIndex;
    int heightDrawStartIndex;

    private boolean whites_turn;

    public Bitmap white;
    public Bitmap black;

    public SquareReference[][] ALL_SQUARES_HITBOXES;
    public SquareReference emptySquareRef = new SquareReference();

    public SquareReference activeSquareRef = new SquareReference();
    public SquareReference blackKingSquareRef = new SquareReference();
    public SquareReference whiteKingSquareRef = new SquareReference();

    public GameSurface(Context context)
    {
        super(context);

        this.setFocusable(true);

        this.getHolder().addCallback(this);
    }

    public  void update()
    {

    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);

        for (int i = 0; i < WIDTH / bitmapSize; i++)
        {
            for (int j = 0; j < HEIGHT / bitmapSize; j++)
            {
                ALL_SQUARES_HITBOXES[i][j].squareRef.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            int x = (int)event.getX() / bitmapSize;
            int y = (int)event.getY() / bitmapSize;

            whites_turn = ALL_SQUARES_HITBOXES[x][y].squareRef.SquareActivation(whites_turn, activeSquareRef, emptySquareRef, ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex, blackKingSquareRef, whiteKingSquareRef, gameThread.canvas, bitmapSize);
            return true;
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

        Bitmap narrow = BitmapFactory.decodeResource(this.getResources(), R.drawable.s18);
        Bitmap[] numbers = new Bitmap[8];
        numbers[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s02);
        numbers[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s03);
        numbers[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s04);
        numbers[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s05);
        numbers[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s06);
        numbers[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s07);
        numbers[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s08);
        numbers[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s09);

        Bitmap[] letters = new Bitmap[8];
        letters[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s10);
        letters[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s11);
        letters[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s12);
        letters[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s13);
        letters[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s14);
        letters[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s15);
        letters[6] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s16);
        letters[7] = BitmapFactory.decodeResource(this.getResources(), R.drawable.s17);

        Bitmap black_pawn = BitmapFactory.decodeResource(this.getResources(), R.drawable.s19);
        Bitmap black_tower = BitmapFactory.decodeResource(this.getResources(), R.drawable.s20);
        Bitmap black_knight = BitmapFactory.decodeResource(this.getResources(), R.drawable.s21);
        Bitmap black_bishop = BitmapFactory.decodeResource(this.getResources(), R.drawable.s22);
        Bitmap black_queen = BitmapFactory.decodeResource(this.getResources(), R.drawable.s23);
        Bitmap black_king = BitmapFactory.decodeResource(this.getResources(), R.drawable.s24);
        Bitmap white_pawn = BitmapFactory.decodeResource(this.getResources(), R.drawable.s25);
        Bitmap white_tower = BitmapFactory.decodeResource(this.getResources(), R.drawable.s26);
        Bitmap white_knight = BitmapFactory.decodeResource(this.getResources(), R.drawable.s27);
        Bitmap white_bishop = BitmapFactory.decodeResource(this.getResources(), R.drawable.s28);
        Bitmap white_queen = BitmapFactory.decodeResource(this.getResources(), R.drawable.s29);
        Bitmap white_king = BitmapFactory.decodeResource(this.getResources(), R.drawable.s30);
        Bitmap active = BitmapFactory.decodeResource(this.getResources(), R.drawable.s31);
        Bitmap empty = BitmapFactory.decodeResource(this.getResources(), R.drawable.s32);

        white = BitmapFactory.decodeResource(this.getResources(), R.drawable.s00);
        black = BitmapFactory.decodeResource(this.getResources(), R.drawable.s01);

        WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
        HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;

        bitmapSize = 64;
        whites_turn = true;

        widthDrawStartPoint = WIDTH/2 - bitmapSize * 4;
        heightDrawStartPoint = HEIGHT/2 - bitmapSize * 4 - HEIGHT % bitmapSize;
        widthDrawStartIndex = widthDrawStartPoint/bitmapSize;
        heightDrawStartIndex = heightDrawStartPoint/bitmapSize;

        ALL_SQUARES_HITBOXES = new SquareReference[WIDTH/bitmapSize][HEIGHT/bitmapSize];

        Chessman emptyChessman = new Chessman(empty, false, 'e');
        Chessman blackPawn = new Chessman(black_pawn, false, 'P');
        Chessman blackTower = new Chessman(black_tower, false, 'T');
        Chessman blackKnight = new Chessman(black_knight, false, 'K');
        Chessman blackBishop = new Chessman(black_bishop, false, 'B');
        Chessman blackQueen = new Chessman(black_queen, false, 'Q');
        Chessman blackKing = new Chessman(black_king, false, 'R');
        Chessman whitePawn = new Chessman(white_pawn, true, 'P');
        Chessman whiteTower = new Chessman(white_tower, true, 'T');
        Chessman whiteKnight = new Chessman(white_knight, true, 'K');
        Chessman whiteBishop = new Chessman(white_bishop, true, 'B');
        Chessman whiteQueen = new Chessman(white_queen, true, 'Q');
        Chessman whiteKing = new Chessman(white_king, true, 'R');

        for (int i = 0; i < WIDTH / bitmapSize; i++)
        {
            for (int j = 0; j < HEIGHT / bitmapSize; j++)
            {
                ALL_SQUARES_HITBOXES[i][j] = new SquareReference();
                ALL_SQUARES_HITBOXES[i][j].squareRef =  new Square(black, i * bitmapSize, j * bitmapSize, false, false,  emptyChessman);
            }
        }

        //Rysowanie szachownicy
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if ((i + j + 1) % 2 == 0)
                    ALL_SQUARES_HITBOXES[widthDrawStartIndex + i][heightDrawStartIndex + j].squareRef = new Square(white,widthDrawStartPoint + bitmapSize * i ,heightDrawStartPoint + bitmapSize*j, true, false, emptyChessman);
                else
                    ALL_SQUARES_HITBOXES[widthDrawStartIndex + i][heightDrawStartIndex + j].squareRef = new Square(black,widthDrawStartPoint + bitmapSize * i , heightDrawStartPoint + bitmapSize*j, true, false, emptyChessman);
            }
        }

        for (int i = 0; i < 8; i++) {
            ALL_SQUARES_HITBOXES[widthDrawStartIndex + i][heightDrawStartIndex - 1].squareRef = new Square(letters[i], widthDrawStartPoint + i * bitmapSize, heightDrawStartPoint - bitmapSize/2, true, false, emptyChessman);
        }
        for (int i = 0; i < 8; i++) {
            ALL_SQUARES_HITBOXES[widthDrawStartIndex + i][heightDrawStartIndex + 8].squareRef = new Square(letters[i], widthDrawStartPoint + i * bitmapSize, heightDrawStartPoint + 8 * bitmapSize, true, false, emptyChessman);
        }
        for (int i = 0; i < 8; i++){
            ALL_SQUARES_HITBOXES[widthDrawStartIndex - 1][heightDrawStartIndex + i].squareRef = new Square(numbers[i], widthDrawStartPoint - bitmapSize/2, heightDrawStartPoint + i * bitmapSize, true, false, emptyChessman);
        }
        for (int i = 0; i < 8; i++){
            ALL_SQUARES_HITBOXES[widthDrawStartIndex + 8][heightDrawStartIndex + i].squareRef = new Square(numbers[i], widthDrawStartPoint + 8 * bitmapSize, heightDrawStartPoint + i * bitmapSize, true, false, emptyChessman);
        }

        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 1][heightDrawStartIndex - 1].squareRef = new Square(narrow, widthDrawStartPoint - bitmapSize/2, heightDrawStartPoint - bitmapSize/2, true, false, emptyChessman);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 1][heightDrawStartIndex + 8].squareRef = new Square(narrow, widthDrawStartPoint - bitmapSize/2, heightDrawStartPoint + 8 * bitmapSize, true, false, emptyChessman);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 8][heightDrawStartIndex - 1].squareRef = new Square(narrow, widthDrawStartPoint + 8 * bitmapSize, heightDrawStartPoint - bitmapSize/2, true, false, emptyChessman);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 8][heightDrawStartIndex + 8].squareRef = new Square(narrow, widthDrawStartPoint + 8 * bitmapSize, heightDrawStartPoint + 8 * bitmapSize, true, false, emptyChessman);

        for (int i = 0; i < 8; i++) {
            ALL_SQUARES_HITBOXES[widthDrawStartIndex + i][heightDrawStartIndex + 1].squareRef.ReceiveChessman(blackPawn);
        }
        for (int i = 0; i < 8; i++) {
            ALL_SQUARES_HITBOXES[widthDrawStartIndex + i][heightDrawStartIndex + 6].squareRef.ReceiveChessman(whitePawn);
        }

        ALL_SQUARES_HITBOXES[widthDrawStartIndex][heightDrawStartIndex].squareRef.ReceiveChessman(blackTower);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 7][heightDrawStartIndex].squareRef.ReceiveChessman(blackTower);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 1][heightDrawStartIndex].squareRef.ReceiveChessman(blackKnight);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 6][heightDrawStartIndex].squareRef.ReceiveChessman(blackKnight);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 2][heightDrawStartIndex].squareRef.ReceiveChessman(blackBishop);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 5][heightDrawStartIndex].squareRef.ReceiveChessman(blackBishop);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 3][heightDrawStartIndex].squareRef.ReceiveChessman(blackQueen);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 4][heightDrawStartIndex].squareRef.ReceiveChessman(blackKing);
        blackKingSquareRef = new SquareReference();
        blackKingSquareRef.squareRef = ALL_SQUARES_HITBOXES[widthDrawStartIndex + 4][heightDrawStartIndex].squareRef;

        ALL_SQUARES_HITBOXES[widthDrawStartIndex][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteTower);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 7][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteTower);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 1][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteKnight);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 6][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteKnight);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 2][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteBishop);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 5][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteBishop);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 3][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteQueen);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex + 4][heightDrawStartIndex + 7].squareRef.ReceiveChessman(whiteKing);
        whiteKingSquareRef = new SquareReference();
        whiteKingSquareRef.squareRef = ALL_SQUARES_HITBOXES[widthDrawStartIndex + 4][heightDrawStartIndex + 7].squareRef;

        emptySquareRef.squareRef = new Square(active, - bitmapSize, - bitmapSize, false, false, emptyChessman);
        activeSquareRef.squareRef = emptySquareRef.squareRef;

        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 2].squareRef.ReceiveChessman(whiteTower);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 3].squareRef.ReceiveChessman(whiteKnight);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 4].squareRef.ReceiveChessman(whiteBishop);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 5].squareRef.ReceiveChessman(whiteQueen);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 2].squareRef.ReceiveChessman(blackTower);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 3].squareRef.ReceiveChessman(blackKnight);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 4].squareRef.ReceiveChessman(blackBishop);
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 5].squareRef.ReceiveChessman(blackQueen);

        Square.EndPromotion(ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex);

        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

/*int main()
    {
        //Petla gry
        while (true)
        {
            ALLEGRO_EVENT ev;
            al_wait_for_event(event_queue, &ev);

            //Wylacza sie po zamknieciu okna
            if (ev.type == ALLEGRO_EVENT_DISPLAY_CLOSE){
                break;
            }
            //Przy kliknieciu
            if (ev.type == ALLEGRO_EVENT_MOUSE_BUTTON_DOWN) {
                int active_square_x = ev.mouse.x / SQUARE_SIZE;
                int active_square_y = ev.mouse.y / SQUARE_SIZE;
                ALL_SQUARES_HITBOXES[active_square_x][active_square_y]->SquareActivation(whites_turn, active_square, emptySquareRef, ALL_SQUARES_HITBOXES, INDEX_OF_ROW_1, INDEX_OF_COLUMN_1, black_king_square, white_king_square);
                al_flip_display();
            }
        }

        //Zwalnianie pamieci
        for (int i = 0; i < ALL_SQUARES_W; i++)
        {
            for (int j = 0; j < ALL_SQUARES_H; j++)
            {
                if (ALL_SQUARES_HITBOXES[i][j])
                {
                    delete ALL_SQUARES_HITBOXES[i][j];
                }
            }
        }

        for (int i = 0; i < ALL_SQUARES_W; i++)
        {
            if (ALL_SQUARES_HITBOXES[i])
            {
                delete ALL_SQUARES_HITBOXES[i];
            }
        }

        if (ALL_SQUARES_HITBOXES)
        {
            delete ALL_SQUARES_HITBOXES;
        }

        delete empty_chessman;
        delete emptySquareRef;
        delete white_pawn;
        delete black_pawn;
        delete white_tower;
        delete black_tower;
        delete white_knight;
        delete black_knight;
        delete white_bishop;
        delete black_bishop;
        delete white_queen;
        delete black_queen;
        delete white_king;
        delete black_king;

        for (int i = 0; i < NUM_OF_BITMAPS; i++)
        {
            al_destroy_bitmap(bitmap_tab[i]);
        }
        al_destroy_bitmap(active_bitmap);
        al_destroy_display(display);
        al_destroy_event_queue(event_queue);

        return 0;
    }
*/
/*//Fukncja ladujaca odpowiednio nazwane bitmapy
    ALLEGRO_BITMAP * load_bitmaps(int bitmap_number)
    {
        char dir[20] = { 'd','a','t','a','/','b','i','t','m','a','p','s','/', '#', '#','.','p','n','g' };

        dir[13] = bitmap_number / 10 + 48;
        dir[14] = bitmap_number - (bitmap_number / 10) * 10 + 48;
        return al_load_bitmap(dir);
    }*/

}

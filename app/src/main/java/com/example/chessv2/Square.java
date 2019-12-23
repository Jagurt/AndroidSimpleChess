package com.example.chessv2;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Square {

    public Bitmap bitmap;

    private Chessman chessman;

    private int x;
    private int y;

    private boolean drawSquare;
    private boolean drawChessman;

    public Square(Bitmap bitmap, int x, int y, boolean drawSquare, boolean drawChessman, Chessman chessman) {

        this.chessman = chessman;
        this.bitmap = bitmap;

        this.x = x;
        this.y = y;

        this.drawSquare = drawSquare;
        this.drawChessman = drawChessman;
    }

    public void draw(Canvas canvas)
    {
        if (drawSquare)
        {
            canvas.drawBitmap(bitmap,x,y,null);
            if (drawChessman)
                canvas.drawBitmap(chessman.bitmap,x,y,null);
        }
    }

    public void drawSquare(Canvas canvas, Bitmap bitmap)
    {
        if (drawSquare)
        {
            canvas.drawBitmap(bitmap,x,y,null);
            drawSquare = false;
            if (drawChessman) {
                canvas.drawBitmap(chessman.bitmap, x, y, null);
                drawChessman = false;
            }
        }
    }

    boolean SquareActivation(boolean  whites_turn, SquareReference activeSquareRef, SquareReference empty_square, SquareReference[][] ALL_SQUARES_HITBOXES, int heightDrawStartIndex, int widthDrawStartIndex, SquareReference blackKingSquareRef, SquareReference whiteKingSquareRef, Canvas canvas, int bitmapSize) {

        int this_index_x = this.x / 64;
        int this_index_y = this.y / 64;

        //Sprawdzam czy kliknieto w szachownice
        if (this_index_x >= widthDrawStartIndex && this_index_x <= widthDrawStartIndex + 7 && this_index_y >= heightDrawStartIndex && this_index_y <= heightDrawStartIndex + 7) {
            // Sprawdzam jest jakis aktywny kwadrat
            if (activeSquareRef.squareRef != empty_square.squareRef) {
                //jesli klikam zaznaczonego pionka to go odznaczam
                if (activeSquareRef.squareRef.chessman == this.chessman || (this.chessman != empty_square.squareRef.chessman && this.chessman.IsWhite() == activeSquareRef.squareRef.chessman.IsWhite())) {
                    this.DeactivateSquare(activeSquareRef, empty_square);
                } else {
                    char active_name = activeSquareRef.squareRef.chessman.GetName();

                    //Sprawdzam czy krol nie bedzie zagrozony po ruchu
                    //Musze sprawdzac to różnie jesli ruszam królem, a innym pionkiem
                    // inaczej sie buguje
                    if (active_name != 'R') {
                        if (whites_turn) {
                            if (!IsKingSafeAfterMove(activeSquareRef, empty_square, whiteKingSquareRef, ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex, bitmapSize)) {
                                DeactivateSquare(activeSquareRef, empty_square);
                                return whites_turn;
                            }
                        }
                        if (!whites_turn) {
                            if (!IsKingSafeAfterMove(activeSquareRef, empty_square, blackKingSquareRef, ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex, bitmapSize)) {
                                DeactivateSquare(activeSquareRef, empty_square);
                                return whites_turn;
                            }
                        }
                    }

                    //Ruch pionka
                    if (active_name == 'P') {
                        int move_dir = (activeSquareRef.squareRef.y - this.y) / bitmapSize;
                        boolean same_collumns = (((activeSquareRef.squareRef.x - this.x) / bitmapSize) == 0);
                        boolean adjectant_collumns = Math.abs(((activeSquareRef.squareRef.x - this.x) / bitmapSize )) == 1;

                        int move_target = 1;
                        int starting_row = heightDrawStartIndex + 6;

                        if (!activeSquareRef.squareRef.chessman.IsWhite()) {
                            move_target = -1;
                            starting_row -= 5;
                        }

                        //Klikniecie pola bez pionka, w tej samej kolumnie
                        if (same_collumns && this.chessman == empty_square.squareRef.chessman) {
                            //Przy kliknieciu 2 pola dalej
                            if (move_dir == move_target * 2) {
                                //jesli pionek jest na starcie
                                if (activeSquareRef.squareRef.y / bitmapSize == starting_row) {
                                    MoveChessman(activeSquareRef, empty_square);
                                    return !whites_turn;
                                }
                            }
                            //Przy kliknieciu 1 pole dalej
                            else if (move_dir == move_target) {
                                MoveChessman(activeSquareRef, empty_square);
                                //Aktywacja promocji pionka przy dotarciu na koniec planszu
                                if (this_index_y == heightDrawStartIndex) {
                                    activeSquareRef.squareRef = this;
                                    PromoteWhite(ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex);
                                    return whites_turn;
                                } else if (this_index_y == heightDrawStartIndex + 7) {
                                    activeSquareRef.squareRef = this;
                                    PromoteBlack(ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex);
                                    return whites_turn;
                                }
                                return !whites_turn;
                            } else {
                                DeactivateSquare(activeSquareRef, empty_square);
                                return whites_turn;
                            }
                        }
                        //bicie figury na polu do przodu po skosie
                        else if (move_dir == move_target && adjectant_collumns && this.chessman != empty_square.squareRef.chessman && this.chessman.IsWhite() != activeSquareRef.squareRef.chessman.IsWhite()) {
                            MoveChessman(activeSquareRef, empty_square);
                            //Aktywacja promocji pionka przy dotarciu na koniec planszu
                            if (this_index_y == heightDrawStartIndex) {
                                activeSquareRef.squareRef = this;
                                PromoteWhite(ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex);
                                return whites_turn;
                            } else if (this_index_y == heightDrawStartIndex + 7) {
                                activeSquareRef.squareRef = this;
                                PromoteBlack(ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex);
                                return whites_turn;
                            }
                            return !whites_turn;
                        } else {
                            //deaktywacja aktywnego pola
                            DeactivateSquare(activeSquareRef, empty_square);
                            return whites_turn;
                        }
                    } else if (active_name == 'T') {
                        boolean same_collumns = (activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize) == 0;
                        boolean same_rows = (activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize) == 0;

                        //Jesli kliknieto na pole w tej samej kolumnie
                        if (same_collumns) {
                            int move_dir = 1;
                            int distance = activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize;
                            int active_index_y = activeSquareRef.squareRef.y / bitmapSize;

                            //Sprawdzam dystans miedzy aktywna wieza a kliknietym polem
                            //i przypisuje odpowiednia wartosc kierunku ruchu
                            if (distance > 0)
                                move_dir = -1;

                            //Jesli sa po drodze pionki to deaktywuj wieze
                            for (int i = active_index_y + move_dir; i != active_index_y - distance; i += move_dir) {
                                if (ALL_SQUARES_HITBOXES[this.x / bitmapSize][i].squareRef.chessman != empty_square.squareRef.chessman) {
                                    DeactivateSquare(activeSquareRef, empty_square);
                                    return whites_turn;
                                }
                            }
                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else if (same_rows) {
                            int move_dir = 1;
                            int distance = activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize;
                            int active_index_x = activeSquareRef.squareRef.x / bitmapSize;

                            //Sprawdzam dystans miedzy aktywna wieza a kliknietym polem
                            //i przypisuje odpowiednia wartosc kierunku ruchu
                            if (distance > 0)
                                move_dir = -1;

                            //Jesli sa po drodze pionki to deaktywuj wieze
                            for (int i = active_index_x + move_dir; i != active_index_x - distance; i += move_dir) {
                                if (ALL_SQUARES_HITBOXES[i][this.y / bitmapSize].squareRef.chessman != empty_square.squareRef.chessman) {
                                    DeactivateSquare(activeSquareRef, empty_square);
                                    return whites_turn;
                                }
                            }

                            //Jak wszystko gra to rzusz wieza
                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else {
                            DeactivateSquare(activeSquareRef, empty_square);
                            return whites_turn;
                        }
                    } else if (active_name == 'K') {
                        boolean same_collumns = (activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize) == 0;
                        boolean same_rows = (activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize) == 0;

                        int distance_x = activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize;
                        int distance_y = activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize;

                        if (!same_collumns && !same_rows && Math.abs(distance_x) + Math.abs(distance_y) == 3) {
                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else {
                            DeactivateSquare(activeSquareRef, empty_square);
                            return whites_turn;
                        }
                    } else if (active_name == 'B') {
                        int distance_x = activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize;
                        int distance_y = activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize;

                        if (Math.abs(distance_x) == Math.abs(distance_y)) {
                            int move_dir_x = 1;
                            int move_dir_y = 1;

                            int active_index_x = activeSquareRef.squareRef.x / bitmapSize;
                            int active_index_y = activeSquareRef.squareRef.y / bitmapSize;

                            if (distance_x > 0)
                                move_dir_x = -1;
                            if (distance_y > 0)
                                move_dir_y = -1;

                            for (int i = active_index_x + move_dir_x, j = active_index_y + move_dir_y; i != active_index_x - distance_x && j != active_index_y - distance_y; i += move_dir_x, j += move_dir_y) {
                                if (ALL_SQUARES_HITBOXES[i][j].squareRef.chessman != empty_square.squareRef.chessman) {
                                    DeactivateSquare(activeSquareRef, empty_square);
                                    return whites_turn;
                                }
                            }

                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else {
                            DeactivateSquare(activeSquareRef, empty_square);
                            return whites_turn;
                        }
                    } else if (active_name == 'Q') {
                        boolean same_collumns = (activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize) == 0;
                        boolean same_rows = (activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize) == 0;

                        int distance_x = activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize;
                        int distance_y = activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize;

                        //Jesli kliknieto na pole w tej samej kolumnie
                        if (same_collumns) {
                            int move_dir = 1;
                            int distance = activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize;
                            int active_index_y = activeSquareRef.squareRef.y / bitmapSize;

                            //Sprawdzam dystans miedzy aktywna krolowa a kliknietym polem
                            //i przypisuje odpowiednia wartosc kierunku ruchu
                            if (distance > 0)
                                move_dir = -1;

                            //Jesli sa po drodze pionki to deaktywuj krolowa
                            for (int i = active_index_y + move_dir; i != active_index_y - distance; i += move_dir) {
                                if (ALL_SQUARES_HITBOXES[this.x / bitmapSize][i].squareRef.chessman != empty_square.squareRef.chessman) {
                                    DeactivateSquare(activeSquareRef, empty_square);
                                    return whites_turn;
                                }
                            }

                            //Jak wszystko gra to rzusz krolowa
                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else if (same_rows) {
                            int move_dir = 1;
                            int distance = (activeSquareRef.squareRef.x - this.x) / bitmapSize;
                            int active_index_x = activeSquareRef.squareRef.x / bitmapSize;

                            //Sprawdzam dystans miedzy aktywna krolowa a kliknietym polem
                            //i przypisuje odpowiednia wartosc kierunku ruchu
                            if (distance > 0)
                                move_dir = -1;

                            //Jesli sa po drodze pionki to deaktywuj krolowa
                            for (int i = active_index_x + move_dir; i != active_index_x - distance; i += move_dir) {
                                if (ALL_SQUARES_HITBOXES[i][this.y / bitmapSize].squareRef.chessman != empty_square.squareRef.chessman) {
                                    DeactivateSquare(activeSquareRef, empty_square);
                                    return whites_turn;
                                }
                            }
                            //Jak wszystko gra to rzusz krolowa
                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else if (Math.abs(distance_x) == Math.abs(distance_y)) {
                            int move_dir_x = 1;
                            int move_dir_y = 1;

                            int active_index_x = activeSquareRef.squareRef.x / bitmapSize;
                            int active_index_y = activeSquareRef.squareRef.y / bitmapSize;

                            if (distance_x > 0)
                                move_dir_x = -1;
                            if (distance_y > 0)
                                move_dir_y = -1;

                            for (int i = active_index_x + move_dir_x, j = active_index_y + move_dir_y; i != active_index_x - distance_x && j != active_index_y - distance_y; i += move_dir_x, j += move_dir_y) {
                                if (ALL_SQUARES_HITBOXES[i][j].squareRef.chessman != empty_square.squareRef.chessman) {
                                    DeactivateSquare(activeSquareRef, empty_square);
                                    return whites_turn;
                                }
                            }

                            MoveChessman(activeSquareRef, empty_square);
                            return !whites_turn;
                        } else {
                            DeactivateSquare(activeSquareRef, empty_square);
                            return whites_turn;
                        }
                    } else if (active_name == 'R') {
                        int distance_x = activeSquareRef.squareRef.x / bitmapSize - this.x / bitmapSize;
                        int distance_y = activeSquareRef.squareRef.y / bitmapSize - this.y / bitmapSize;

                        int divider = 0;

                        //Przy ruchu w poziomie albo pionie divider jest rowny 1
                        // a przy ruchu po skosie divider jest rowny 2
                        if (distance_x != 0)
                            divider++;
                        if (distance_y != 0)
                            divider++;

                        //Jesli krol ma sie ruszyc o 1 pole w pionie, poziomie albo po skosie
                        if (Math.abs(distance_x) + Math.abs(distance_y) / divider == 1) {
                            //Symuluje ruch i sprawdzam czy krol jest zagrozony
                            Chessman temp = this.chessman;
                            this.ReceiveChessman(activeSquareRef.squareRef.chessman);
                            activeSquareRef.squareRef.EraseChessman(empty_square.squareRef.chessman);
                            if (!IsKingSafe(this, empty_square, ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex, bitmapSize)) {
                                //Cofam symulowany ruch
                                activeSquareRef.squareRef.ReceiveChessman(this.chessman);
                                this.ReceiveChessman(temp);
                                DeactivateSquare(activeSquareRef, empty_square);
                                return whites_turn;
                            }
                            //Cofam symulowany ruch
                            activeSquareRef.squareRef.ReceiveChessman(this.chessman);
                            this.ReceiveChessman(temp);

                            // Jesli nie ma zadnych zagrozen to krol moze isc
                            // Zmieniam wskaznik na krola
                            MoveChessman(activeSquareRef, empty_square);

                            if (whites_turn) whiteKingSquareRef.squareRef = this;
                            else blackKingSquareRef.squareRef = this;

                            return !whites_turn;
                        } else {
                            DeactivateSquare(activeSquareRef, empty_square);
                            return whites_turn;
                        }
                    }
                }
            }
            //Aktywuj odpowiedniego pionka
            else if (chessman.IsWhite() && whites_turn || !chessman.IsWhite() && !whites_turn && this.chessman != empty_square.squareRef.chessman) {
                Bitmap temp = activeSquareRef.squareRef.bitmap;
                activeSquareRef.squareRef = this;
                this.drawSquare = true;
                this.bitmap = temp;
                return whites_turn;
            }
        }
        //Przy kliknieciu poza plansze gdy promuje sie pionka
        else if (activeSquareRef.squareRef.chessman.GetName() == 'P') {
            if ((this.chessman.IsWhite() && activeSquareRef.squareRef.chessman.IsWhite() && activeSquareRef.squareRef.y / bitmapSize == heightDrawStartIndex) || (!this.chessman.IsWhite() && !activeSquareRef.squareRef.chessman.IsWhite() && activeSquareRef.squareRef.y / bitmapSize == heightDrawStartIndex + 7)) {
                activeSquareRef.squareRef.ReceiveChessman(this.chessman);
                EndPromotion(ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex);
                DeactivateSquare(activeSquareRef, empty_square);
                return !whites_turn;
            }
        } else {
            DeactivateSquare(activeSquareRef, empty_square);
            return whites_turn;
        }
        return whites_turn;
    }

    private boolean IsKingSafe(Square kings_square, SquareReference empty_square, SquareReference[][] ALL_SQUARES_HITBOXES, int heightDrawStartIndex, int widthDrawStartIndex, int bitmapSize) {
        int this_index_x = kings_square.x / bitmapSize;
        int this_index_y = kings_square.y / bitmapSize;

        //Sprawdzam czy krol nie bedzie w zasiegu przeciwnego krola
        if (ALL_SQUARES_HITBOXES[this_index_x][this_index_y - 1].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x][this_index_y - 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x][this_index_y + 1].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x][this_index_y + 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y - 1].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y - 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y + 1].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y + 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y - 1].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y - 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y + 1].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y + 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }
        if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y].squareRef.chessman.GetName() == 'R' && ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
            return false;
        }

        //Przeciwnych pionkow
        if (kings_square.chessman.IsWhite()) {
            if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y - 1].squareRef.chessman.GetName() == 'P' && !ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y - 1].squareRef.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y - 1].squareRef.chessman.GetName() == 'P' && !ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y - 1].squareRef.chessman.IsWhite()) {
                return false;
            }
        } else {
            if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y + 1].squareRef.chessman.GetName() == 'P' && ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y + 1].squareRef.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y + 1].squareRef.chessman.GetName() == 'P' && ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y + 1].squareRef.chessman.IsWhite()) {
                return false;
            }
        }

        //Sprawdzam czy pierwsza figura ktora krol zobaczy powyzej od niego nie jest krolowa albo wieza
        for (int i = this_index_y - 1; i >= heightDrawStartIndex; i--) {
            if ((ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.GetName() == 'T' || ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.GetName() != 'e') {
                //Jesli zobaczy inna figure to moze isc
                break;
            }
        }
        //To samo ponizej krola
        for (int i = this_index_y + 1; i <= heightDrawStartIndex + 7; i++) {
            if ((ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.GetName() == 'T' || ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[this_index_x][i].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }
        //Po lewej
        for (int i = this_index_x - 1; i >= widthDrawStartIndex; i--) {
            if ((ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.GetName() == 'T' || ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }
        // I prawej od niego
        for (int i = this_index_x + 1; i <= widthDrawStartIndex + 7; i++) {
            if ((ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.GetName() == 'T' || ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[i][this_index_y].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }

        //Podobnie po skosach dla gonca i krolowej
        //prawy dol
        for (int i = this_index_x + 1, j = this_index_y + 1; i <= widthDrawStartIndex + 7 && j <= heightDrawStartIndex + 7; i++, j++) {
            if ((ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'B' || ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }
        //lewy dol
        for (int i = this_index_x - 1, j = this_index_y; i >= widthDrawStartIndex && j <= heightDrawStartIndex + 7; i--, j++) {
            if ((ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'B' || ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }
        //lewa gora
        for (int i = this_index_x - 1, j = this_index_y - 1; i >= widthDrawStartIndex && j >= heightDrawStartIndex; i--, j--) {
            if ((ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'B' || ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }
        //prawa gora
        for (int i = this_index_x + 1, j = this_index_y - 1; i <= widthDrawStartIndex + 7 && j >= heightDrawStartIndex; i++, j--) {
            if ((ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'B' || ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() == 'Q') && ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            } else if (ALL_SQUARES_HITBOXES[i][j].squareRef.chessman.GetName() != 'e') {
                break;
            }
        }

        //Konie
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x - 2][this_index_y - 1].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x - 2][this_index_y - 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y - 2].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y - 2].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y - 2].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y - 2].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x + 2][this_index_y - 1].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x + 2][this_index_y - 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x + 2][this_index_y + 1].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x + 2][this_index_y + 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y + 2].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x + 1][this_index_y + 2].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y + 2].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x - 1][this_index_y + 2].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}
        try {
            if (ALL_SQUARES_HITBOXES[this_index_x - 2][this_index_y + 1].squareRef.chessman.GetName() == 'K' && ALL_SQUARES_HITBOXES[this_index_x - 2][this_index_y + 1].squareRef.chessman.IsWhite() != kings_square.chessman.IsWhite()) {
                return false;
            }
        }catch (Exception e){}

        return true;
    }

    public void ReceiveChessman(Chessman chessman)
    {
        this.chessman = chessman;
        this.drawChessman = true;
    }

    private void EraseChessman(Chessman emptyChessman)
    {
        this.chessman = emptyChessman;
        this.drawChessman = false;
    }

    private void MoveChessman(SquareReference activeSquareRef, SquareReference empty_square)
    {
        this.chessman = activeSquareRef.squareRef.chessman;
        this.drawSquare = true;
        this.drawChessman = true;

        activeSquareRef.squareRef.chessman = empty_square.squareRef.chessman;
        activeSquareRef.squareRef.drawSquare = true;
        activeSquareRef.squareRef = empty_square.squareRef;
    }

    private void DeactivateSquare(SquareReference activeSquareRef, SquareReference empty_square)
    {
        activeSquareRef.squareRef = empty_square.squareRef;
    }

    private boolean IsKingSafeAfterMove(SquareReference activeSquareRef, SquareReference empty_square, SquareReference king_square, SquareReference[][] ALL_SQUARES_HITBOXES, int heightDrawStartIndex, int widthDrawStartIndex, int bitmapSize)
    {
        //Symuluje ruch i sprawdzam czy krol jest zagrozony
        boolean result = true;
        Chessman temp = this.chessman;
        this.chessman = activeSquareRef.squareRef.chessman;
        activeSquareRef.squareRef.chessman = empty_square.squareRef.chessman;
        if (!IsKingSafe(king_square.squareRef, empty_square, ALL_SQUARES_HITBOXES, heightDrawStartIndex, widthDrawStartIndex, bitmapSize))
        {
            result = false;
        }
        //Cofam symulowany ruch
        activeSquareRef.squareRef.chessman = this.chessman;
        this.chessman = temp;

        return result;
    }

    //Aktywuje kwadraty pokazujace biale pionki do promocji
    private void PromoteWhite(SquareReference[][] ALL_SQUARES_HITBOXES, int heightDrawStartIndex, int widthDrawStartIndex)
    {
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 2].squareRef.drawSquare = true;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 3].squareRef.drawSquare = true;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 4].squareRef.drawSquare = true;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 5].squareRef.drawSquare = true;
    }

    //Aktywuje kwadraty pokazujace czarne pionki do promocji
    private void PromoteBlack(SquareReference[][] ALL_SQUARES_HITBOXES, int heightDrawStartIndex, int widthDrawStartIndex)
    {
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 2].squareRef.drawSquare = true;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 3].squareRef.drawSquare = true;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 4].squareRef.drawSquare = true;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 5].squareRef.drawSquare = true;
    }

    public static void EndPromotion(SquareReference[][] ALL_SQUARES_HITBOXES, int heightDrawStartIndex, int widthDrawStartIndex)
    {
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 2].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 3].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 4].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 3][heightDrawStartIndex + 5].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 2].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 3].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 4].squareRef.drawSquare = false;
        ALL_SQUARES_HITBOXES[widthDrawStartIndex - 2][heightDrawStartIndex + 5].squareRef.drawSquare = false;
    }

    public void setDrawSquare(boolean drawSquare)
    {
        this.drawSquare = drawSquare;
    }

}

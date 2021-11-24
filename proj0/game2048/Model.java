package game2048;

import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: SHRUTI PAI
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */

    /** TILT HELPER returns true if tile is in top row */
    public boolean topRow(int col, int row) {
        if (row == 3) {
            return true;
        }
        return false;
    }

    /** TILT HELPER returns true if a tile (not in top row) has no tiles above it */
    public boolean emptyAbove(int col, int row) {
        if (this.board.tile(col, 3) == null) {
            return true;
        }
        return false;
    }

    /** TILT HELPER returns true if there is only one tile above*/
    public boolean oneAbove(int col, int row) {
        if (this.board.tile(col, 3) == null) {
            return false;
        } else {
            if (row == 2) {
                return true;
            } else if (row == 1 & this.board.tile(col,2) == null){
                return true;
            } else if (row == 0 & this.board.tile(col,1) == null & this.board.tile(col,2) == null) {
                return true;
            }
        }
        return false;
    }





    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        this.board.startViewingFrom(side);


        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.


        /** iterates through each row and column, moving up and to the right */

        for (int c = 0; c <= 3; c += 1) {
            int merges = 0;
            for (int r = 3; r >= 0; r -= 1) {
                Tile t = this.board.tile(c, r);

                /** if no tile, skip */
                if (t == null) {
                    continue;
                }

                /** if tile is in top row, nothing happens */
                if (topRow(c, r)) {
                    continue;


                /** if tile has no tile above it, move tile to top row */
                } else if (emptyAbove(c, r)) {
                    this.board.move(c, 3, t);
                    changed = true;

                /** if tile has one tile above it, and that tile has not merged already, then merge */
                } else if (oneAbove(c,r)) {
                    Tile above = this.board.tile(c,3);
                    if (t.value() == above.value() & merges == 0) {
                        this.board.move(c,3,t);
                        this.score += t.value() + t.value();
                        merges += 1;
                        changed = true;
                    } else {
                        this.board.move(c,2,t);
                        changed = true;
                    }

                /** tile in row 1 trying to merge with tile in row 2 */
                } else if (r == 1) {
                    Tile above = this.board.tile(c,2);
                    if (t.value() == above.value()) {
                        this.board.move(c,2,t);
                        this.score += t.value() + t.value();
                        merges += 1;
                        changed = true;

                    /** if value is not the same, tile doesn't move */
                    } else {
                        continue;
                    }

                /** tile in row 0 trying to merge with tile in row 2 */
                } else if (r == 0 & this.board.tile(c,1) == null) {
                    Tile above = this.board.tile(c,2);
                    /** if value is the same and tile in row 2 has not merged already */
                    if (t.value() == above.value() & merges < 2) {
                        this.board.move(c,2,t);
                        this.score += t.value() + t.value();
                        merges += 1;
                        changed = true;
                    /** if tile in row 2 has merged already, tile moves to row 1 instead */
                    } else {
                        this.board.move(c,1,t);
                        changed = true;
                    }

                /** tile in row 0 trying to merge with tile in row 1 */
                } else if (r == 0 & this.board.tile(c,1) != null) {
                    Tile above = this.board.tile(c,1);
                    if (t.value() == above.value()) {
                        this.board.move(c,1,t);
                        this.score += t.value() + t.value();
                        merges += 1;
                        changed = true;
                    } else {
                        continue;
                    }
                }
            }
        }


        this.board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i <= 3; i += 1) {
            for (int j = 0; j <= 3; j += 1) {
                if (b.tile(i, j) == null) {
                    return true;
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (int i = 0; i <= 3; i += 1) {
            for (int j = 0; j <= 3; j += 1) {
                if (b.tile(i, j) == null) {
                    continue;
                } else if (b.tile(i,j).value() == MAX_PIECE){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        if (emptySpaceExists(b)) {
            return true;
        }

        for (int i = 0; i <= 3; i +=1) {
            for (int j = 0; j <= 2; j += 1) {
                if (b.tile(i,j) == null) {
                    continue;
                } else if (b.tile(i,j).value() == b.tile(i,j+1).value()){
                    return true;
                }
            }
        }

        for (int i = 0; i <=2; i +=1){
            for (int j = 0; j <= 3; j += 1) {
                if (b.tile(i,j) == null) {
                    continue;
                } else if (b.tile(i,j).value() == b.tile(i+1,j).value()){
                    return true;
                }
            }
        }

        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}

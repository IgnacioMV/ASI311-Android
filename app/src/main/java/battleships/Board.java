package battleships;

import java.util.ArrayList;
import java.util.List;

import battleships.ship.AbstractShip;

import static battleships.ColorUtil.Color.BLUE;
import static battleships.ColorUtil.Color.GREEN;
import static battleships.ColorUtil.Color.RED;
import static battleships.ColorUtil.Color.RESET;
import static battleships.ColorUtil.Color.YELLOW;

public class Board implements IBoard {

    private static final String HIT_LABEL = ColorUtil.colorize("X", RED);
    private static final String MISS_LABEL = ColorUtil.colorize("X", RESET);
    private static final String NO_HIT_LABEL = ColorUtil.colorize(".", GREEN);

    private static final String NO_SHIP_LABEL = ColorUtil.colorize("~", BLUE);
    private static final ColorUtil.Color COORD_COLORS = YELLOW;

    private int size;
    private String name;
    private ShipState[][] ships;
    private Boolean[][] hits;

    public Board(String name, int size) {
        this.size = size;
        this.name = name;

        this.ships = new ShipState[size][size];
        this.hits = new Boolean[size][size];
    }

    public Board(String name) {
        this(name, 10);
    }

    public void print() {
        Character currentLetter = 'A';
        String currentShipLabel;
        String currentHitLabel;

        clearConsole();
        System.out.println(this.name);

        System.out.print("   ");
        for (int i = 0; i < this.size; ++i) {
            System.out.print(ColorUtil.colorize(currentLetter++, COORD_COLORS) + " ");
        }

        currentLetter = 'A';
        System.out.print("    ");
        for (int i = 0; i < this.size; ++i) {
            System.out.print(ColorUtil.colorize(currentLetter++, COORD_COLORS) + " ");
        }

        System.out.println();

        for (int j = 0; j < this.size; ++j) {
            System.out.print(ColorUtil.colorize(String.format("%2d ", j + 1), COORD_COLORS));
            for (int i = 0; i < this.size; ++i) {
                currentShipLabel = NO_SHIP_LABEL;

                if (this.ships[i][j] != null) {
                    currentShipLabel = this.ships[i][j].print();
                }
                System.out.print(currentShipLabel + " ");
            }
            System.out.print(" ");
            System.out.print(ColorUtil.colorize(String.format("%2d ", j + 1), COORD_COLORS));
            for (int i = 0; i < this.size; ++i) {
                currentHitLabel = NO_HIT_LABEL;

                if (this.hits[i][j] != null) {
                    currentHitLabel = this.hits[i][j] ? HIT_LABEL : MISS_LABEL;
                }
                System.out.print(currentHitLabel + " ");
            }
            System.out.println();
        }
    }

    private void clearConsole() {
        final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";
        System.out.print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
    }

    @Override
    public Hit sendHit(int x, int y) {
        System.out.println("sendHit");
        ShipState state;
        state = ships[x][y];
        System.out.println(hits[x][y]);

        if (ships[x][y] == null) {
            return Hit.MISS;
        } else {
            if (state.isStruck()) {
                return Hit.ALREADY_STRIKE;
            } else {
                state.addStrike();
                if (state.isSunk()) {
                    return Hit.fromInt(state.getShip().getLength());
                } else {
                    return Hit.STIKE;
                }
            }
        }
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void putShip(AbstractShip ship, int x, int y) throws ShipException {
        AbstractShip.Orientation o = ship.getOrientation();
        if (o == null) {
            throw new ShipException("no orientation provided");
        }
        int dx = 0, dy = 0;
        if (o == AbstractShip.Orientation.EAST) {
            if (x + ship.getLength() > this.size) {
                throw new ShipException("ship is out of the grid.");
            }
            dx = 1;
        } else if (o == AbstractShip.Orientation.SOUTH) {
            if (y + ship.getLength() > this.size) {
                throw new ShipException("ship is out of the grid.");
            }
            dy = 1;
        } else if (o == AbstractShip.Orientation.NORTH) {
            if (y + 1 - ship.getLength() < 0) {
                throw new ShipException("ship is out of the grid.");
            }
            dy = -1;
        } else if (o == AbstractShip.Orientation.WEST) {
            if (x + 1 - ship.getLength() < 0) {
                throw new ShipException("ship is out of the grid.");
            }
            dx = -1;
        }

        int ix = x;
        int iy = y;

        for (int i = 0; i < ship.getLength(); ++i) {
            if (hasShip(ix, iy)) {
                throw new ShipException("Ship overlays.");
            }
            ix += dx;
            iy += dy;
        }

        for (int i = -1; i < 2; i++) {
            if (o == AbstractShip.Orientation.EAST || o == AbstractShip.Orientation.WEST) {
                ix = x-dx;
                iy = y+i;
            }
            else {
                ix = x+i;
                iy = y-dy;
            }
            for (int j = -1; j <= ship.getLength(); ++j) {
                if (ix > -1 && iy > -1 && ix < 10 && iy < 10) {
                    if (hasShip(ix, iy)) {
                        throw new ShipException("Ship overlays.");
                    }
                }
                ix += dx;
                iy += dy;
            }
        }

        ix = x;
        iy = y;

        for (int i = 0; i < ship.getLength(); ++i) {
            this.ships[ix][iy] = new ShipState(ship);
            ix += dx;
            iy += dy;
        }
    }

    @Override
    public boolean hasShip(int x, int y) {
        if (x > this.size || y > this.size) {
            throw new IllegalArgumentException("out of the grid.");
        }
        return this.ships[x][y] != null && !this.ships[x][y].isStruck();
    }

    @Override
    public void setHit(Boolean hit, int x, int y) {
        if (x > this.size || y > this.size) {
            throw new IllegalArgumentException("out of the grid.");
        }
        this.hits[x][y] = hit;
    }

    @Override
    public Boolean getHit(int x, int y) {
        if (x > this.size || y > this.size) {
            throw new IllegalArgumentException("out of the grid.");
        }
        return this.hits[x][y];
    }

    @Override
    public List<int[]> surroundShipWithMiss(IBoard board, int x, int y) {
        List<int[]> coordinates = new ArrayList<int[]>();

        ShipState state = ships[x][y];
        if (state == null) {
            System.out.println("ShipState is null");
            return coordinates;
        }
        AbstractShip ship = ships[x][y].getShip();
        if (!ship.isSunk()) {
            return coordinates;
        }
        System.out.println("surrounding sunk ship with misses");
        AbstractShip.Orientation o = ship.getOrientation();
        int dx = 0, dy = 0;
        if (o == AbstractShip.Orientation.EAST) {
            dx = 1;
        } else if (o == AbstractShip.Orientation.SOUTH) {
            dy = 1;
        } else if (o == AbstractShip.Orientation.NORTH) {
            dy = -1;
        } else if (o == AbstractShip.Orientation.WEST) {
            dx = -1;
        }
        int ix, iy, startX, startY;
        startX = x;
        startY = y;
        while (ships[startX][startY] != null) {
            startX -= dx;
            startY -= dy;
            if (startX < 0 || startX > 9 || startY < 0 || startY > 9) {
                break;
            }
        }

        startX += dx;
        startY += dy;

        for (int i = -1; i < 2; i++) {
            if (o == AbstractShip.Orientation.EAST || o == AbstractShip.Orientation.WEST) {
                ix = startX-dx;
                iy = startY+i;
            }
            else {
                ix = startX+i;
                iy = startY-dy;
            }
            for (int j = -1; j <= ship.getLength(); ++j) {
                System.out.println("marking ("+Integer.toString(ix)+","+Integer.toString(iy)+")");
                if (ix > -1 && iy > -1 && ix < 10 && iy < 10) {
                    Hit hit = sendHit(ix, iy);
                    boolean strike = hit != Hit.MISS;
                    board.setHit(strike, ix, iy);
                    if (!strike) {
                        coordinates.add(new int[]{ix, iy});
                    }
                }
                ix += dx;
                iy += dy;
            }
        }
        return coordinates;
    }
}

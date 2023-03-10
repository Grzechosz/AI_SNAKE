import java.io.FileNotFoundException;
import java.io.PrintWriter;

import objs.Apple;
import objs.Piece;
import objs.Snake;

public class SaveAsFile {

    private final String DESTINATION = "data";
    private final String LIFE_INFO = "life_info";
    public static int map[][];
    private PrintWriter data;

    public SaveAsFile() {
        SaveAsFile.map = new int[Game.WINDOW_SIZE.height / Game.SECTOR_SIZE][Game.WINDOW_SIZE.width / Game.SECTOR_SIZE];
    }

    public void save() {
        try {
            this.data = new PrintWriter(DESTINATION);
        } catch (FileNotFoundException ex) {
            ex.getStackTrace();
            System.exit(0);
        }
        for (int m[] : map) {
            for (int i : m) {
                if (i == 3) {
                    data.print(1 + " ");
                } else if (i == 2) {
                    data.print(0.67 + " ");
                } else if (i == 1) {
                    data.print(0.33 + " ");
                } else {
                    data.print(0 + " ");
                }
            }
            data.println();
        }
        this.data.close();
    }

    public void snakeAndAppleSave(Apple apple, Snake snake) {
        this.zero();
        for (int i = 0; i < snake.getPieces().size(); i++) {
            if (i == 0) {
                map[snake.getPieces().get(i).y / Piece.SIZE][snake.getPieces().get(i).x / Piece.SIZE] = 2;
            } else {
                map[snake.getPieces().get(i).y / Piece.SIZE][snake.getPieces().get(i).x / Piece.SIZE] = 1;
            }
        }
        map[apple.y / Apple.SIZE][apple.x / Apple.SIZE] = 3;
    }

    public void zero() {
        for (int i = 0; i < SaveAsFile.map.length; i++) {
            for (int j = 0; j < SaveAsFile.map[0].length; j++) {
                SaveAsFile.map[i][j] = 0;
            }
        }
    }

    public void gameover() {
        try {
            this.data = new PrintWriter(LIFE_INFO);
        } catch (FileNotFoundException ex) {
            ex.getStackTrace();
            System.exit(0);
        }
        data.print(0);
        this.data.close();
    }
}

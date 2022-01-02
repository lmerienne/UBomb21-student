package fr.ubx.poo.ubomb.game;
import java.io.*;
import java.util.Scanner;

public class GridRepoFile extends GridRepo{

    public GridRepoFile(Game game) {
        super(game);
    }

    @Override
    public final Grid load(int level, String name) throws FileNotFoundException {
        name = name + String.valueOf(level);
        name = name + ".txt";
        System.out.println(name);
        File file = new File(name);
        System.out.println(file);
        Scanner scan = new Scanner(file);
        String fileContent = "";
        int height = 0;
        int width = 0;
        while(scan.hasNextLine()){
            fileContent = scan.nextLine();
            width = fileContent.length();
            height += 1;
        }
        scan.close();
        Grid grid = new Grid(width, height);

        Scanner scan2 = new Scanner(file);
        String fileContent2 = "";
        EntityCode[][] level2;
        int j = 0;
        while(scan2.hasNextLine()){
            fileContent2 = scan2.nextLine();
            for (int i = 0; i < width; i++) {
                char c = fileContent2.charAt(i);
                System.out.println(c);
                Position position = new Position(i, j);
                EntityCode entityCode = EntityCode.fromCode(c);
                grid.set(position, processEntityCode(entityCode, position));

            }
            j++;
        }
        scan2.close();

        return grid;
    }
}
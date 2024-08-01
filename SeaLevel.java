
import java.util.*;
import java.util.LinkedList;
public class SeaLevel {
  public static boolean[][] floodedRegionsIn(double[][] terrain,
      GridLocation[] sources,
      double height) {


    boolean[][] output = new boolean[terrain.length][terrain[0].length];

    Queue<GridLocation> flooded = new Queue<GridLocation>();

    // add initial water source locations to the flooded queue
    for (GridLocation g : sources) {
      int row = g.row;
      int col = g.col;
      // if the water source location is below or at height, flood it
      if (terrain[row][col] <= height) {
        output[row][col] = true;
        flooded.enqueue(g);
      }
    }


    while (!flooded.isEmpty()) {
      GridLocation g = flooded.dequeue();

      int row = g.row;
      int col = g.col;
      int rowMax = terrain.length;
      int colMax = terrain[0].length;

      //North
      if(isValid(row +1, col, rowMax, colMax)) {
        if(terrain[row+1][col] <= height && !output[row+1][col]) {
          output[row+1][col] = true;
          flooded.enqueue(new GridLocation(row+1,col));
        }
      }

      //South
      if(isValid(row -1, col, rowMax, colMax)) {
        if(terrain[row-1][col] <= height && !output[row-1][col]) {
          output[row-1][col] = true;
          flooded.enqueue(new GridLocation(row-1,col));
        }
      }

      //East
      if(isValid(row, col+1, rowMax, colMax)) {
        if(terrain[row][col+1] <= height && !output[row][col+1]) {
          output[row][col+1] = true;
          flooded.enqueue(new GridLocation(row,col+1));
        }
      }


      //West
      if(isValid(row, col-1, rowMax, colMax)) {
        if(terrain[row][col-1] <= height && !output[row][col-1]) {
          output[row][col-1] = true;
          flooded.enqueue(new GridLocation(row, col-1));
        }
      }
    }

    return output;
  }

  public static boolean isValid(int row, int col, int rowMax, int colMax) {
    if (row < 0 || col < 0 || row > rowMax - 1 || col > colMax - 1) {
      return false;
    }
    return true;
  }
}




//Code below is not entirely my own, code above is my own and it works
/*

    Set<GridLocation> output = new HashSet<>();
    LinkedList<GridLocation> flooded = new LinkedList<>();


    for (GridLocation g : sources) {
      int row = g.row;
      int col = g.col;

      if (terrain[row][col] <= height) {
        output.add(g);
        flooded.add(g);
      }
    }

    int[] dx = {0, 0, 1, -1};
    int[] dy = {1, -1, 0, 0};

    while (!flooded.isEmpty()) {
      GridLocation g = flooded.remove();
      int row = g.row;
      int col = g.col;

      for (int i=0; i < 4; i++) {
        int newRow = row + dx[i];
        int newCol = col + dy[i];

        if (isValid(newRow, newCol, terrain.length, terrain[0].length) && terrain[newRow][newCol] <= height && !output.contains(new GridLocation(newRow, newCol))) {
          output.add(new GridLocation(newRow, newCol));
          flooded.add(new GridLocation(newRow, newCol));
        }
      }
    }
    return output;
  }

  public static boolean isValid(int row, int col, int rowMax, int colMax) {
    return row >= 0 && col >= 0 && row < rowMax && col < colMax;
  }

  static class GridLocation {
    int row;
    int col;

    GridLocation(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GridLocation that = (GridLocation) o;
        return row == that.row &&
                col == that.col;
    }


    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    */
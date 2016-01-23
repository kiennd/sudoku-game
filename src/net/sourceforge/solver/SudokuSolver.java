// Copyright 2011 Hakan Kjellerstrand hakank@bonetmail.com
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.sourceforge.solver;

import com.google.ortools.constraintsolver.DecisionBuilder;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.Solver;

public class SudokuSolver {

  static {
    System.loadLibrary("jniortools");
  }


  /**
   *
   * Solves a Sudoku problem.
   *
   */
  
  public static IntVar[][] grid;
  public static Solver solve(int [][] initial_grid, int cell_size) {

    Solver solver = new Solver("Sudoku");
   
//    int cell_size = 5;
    int n = cell_size * cell_size;

    // 0 marks an unknown value

    //
    // variables
    //
    grid = new IntVar[n][n];
    IntVar[] grid_flat = new IntVar[n * n];

    for(int i = 0; i < n; i++) {
      for(int j = 0; j < n; j++) {
        grid[i][j] = solver.makeIntVar(1, n, "grid[" + i +"," + j + "]");
        grid_flat[i * n + j] = grid[i][j];
      }
    }

    //
    // constraints
    //

    // init and rows
    for(int i = 0; i < n; i++) {
      IntVar[] row = new IntVar[n];
      for(int j = 0; j < n; j++) {
        if (initial_grid[i][j] > 0) {
          solver.addConstraint(
              solver.makeEquality(grid[i][j], initial_grid[i][j]));
        }
        row[j] = grid[i][j];
      }
      solver.addConstraint(solver.makeAllDifferent(row));
    }

    // columns
    for(int j = 0; j < n; j++) {
      IntVar[] col = new IntVar[n];
      for(int i = 0; i < n; i++) {
        col[i] = grid[i][j];
      }
      solver.addConstraint(solver.makeAllDifferent(col));
    }

    // cells
    for(int i = 0; i < cell_size; i++) {
      for(int j = 0; j < cell_size; j++) {
        IntVar[] cell = new IntVar[n];
        for(int di = 0; di < cell_size; di++) {
          for(int dj = 0; dj < cell_size; dj++) {
            cell[di * cell_size + dj] =
              grid[i * cell_size + di][j * cell_size + dj];
          }
        }
        solver.addConstraint(solver.makeAllDifferent(cell));
      }
    }

    //
    // Search
    //
    
    DecisionBuilder db = solver.makePhase(grid_flat,
                                          solver.INT_VAR_SIMPLE,
                                          solver.INT_VALUE_SIMPLE);

    solver.newSearch(db);

    while (solver.nextSolution()) {
      for(int i = 0; i < n; i++) {
        for(int j = 0; j < n; j++) {
          System.out.print(grid[i][j].value() + " ");
        }
        System.out.println();
      }
      System.out.println();
    }
    solver.endSearch();

    // Statistics
    System.out.println();
    System.out.println("Solutions: " + solver.solutions());
    System.out.println("Failures: " + solver.failures());
    System.out.println("Branches: " + solver.branches());
    System.out.println("Wall time: " + solver.wallTime() + "ms");

    return solver;
    
  }

  public static void main(String[] args) throws Exception {
    int[][] initial_grid = new int[][] {
		{0,2,0,0,0,3,14,0,8,0,0,0,0,0,0,0,0,13,4,24,0,7,1,0,0},
		{0,10,17,0,0,0,6,18,0,0,22,16,0,12,0,0,0,0,1,0,0,0,13,19,0},
		{0,15,24,13,7,0,0,0,4,0,10,0,0,3,14,0,18,0,0,0,0,22,2,6,0},
		{0,0,1,21,0,0,15,0,22,0,0,19,13,0,0,0,8,0,0,0,0,16,18,20,0},
		{0,5,0,0,20,7,25,19,0,0,0,21,17,18,2,10,12,22,9,15,11,0,0,0,0},
		{11,0,0,0,22,8,0,24,7,1,5,0,0,0,13,16,17,25,23,2,4,0,6,0,19},
		{16,9,12,0,17,0,19,22,0,0,0,0,18,21,0,0,20,6,13,0,7,0,0,23,11},
		{0,0,6,0,21,9,16,0,3,0,0,22,20,19,0,0,0,0,15,8,25,0,0,0,0},
		{0,0,23,5,0,2,0,0,11,17,8,0,0,0,16,12,9,0,0,21,0,3,10,0,0},
		{0,0,0,0,0,6,0,0,12,0,9,1,25,0,3,0,11,0,0,7,0,0,21,0,0},
		{0,0,9,0,0,23,0,5,17,4,16,0,11,0,22,18,2,0,21,13,0,0,7,0,0},
		{4,6,0,0,5,0,0,2,0,0,0,18,21,24,0,0,19,3,0,12,23,0,0,17,0},
		{0,0,0,12,11,0,7,3,0,24,17,20,15,13,19,1,0,5,8,0,6,9,0,0,0},
		{0,22,0,0,14,19,0,6,16,0,0,8,9,7,0,0,0,24,0,0,3,0,0,1,18},
		{0,0,21,0,0,25,13,0,20,8,12,0,14,0,10,9,16,15,0,6,0,0,4,0,0},
		{0,0,25,0,0,24,0,0,18,0,4,0,3,10,5,0,1,0,0,14,0,0,0,0,0},
		{0,0,5,3,0,17,0,0,23,7,13,0,0,0,18,19,21,0,0,22,0,11,12,0,0},
		{0,0,0,0,18,10,8,0,0,0,0,25,23,2,0,0,5,0,16,11,9,0,3,0,0},
		{17,20,0,0,2,0,22,16,6,0,0,7,12,0,0,0,0,9,3,0,18,0,23,24,25},
		{6,0,4,0,16,1,11,12,25,3,19,0,0,0,21,17,23,8,0,18,2,0,0,0,14},
		{0,0,0,0,4,14,24,11,19,23,21,17,16,8,0,0,0,1,2,9,13,0,0,5,0},
		{0,1,14,23,0,0,0,0,9,0,0,0,19,5,0,0,24,0,12,0,0,8,17,0,0},
		{0,16,11,8,0,0,0,0,1,0,6,4,0,0,23,0,15,0,0,0,14,12,9,10,0},
		{0,21,3,0,0,0,17,0,0,0,0,15,0,25,20,0,0,4,10,0,0,0,16,11,0},
		{0,0,20,2,0,16,5,8,0,0,0,0,0,0,0,0,6,0,19,25,0,0,0,3,0}
		};

    SudokuSolver.solve(initial_grid,5);
  }
}
package IR;

import IR.extra_credit.Solution;
import IR.phase2.Phase2;
import IR.phase3.Phase3;
import IR.task1.DirCheck;
import IR.task1.Phase1;

import java.io.IOException;
import java.util.Scanner;

/**
 * Phase1 function for Info retrieval system
 */
public class Main {
    public static void main(String[] args){

        DirCheck dr = new DirCheck();
        while (true) {
            System.out.println("Welcome to use our information retrieval system!");
            System.out.println(" 1. Indexing and Retrieval");
            System.out.println(" 2. Displaying Results");
            System.out.println(" 3. Evaluation");
            System.out.println(" 4. Extra_credit");
            System.out.println(" q. Exit");
            Scanner scanner = new Scanner( System.in );
            String command = scanner.nextLine();
            if (command.equals("1")) {
                try {
                    Phase1.main();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (command.equals("2")) {
                try {
                    Phase2.main();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (command.equals("3")) {
                try {
                    Phase3.main();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (command.equals("4")) {
                Solution aa = new Solution();
                aa.main();
            }else if (command.equals("q")) {
                break;
            }
        }
    }
}
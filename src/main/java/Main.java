import examples.commGhosts.POCommGhosts;
import pacman.Executor;
//import examples.poPacMan.POPacMan;
import entrants.pacman.username.*;

/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {
    public static void main(String[] args) {

        Executor executor = new Executor(false, true);

        executor.runGameTimed(new MyPacMan(), new POCommGhosts(50), true);
    }
}